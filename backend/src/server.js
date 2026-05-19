require('dotenv').config();
const express = require('express');
const mongoose = require('mongoose');
const cors = require('cors');
const helmet = require('helmet');
const morgan = require('morgan');
const rateLimit = require('express-rate-limit');

const authRoutes = require('./routes/auth');
const recordRoutes = require('./routes/records');

const app = express();
app.use(helmet());
app.use(cors());
app.use(express.json({ limit: '2mb' }));
app.use(morgan('tiny'));
app.use('/api/', rateLimit({ windowMs: 60_000, max: 120 }));

app.get('/', (_req, res) => res.json({ ok: true, service: 'south-intel-archive' }));
app.use('/api/auth', authRoutes);
app.use('/api/records', recordRoutes);

app.use((err, _req, res, _next) => {
  console.error(err);
  res.status(err.status || 500).json({ error: err.message || 'Server error' });
});

const PORT = process.env.PORT || 4000;
mongoose
  .connect(process.env.MONGODB_URI)
  .then(() => {
    console.log('✔ MongoDB connected');
    app.listen(PORT, () => console.log(`✔ API on :${PORT}`));
  })
  .catch((e) => {
    console.error('✘ Mongo connection failed:', e.message);
    process.exit(1);
  });
