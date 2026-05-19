const router = require('express').Router();
const auth = require('../middleware/auth');
const Record = require('../models/Record');

// List + optional ?since=<ISO> for incremental sync
router.get('/', auth, async (req, res) => {
  const { since, q } = req.query;
  const filter = { userId: req.userId };
  if (since) filter.clientUpdatedAt = { $gt: new Date(since) };
  let query = Record.find(filter).sort({ clientUpdatedAt: -1 }).limit(1000);
  if (q) query = Record.find({ ...filter, $text: { $search: q } }).limit(200);
  const docs = await query.lean();
  res.json({ records: docs, serverTime: new Date().toISOString() });
});

router.get('/:id', auth, async (req, res) => {
  const doc = await Record.findOne({ _id: req.params.id, userId: req.userId }).lean();
  if (!doc) return res.status(404).json({ error: 'Not found' });
  res.json(doc);
});

// Upsert (used by sync). Body: { records: [...] } — last-write-wins by clientUpdatedAt
router.post('/sync', auth, async (req, res) => {
  const incoming = Array.isArray(req.body.records) ? req.body.records : [];
  const ops = incoming.map((r) => ({
    updateOne: {
      filter: {
        _id: r._id,
        userId: req.userId,
        $or: [
          { clientUpdatedAt: { $lt: new Date(r.clientUpdatedAt) } },
          { clientUpdatedAt: { $exists: false } },
        ],
      },
      update: { $set: { ...r, userId: req.userId, clientUpdatedAt: new Date(r.clientUpdatedAt) } },
      upsert: true,
    },
  }));
  if (ops.length) {
    try { await Record.bulkWrite(ops, { ordered: false }); }
    catch (e) { /* ignore upsert race conflicts */ }
  }
  res.json({ ok: true, accepted: ops.length, serverTime: new Date().toISOString() });
});

router.post('/', auth, async (req, res) => {
  const r = req.body;
  if (!r._id || !r.clientUpdatedAt) return res.status(400).json({ error: '_id & clientUpdatedAt required' });
  await Record.updateOne({ _id: r._id, userId: req.userId },
    { $set: { ...r, userId: req.userId, clientUpdatedAt: new Date(r.clientUpdatedAt) } },
    { upsert: true });
  res.json({ ok: true });
});

router.delete('/:id', auth, async (req, res) => {
  await Record.updateOne(
    { _id: req.params.id, userId: req.userId },
    { $set: { deleted: true, clientUpdatedAt: new Date() } }
  );
  res.json({ ok: true });
});

module.exports = router;
