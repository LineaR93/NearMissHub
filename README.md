# NearMissHub — Final Exam (Spring Boot REST API)

NearMissHub is a **Spring Boot REST API** to report and manage **near‑miss incidents**.

The project is designed to be evaluated **without database seeding**: everything can be reproduced via the included **Postman collection**.

---

## Key features

- **JWT authentication** (stateless)
- **Role-based access control**: `REPORTER` / `TRIAGER` / `VALIDATOR`
- **Near‑miss report lifecycle** with a **strict linear workflow**
- **Assignments**, **comments**, **attachments**, **status history**
- **KPI endpoints** (simple aggregations)
- **Centralized JSON error handling** (consistent error format)

---

## Tech stack

- **Java 21**
- **Spring Boot** (Web, Security, Validation, Data JPA)
- **PostgreSQL**
- **JWT** (jjwt)
- **Cloudinary** (uploads: profile images + report attachments)
- **Mailgun** (email notifications)

---

## Domain model (entities)

Main entities/tables:
- `User` (with `Role`)
- `Category` (with `CategoryType`)
- `Report` (base entity)
- `NearMissReport` (extends `Report` via **JPA JOINED inheritance** → 2 tables: `reports` + `near_miss_reports`)
- `Assignment` (report → validator)
- `Comment` (report → comments)
- `Attachment` (report → uploaded files)
- `ReportStatusHistory` (audit trail of status transitions)

---

## Roles & permissions (high level)

### REPORTER
- Creates reports
- Reads **only** reports they created (ownership rule)
- Submits a report (`DRAFT → SUBMITTED`)
- Can add comments/attachments on accessible reports

### TRIAGER
- Can access **all** reports
- Creates categories
- Sets/changes a report category
- Assigns a `VALIDATOR` to a report

### VALIDATOR
- Can access **all** reports
- Changes report status (`SUBMITTED → IN_REVIEW → COMPLETED`)
- Can list users and change roles
- Can access KPI endpoints

**Object-level access**  
- `REPORTER` → only own reports  
- `TRIAGER` + `VALIDATOR` → all reports

---

## Report workflow (strictly linear)

Statuses:
1. `DRAFT` (created)
2. `SUBMITTED` (submitted by REPORTER)
3. `IN_REVIEW` (started by VALIDATOR)
4. `COMPLETED` (completed by VALIDATOR)

Allowed transitions:
- `DRAFT → SUBMITTED` only via `POST /reports/{id}/submit`
- `SUBMITTED → IN_REVIEW` via `PATCH /reports/{id}/status`
- `IN_REVIEW → COMPLETED` via `PATCH /reports/{id}/status`

Any non-linear transition returns **400 Bad Request**.

---

## Error format (consistent JSON)

All errors return the same JSON structure:

```json
{
  "timestamp": "2026-01-04T19:44:44.0585286",
  "status": 403,
  "error": "Forbidden",
  "message": "You do not have permission to perform this action",
  "details": null
}
```

Validation errors (`400`) include `details[]` with field-level messages.

---

## Local setup

### Prerequisites
- Java **21**
- Maven
- PostgreSQL running locally

### Database
Default values are in `src/main/resources/application.properties`:

- `spring.datasource.url=jdbc:postgresql://localhost:5432/nearmisshub`
- `spring.datasource.username=postgres`
- `spring.datasource.password=postgres`
- `spring.jpa.hibernate.ddl-auto=update`

Create the database:

```sql
CREATE DATABASE nearmisshub;
```

### Environment variables (optional overrides)

The application includes fallback defaults. For a clean setup you can override with env vars:

**JWT**
- `JWT_SECRET`

**Cloudinary**
- `CLOUDINARY_NAME`
- `CLOUDINARY_KEY`
- `CLOUDINARY_SECRET`

**Mailgun**
- `MAILGUN_API_KEY`
- `MAILGUN_DOMAIN`
- `MAILGUN_FROM`

---

## Run

```bash
mvn spring-boot:run
```

Base URL (default):
- `http://localhost:8080`

---

## Authentication

### Register (public)
`POST /users`

```json
{
  "name": "Mario",
  "surname": "Rossi",
  "email": "mario.rossi@example.com",
  "password": "Passw0rd!"
}
```

### Bootstrap rule (no seeding)
- The **first** user ever created in the DB becomes **VALIDATOR**
- All subsequent users become **REPORTER**

### Login
`POST /auth/login`

```json
{ "email": "mario.rossi@example.com", "password": "Passw0rd!" }
```

Response:
```json
{ "token": "<JWT>" }
```

Use:
`Authorization: Bearer <JWT>`

---

## API endpoints

### Meta (enums)
- `GET /meta/report-areas`
- `GET /meta/risk-levels`
- `GET /meta/roles`
- `GET /meta/report-statuses`

### Users (admin — VALIDATOR only)
- `GET /users` — list users
- `PATCH /users/{id}/role` — update role
```json
{ "role": "TRIAGER" }
```

### Me
- `GET /me`
- `PATCH /me` — update profile fields
- `PATCH /me/password`
- `PATCH /me/profile-image` — set profile image URL directly
```json
{ "profileImageUrl": "https://example.com/avatar.png" }
```
- `POST /me/profile-image` — upload profile image (multipart/form-data: `file`)

### Categories
- `GET /categories`
- `POST /categories` — create category (**TRIAGER** or **VALIDATOR**)
```json
{ "name": "Safety", "type": "SAFETY", "description": "Workplace safety hazards" }
```

### Reports
- `POST /reports` — create a near-miss report (authenticated)
```json
{
  "title": "Forklift near miss",
  "description": "Almost hit a pallet while reversing.",
  "area": "WAREHOUSE",
  "location": "Warehouse A - Bay 3",
  "riskLevel": "MEDIUM",
  "categoryId": "PUT_CATEGORY_UUID_HERE"
}
```

- `GET /reports` — list reports (filters + sorting)
  - Filters:
    - `status=DRAFT|SUBMITTED|IN_REVIEW|COMPLETED`
    - `categoryId=<uuid>`
  - Sorting:
    - `sortBy=createdAt|status|title|area|location|riskLevel`
    - `sortDir=asc|desc`
  - Ownership rule applies automatically (REPORTER sees only own reports)

- `GET /reports/{id}` — report details

### Workflow
- `POST /reports/{id}/submit` — `DRAFT → SUBMITTED` (**REPORTER creator only**)

- `PATCH /reports/{id}/status` — `SUBMITTED → IN_REVIEW → COMPLETED` (**VALIDATOR only**)
```json
{ "toStatus": "IN_REVIEW", "note": "Starting review" }
```

- `PATCH /reports/{id}/category` — set category (**TRIAGER** or **VALIDATOR**)
```json
{ "categoryId": "PUT_CATEGORY_UUID_HERE" }
```

### Assignment
- `PUT /reports/{id}/assignment` — assign a validator (**TRIAGER/VALIDATOR**)
```json
{ "assignedToUserId": "PUT_VALIDATOR_UUID_HERE", "note": "Assigned for validation" }
```

- `GET /reports/{id}/assignment` — get current assignment

### Comments
- `POST /reports/{id}/comments`
```json
{ "text": "I saw the pallet was unstable." }
```

- `GET /reports/{id}/comments`

### Attachments (Cloudinary)
- `POST /reports/{id}/attachments` — upload file (multipart/form-data: `file`)
- `GET /reports/{id}/attachments`
- `DELETE /reports/{reportId}/attachments/{attachmentId}`

### Status history
- `GET /reports/{id}/status-history`

### KPI (VALIDATOR only)
- `GET /kpi/reports/by-status`
- `GET /kpi/reports/by-category` (counts by category name)

---

## Postman (recommended evaluation)

File included in project root:
- **NearMissHub - Exam Collection.postman_collection.json**

Collection variables include:
- `baseUrl` (default `http://localhost:8080`)
- credentials for 5 users
- token variables (`t_u1` ... `t_u5`)
- created IDs (categories, reports, attachments)

Run folders in order:
1. **00 Setup (Users, Roles, Tokens)**
2. **01 Categories, Area, Risk, Role**
3. **02 Me (Profile, Password)**
4. **03 Reports (Create & Read)**
5. **04 Workflow (Submit, Assign, Status, History)**
6. **[MANUAL - To select file]** *(multipart uploads requiring local file selection)*
7. **05 Attachments (List/Delete)**
8. **06 KPI**
9. **[CHECKING AUTH]** *(expected 401/403 demonstrations)*

---

## External integrations

### Cloudinary
Used for:
- profile image upload (`POST /me/profile-image`)
- report attachment upload (`POST /reports/{id}/attachments`)

### Mailgun
Used for notifications triggered by:
- report assignment
- status changes

---

## Notes
- No seed scripts: everything is reproducible via Postman.
