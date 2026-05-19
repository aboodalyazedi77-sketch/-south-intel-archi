# أرشيف الاستخبارات الجنوبية — South Intelligence Archive

تطبيق أندرويد عربي (RTL) لأرشفة السجلات بشكل آمن، يعمل دون اتصال بالإنترنت، مع مزامنة سحابية تلقائية عبر MongoDB Atlas.

## 🧭 المحتويات
- `android/` — مشروع Android Studio (Kotlin, MVVM, Room, Hilt, Retrofit, Compose)
- `backend/` — خادم Node.js + Express + Mongoose (JWT auth + REST API)
- `.github/workflows/android.yml` — CI لبناء APK تلقائياً
- `docs/` — تعليمات النشر والاستخدام

## ⚡ بدء سريع

### 1) الخادم (Backend)
```bash
cd backend
cp .env.example .env       # ضع MONGODB_URI و JWT_SECRET
npm install
npm run seed               # (اختياري) بيانات تجريبية
npm start                  # يعمل على http://localhost:4000
```

### 2) التطبيق (Android)
1. افتح `android/` في Android Studio (Hedgehog أو أحدث).
2. عدّل `API_BASE_URL` في `app/build.gradle.kts` ليشير إلى عنوان خادمك.
3. `Run` على جهازك، أو شغّل `./gradlew assembleDebug` لإنتاج APK.

### 3) APK تلقائي عبر CI
ادفع الكود إلى GitHub — سيُولِّد GitHub Actions ملف APK في تبويب **Actions → Artifacts**.

## 🔐 الحسابات
- تسجيل / دخول عبر بريد + كلمة مرور.
- كل سجل مُقيَّد بمستخدمه.
- المزامنة: «آخر تعديل يفوز» (last-write-wins) بالاعتماد على `updatedAt`.

## 📁 الحقول
الاسم • الكنية • الجنسية • المحافظة • السكن • العمل • العمر • المؤهل • الانتماء • المعلومات • الملاحظات • تاريخ الإنشاء • آخر تحديث

تفاصيل كاملة: [docs/SETUP.md](docs/SETUP.md)
