# مرجع الـ API

عنوان أساسي: `${API_BASE_URL}/api`

## المصادقة
كل طلبات السجلات تتطلب: `Authorization: Bearer <token>`.

### POST /auth/register
```json
{ "email": "x@y.z", "password": "secret123", "displayName": "اسم" }
```
استجابة: `{ token, user: { id, email, displayName } }`

### POST /auth/login
```json
{ "email": "x@y.z", "password": "secret123" }
```

## السجلات

### GET /records?since=<ISO>&q=<text>
يعيد السجلات الخاصة بالمستخدم. مع `since` يعيد فقط ما تغيّر بعد ذلك (للمزامنة).

استجابة:
```json
{ "records": [ { "_id": "...", "name": "...", "clientUpdatedAt": "...", ... } ],
  "serverTime": "2026-05-17T..." }
```

### POST /records
حفظ سجل واحد (upsert).

### POST /records/sync
دفع مجموعة:
```json
{ "records": [ { "_id": "...", "name": "...", "clientUpdatedAt": "2026-05-17T10:00:00.000Z" } ] }
```
الخادم يقبل التحديث فقط إذا كان `clientUpdatedAt` الواصل أحدث من المخزّن.

### DELETE /records/:id
حذف ناعم (`deleted = true`).
