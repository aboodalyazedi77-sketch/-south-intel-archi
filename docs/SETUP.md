# دليل التثبيت والنشر

## 1) إعداد MongoDB Atlas
1. أنشئ حساباً مجانياً على [MongoDB Atlas](https://www.mongodb.com/atlas).
2. أنشئ **Cluster** مجاني (M0).
3. **Database Access** → أضف مستخدماً (username + password).
4. **Network Access** → أضف `0.0.0.0/0` (أو IP خادمك).
5. **Connect → Drivers → Node.js** → انسخ URI (سيكون شيء مثل
   `mongodb+srv://USER:PASS@cluster0.xxxxx.mongodb.net/?retryWrites=true&w=majority`).
6. ضع هذا الـ URI في ملف `backend/.env` تحت `MONGODB_URI` وأضف اسم قاعدة بيانات في النهاية مثل `/south_archive`.

## 2) تشغيل الخادم محلياً
```bash
cd backend
cp .env.example .env
# عدّل MONGODB_URI و JWT_SECRET
npm install
npm run seed     # يُنشئ مستخدم admin@south.local / admin123 + سجلات تجريبية
npm start        # http://localhost:4000
```

## 3) نشر الخادم (مجاناً)
أي من هذه الخدمات يعمل بدون تعديل:
- **Render**: New → Web Service → اربط GitHub → اختر `backend/` → Runtime: Node 20 → Build: `npm install` → Start: `npm start`. أضف `MONGODB_URI` و `JWT_SECRET` كـ Environment Variables.
- **Railway / Fly.io / Cyclic**: نفس الفكرة.

بعد النشر ستحصل على رابط مثل `https://south-archive-api.onrender.com/`.

## 4) إعداد التطبيق
1. افتح ملف `android/app/build.gradle.kts`.
2. غيّر السطر:
   ```kotlin
   buildConfigField("String", "API_BASE_URL", "\"https://your-backend.example.com/\"")
   ```
   إلى رابط خادمك (لا تنسَ الشرطة المائلة `/` في النهاية).
3. إن جربت محلياً مع المحاكي استخدم `http://10.0.2.2:4000/` (هذا موجود في وضع debug تلقائياً).

> ملاحظة: للسماح بـ HTTP في الإنتاج أضف `android:usesCleartextTraffic="true"` في `<application>`. الإنتاج يجب أن يستخدم HTTPS فقط.

## 5) البناء
### عبر Android Studio
- افتح `android/` → **Build → Build Bundle(s) / APK(s) → Build APK(s)**.
- ستجد الملف في `android/app/build/outputs/apk/debug/app-debug.apk`.

### عبر سطر الأوامر
```bash
cd android
./gradlew assembleDebug
# أو للنسخة الموقعة (Release):
./gradlew assembleRelease
```

### عبر GitHub Actions (تلقائي)
- ادفع الكود إلى GitHub.
- اذهب إلى **Actions → Build Android APK** → افتح آخر run → **Artifacts** → نزّل `south-intel-archive-debug-apk`.

## 6) التثبيت على الجهاز
- فعّل "مصادر غير معروفة" في إعدادات الأمان.
- انقل الـ APK إلى الهاتف وثبّته.
- سجّل بالحساب التجريبي: `admin@south.local` / `admin123` (إن استخدمت بذرة البيانات).

## 7) كيف تعمل المزامنة
- جميع العمليات (إضافة / تعديل / حذف) تُحفظ محلياً أولاً مع علم `pendingSync = true`.
- WorkManager يشغّل `SyncWorker` كل ساعتين + عند توفر الإنترنت + يدوياً من الإعدادات.
- المزامنة تسحب من الخادم منذ `updatedAt` المحلي الأكبر، ثم تدفع التغييرات المعلّقة.
- تعارض السجلات يُحَل بـ **آخر تعديل يفوز** (`clientUpdatedAt` على الخادم).

## 8) النسخ الاحتياطي المحلي
- زر **إنشاء نسخة احتياطية** يكتب `archive-backup.db` في
  `Android/data/com.southintel.archive/files/Documents/`.
- زر **استعادة من النسخة** يستبدل قاعدة البيانات. أعد فتح التطبيق بعدها.

## 9) الحقول المخزّنة
| العربية | English (DB) |
|---|---|
| الاسم | name |
| الكنية | kunya |
| الجنسية | nationality |
| المحافظة | governorate |
| السكن | residence |
| العمل | work |
| العمر | age |
| المؤهل | qualification |
| الانتماء | affiliation |
| المعلومات | information |
| الملاحظات | notes |
| تاريخ الإنشاء | createdAt |
| آخر تحديث | updatedAt |
