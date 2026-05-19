require('dotenv').config();
const mongoose = require('mongoose');
const bcrypt = require('bcryptjs');
const { randomUUID } = require('crypto');
const User = require('./models/User');
const Record = require('./models/Record');

const sample = [
  { name: 'محمد علي عبدالله', kunya: 'أبو علي', nationality: 'يمني', governorate: 'عدن', residence: 'كريتر', work: 'مهندس', age: 34, qualification: 'بكالوريوس', affiliation: 'مستقل', information: 'خبرة في الاتصالات', notes: '' },
  { name: 'سالم أحمد ناصر', kunya: 'أبو أحمد', nationality: 'يمني', governorate: 'حضرموت', residence: 'المكلا', work: 'موظف حكومي', age: 41, qualification: 'دبلوم', affiliation: 'حزبي', information: 'نشاط في المجتمع المحلي', notes: 'تحت المتابعة' },
  { name: 'فاطمة عبدالرحمن', kunya: '', nationality: 'يمنية', governorate: 'لحج', residence: 'الحوطة', work: 'معلمة', age: 29, qualification: 'بكالوريوس تربية', affiliation: 'مستقل', information: '', notes: '' },
  { name: 'خالد منصور', kunya: 'أبو منصور', nationality: 'يمني', governorate: 'أبين', residence: 'زنجبار', work: 'تاجر', age: 52, qualification: 'ثانوي', affiliation: 'مستقل', information: 'يملك محلات تجارية', notes: '' },
  { name: 'عبدالله سعيد', kunya: 'أبو سعيد', nationality: 'يمني', governorate: 'الضالع', residence: 'الضالع', work: 'طالب', age: 22, qualification: 'ثانوي', affiliation: 'طلابي', information: '', notes: '' },
];

(async () => {
  await mongoose.connect(process.env.MONGODB_URI);
  console.log('Connected.');
  const email = 'admin@south.local';
  let user = await User.findOne({ email });
  if (!user) {
    user = await User.create({
      email,
      passwordHash: await bcrypt.hash('admin123', 10),
      displayName: 'المسؤول',
    });
    console.log('Created user:', email, '/ admin123');
  }
  await Record.deleteMany({ userId: user._id });
  const now = new Date();
  await Record.insertMany(
    sample.map((s) => ({ _id: randomUUID(), userId: user._id, ...s, clientUpdatedAt: now }))
  );
  console.log('Seeded', sample.length, 'records.');
  await mongoose.disconnect();
})();
