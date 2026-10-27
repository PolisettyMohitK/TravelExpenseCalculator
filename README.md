# Trip Cost Planner

Estimate the cost of a trip and keep a record of its main expenses.

- **Frontend:** React (Vite) + Axios
- **Backend:** Java 21, Spring Boot REST API
- **Database:** H2 file DB locally, PostgreSQL in production

## Features

1. Add destination, number of days and number of travellers
2. Enter travel, accommodation and food expenses
3. Total trip cost = travel + accommodation + food
4. Cost per traveller = total ÷ travellers (rounded to 2 decimals)
5. List of saved trips with their expense summaries

Plus: register / log in, trip details page with expense breakdown, edit and delete trips. Each user only sees their own trips.

Validation (enforced on both client and server): days and travellers must be greater than 0, expenses cannot be negative.

## Project structure

```
backend/src/main/java/com/travel/expense/
├── controller/   AuthController, TripController (REST endpoints)
├── service/      AuthService, TripService (business logic + cost calculation)
├── model/        User, Trip (JPA entities)
├── repository/   UserRepository, TripRepository
├── dto/          request / response records with validation
├── config/       WebConfig (CORS), AuthInterceptor (token check)
└── exception/    ApiException, GlobalExceptionHandler

frontend/src/
├── App.jsx       login gate + tab switching
├── pages/        AuthPage, TripsPage, AddTripPage, TripDetailsPage
├── components/   Navbar, TripForm, TripCard, Field
├── context/      AuthProvider (login state)
├── api/          Axios client, auth and trip calls
└── utils/        cost calculation, validation, formatting
```

## API

| Method | Path                 | Auth | Description                       |
|--------|----------------------|------|-----------------------------------|
| POST   | `/api/auth/register` | –    | Create account, returns token     |
| POST   | `/api/auth/login`    | –    | Log in, returns token             |
| POST   | `/api/auth/logout`   | ✓    | Invalidate token                  |
| GET    | `/api/trips`         | ✓    | List your trips, newest first     |
| POST   | `/api/trips`         | ✓    | Save a trip, returns totals (201) |
| GET    | `/api/trips/{id}`    | ✓    | Trip details                      |
| PUT    | `/api/trips/{id}`    | ✓    | Update a trip                     |
| DELETE | `/api/trips/{id}`    | ✓    | Delete a trip (204)               |

Authenticated requests send `Authorization: Bearer <token>`. Passwords are stored as BCrypt hashes.

Invalid input returns `400` with a field → message map:

```json
{ "message": "Please fix the highlighted fields", "errors": { "days": "Days must be greater than 0" } }
```

## Run locally

Backend (port 8080):

```bash
cd backend && mvn spring-boot:run
```

Frontend (port 5173):

```bash
cd frontend && npm install && npm run dev
```

Tests:

```bash
cd backend && mvn test
```

## Deploy

### 1. Push to GitHub

Create a repository and push this folder.

### 2. Backend + database on Render

1. In Render, choose **New → Blueprint** and select the repository. `render.yaml` creates a free PostgreSQL database and the `trip-api` Docker service, and wires the DB credentials automatically.
2. When asked for `APP_CORS_ORIGINS`, enter your Cloudflare Pages URL (you can set it after step 3 and redeploy).
3. Note the service URL, e.g. `https://trip-api.onrender.com`.

### 3. Frontend on Cloudflare Pages

1. In Cloudflare, go to **Workers & Pages → Create → Pages → Connect to Git** and pick the repository.
2. Settings:
   - Framework preset: **Vite** (or none)
   - Root directory: `frontend`
   - Build command: `npm run build`
   - Output directory: `dist`
   - Environment variable: `VITE_API_URL=https://trip-api.onrender.com/api`
3. Deploy, then copy the `*.pages.dev` URL into `APP_CORS_ORIGINS` on Render.

> Render's free tier sleeps after inactivity, so the first request can take ~30 seconds.
