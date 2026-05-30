# Coffee Shop

## Стек

| Слой | Технологии |
|------|-----------|
| Backend | Kotlin 2.3, Spring Boot 4.0.4, Spring MVC, Spring Security, Spring Data JPA |
| База данных | H2 (dev) / PostgreSQL 16 (prod), Redis 7 (кэш + сессии) |
| Инфраструктура | Docker: nginx + app + PostgreSQL + Redis |
| Веб-панель | Thymeleaf + HTML5/CSS3 + JavaScript (AJAX) |
| Авторизация | Firebase Phone Auth (OTP) + JWT (REST API) |
| Оплата | Tbank Acquiring |
| Push-уведомления | Firebase Cloud Messaging (FCM) |
| Рекомендации | FastAPI микросервис (async, OkHttp) |
| Хранение файлов | Docker named volume `uploads`, nginx отдаёт статику |

## Запуск

```bash
# Локальный запуск (dev — H2 in-memory, Redis на localhost:6379)
./gradlew bootRun

# Продакшн: Docker (PostgreSQL + Redis + nginx + app)
docker compose up --build
```

**Dev-окружение:**
- H2 console: `http://localhost:8080/h2-console` (JDBC URL: `jdbc:h2:mem:coffeeshop`)
- Swagger UI: `http://localhost:8080/swagger-ui.html`
- Uploads: `http://localhost:8080/uploads/**`

## Архитектура

```
Android App  ──►  nginx (:80)  ──►  Spring Boot (:8080)
                                         │
                              ┌──────────┴──────────┐
                         PostgreSQL              Redis
```

### Безопасность — две цепочки

| Цепочка | Матчер | Авторизация | CSRF | Сессия |
|---------|--------|-------------|------|--------|
| REST API | `/api/**` | JWT Bearer (stateless) | Отключён | Нет |
| Admin | `/admin/**` | Form login, роль ADMIN | Включён | Redis |

### Жизненный цикл заказа

```
PENDING → (оплата Tbank) → PAID → PREPARING → READY → COMPLETED
               ↘ CANCELLED             ↘ CANCELLED
```

## API

OpenAPI: `GET /api/docs` | Swagger UI: `/swagger-ui.html`

### Публичные эндпоинты (без JWT)

| Метод | Путь | Описание |
|-------|------|----------|
| `GET` | `/api/menu` | Меню, сгруппированное по категориям |
| `GET` | `/api/menu/items/{id}` | Позиция с объёмами и модификаторами |
| `GET` | `/api/menu/search?q=` | Поиск по названию |
| `GET` | `/api/modifiers` | Все модификаторы |
| `GET` | `/api/shop/status` | Принимает ли кофейня заказы |
| `POST` | `/api/auth/firebase/verify` | Firebase ID Token → JWT-пара |
| `POST` | `/api/auth/firebase/register` | Регистрация нового пользователя |

## Переменные окружения

| Переменная | Описание |
|-----------|----------|
| `JWT_SECRET` | HS256-секрет (≥ 256 бит) |
| `FIREBASE_SERVICE_ACCOUNT_BASE64` | Firebase Admin SDK JSON (Base64) |
| `TBANK_TERMINAL_KEY` / `TBANK_SECRET_KEY` | Tbank Acquiring |
| `FASTAPI_URL` | URL микросервиса рекомендаций |
| `UPLOADS_DIR` / `UPLOADS_BASE_URL` | Пути хранения файлов |
