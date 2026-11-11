# Library Microservices

A small library-management system built as microservices: users register and
log in, browse and manage books, and borrow and return them. Requests go
through an API gateway that checks JWTs, and services find each other through
a Eureka discovery server.

## Architecture

```
                         ┌──────────────────────┐
  client ──HTTP :8080──▶ │   gateway-service    │  Spring Cloud Gateway
                         │  verifies JWT (HS256)│  (routes via Eureka, lb://)
                         └──────────┬───────────┘
              /api/users/**         │ /api/books/** (JWT)   /api/loans/** (JWT)
          ┌─────────────────────────┼────────────────────────┐
          ▼                         ▼                        ▼
  ┌───────────────┐        ┌───────────────┐        ┌───────────────┐
  │ user-service  │        │ book-service  │        │ loan-service  │
  │ Django + DRF  │        │ Django + DRF  │        │ Spring Boot   │
  │ issues JWTs   │        │               │        │ JPA           │
  └──────┬────────┘        └──────┬────────┘        └──────┬────────┘
         ▼                        ▼                        ▼
   postgres-user            postgres-book            postgres-loan
     (user_db)                (book_db)                (loan_db)

        All services register with discovery-service (Eureka, :8761)
```

| Service             | Stack                         | Container port | Host port |
|---------------------|-------------------------------|----------------|-----------|
| `discovery-service` | Spring Boot, Eureka server    | 8761           | 8761      |
| `gateway-service`   | Spring Cloud Gateway, jjwt    | 8080           | 8080      |
| `user-service`      | Django, DRF, SimpleJWT        | see compose    | 8001      |
| `book-service`      | Django, DRF                   | see compose    | 8002      |
| `loan-service`      | Spring Boot, Spring Data JPA  | 8083           | 8083      |
| `postgres-user`     | PostgreSQL 15 (`user_db`)     | 5432           | 5433      |
| `postgres-book`     | PostgreSQL 15 (`book_db`)     | 5432           | 5434      |
| `postgres-loan`     | PostgreSQL 15 (`loan_db`)     | 5432           | 5435      |

- **One database per service.** Each service owns its own PostgreSQL
  container and schema; services never share tables.
- **Authentication.** `user-service` signs access tokens with HS256 using
  `JWT_SECRET`. The gateway verifies them with the same `JWT_SECRET` on the
  `/api/books/**` and `/api/loans/**` routes, then forwards `X-User-Id` and
  `X-User-Role` headers. `/api/users/**` (register and login) is not filtered.

## Running with Docker Compose

Prerequisites: Docker with the Compose plugin.

1. Create your local environment file. `.env` is git-ignored, so never commit it:

   ```bash
   cp .env.example .env
   ```

2. Replace every value in `.env`. For the two keys, use random values:

   ```bash
   openssl rand -base64 48   # use the output for JWT_SECRET, run again for DJANGO_SECRET_KEY
   ```

3. Check the configuration, then start everything:

   ```bash
   docker compose config --quiet   # fails with a clear message if a variable is missing
   docker compose up --build
   ```

4. The gateway is at <http://localhost:8080> and the Eureka dashboard at
   <http://localhost:8761>.

> **Changing `DB_PASSWORD` on an existing setup:** the Postgres image only
> applies `POSTGRES_PASSWORD` when a data volume is first created. If the
> `*_db_data` volumes already exist, either change the password inside each
> database (`ALTER USER postgres WITH PASSWORD '...'`) or, **if you can lose
> the data**, recreate the volumes with `docker compose down -v`.

## Environment variables

All secrets come from the environment. None of them has a default, and every
service refuses to start if one it needs is missing.

| Variable            | Used by                                                         | Notes |
|---------------------|-----------------------------------------------------------------|-------|
| `DB_PASSWORD`       | all 3 Postgres containers, `user-service`, `book-service`, `loan-service` | Password of the `postgres` DB user. One value shared by every database. |
| `JWT_SECRET`        | `gateway-service` (verify), `user-service` (sign)               | **At least 32 bytes (256 bits).** Must be identical in both services. |
| `DJANGO_SECRET_KEY` | `user-service`, `book-service`                                   | Django `SECRET_KEY`. |

### Why `JWT_SECRET` needs at least 32 bytes

The gateway builds its key with jjwt 0.11.5's `Keys.hmacShaKeyFor(...)`, which
throws `WeakKeyException` for any key shorter than 256 bits (32 bytes, counted
after UTF-8 encoding), as RFC 7518 requires for HS256. The gateway's filter
turns that exception into `401 Unauthorized`, so a short key would make **every**
protected request fail, including ones with valid tokens. `user-service`
checks the length at startup and refuses to start with a shorter key.
`openssl rand -base64 48` gives a 64-character key.

### Non-secret settings

The Django services also read `DB_NAME`, `DB_USER`, `DB_HOST` and `DB_PORT`, and
`loan-service` reads `SPRING_DATASOURCE_URL` and `SPRING_DATASOURCE_USERNAME`.
`docker-compose.yml` already sets these, so you don't put them in `.env`.

## Running tests

The Spring Boot modules have no parent POM, so run Maven in each module:

```bash
(cd discovery-service && mvn -q clean test)
(cd gateway-service   && mvn -q clean test)   # test supplies its own dummy JWT_SECRET
(cd loan-service      && mvn -q clean test)   # needs a reachable postgres-loan DB and DB_PASSWORD
```
