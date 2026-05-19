const mongoose = require('mongoose');

const RecordSchema = new mongoose.Schema(
  {
    _id: { type: String }, // UUID from client to make sync trivial
    userId: { type: mongoose.Schema.Types.ObjectId, ref: 'User', required: true, index: true },
    name: { type: String, default: '', index: 'text' },
    kunya: { type: String, default: '' },          // الكنية
    nationality: { type: String, default: '' },    // الجنسية
    governorate: { type: String, default: '', index: true }, // المحافظة
    residence: { type: String, default: '' },      // السكن
    work: { type: String, default: '' },           // العمل
    age: { type: Number, default: null },          // العمر
    qualification: { type: String, default: '' },  // المؤهل
    affiliation: { type: String, default: '', index: true }, // الانتماء
    information: { type: String, default: '' },    // المعلومات
    notes: { type: String, default: '' },          // الملاحظات
    deleted: { type: Boolean, default: false, index: true },
    clientUpdatedAt: { type: Date, required: true, index: true },
  },
  { timestamps: true, _id: false }
);

RecordSchema.index({
  name: 'text', kunya: 'text', nationality: 'text', governorate: 'text',
  residence: 'text', work: 'text', qualification: 'text', affiliation: 'text',
  information: 'text', notes: 'text'
}, { default_language: 'none' });

module.exports = mongoose.model('Record', RecordSchema);
