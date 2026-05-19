# Backend — South Intelligence Archive

Node.js + Express + Mongoose + JWT.

## التشغيل
```bash
cp .env.example .env
# عدّل MONGODB_URI و JWT_SECRET
npm install
npm run seed
npm start
```

## النقاط
- `POST /api/auth/register`
- `POST /api/auth/login`
- `GET  /api/records?since=ISO&q=text`
- `POST /api/records` (upsert واحد)
- `POST /api/records/sync` (دفع مجموعة)
- `DELETE /api/records/:id`

التفاصيل في `../docs/API.md`.
