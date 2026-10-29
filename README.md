# Sembi Amir Student API

Spring Boot CRUD API backed by PostgreSQL for the DevOps midterm.
Author: Sembi Amir. Group: IT2-2307. Student ID: 37693.

Documentation status: Sembi Amir Docker and Jenkins guide

## Run

Copy `.env.example` to `.env`, set a random DB_PASSWORD, then run:

```bash
docker compose up --build -d
docker compose ps
curl http://localhost:8093/
```

API: http://localhost:8093/api/students
Personalized Docker image: sembi-amir-student-api:v1.0-37693.
Jenkins job: Sembi_Amir_Student_API.
Java 21 and the included Gradle 8.14.3 wrapper build the application.
PostgreSQL is accessed through the Compose service name `db`.

## Tests

```bash
./gradlew test
```

Automated HTTP tests use an isolated H2 database in PostgreSQL compatibility mode.
The demonstration additionally tests the running API against real PostgreSQL.

## Secrets

Never commit `.env`, passwords or database credentials: Git history and public
clones retain them even after file deletion. Use local environment variables
and Jenkins Credentials for any private-repository credentials. The public
GitHub checkout does not need a credential.

## HTTP endpoints

| Method | Endpoint | Success | Error examples |
| --- | --- | --- | --- |
| POST | /api/students | 201 and Location header | 400 invalid body, 409 duplicate studentNumber |
| GET | /api/students | 200 array | |
| GET | /api/students/{id} | 200 student | 404 missing |
| PUT | /api/students/{id} | 200 updated student | 400 invalid body, 404 missing, 409 duplicate |
| DELETE | /api/students/{id} | 204 | 404 missing |

Fields: fullName, groupName, studentNumber (digits), email.

## Midterm demonstration

- [Commands and submission checklist in Russian](docs/DEMONSTRATION_RU.md)
- [Defense questions and answers in Russian](docs/DEFENSE_RU.md)
- [Official references](docs/REFERENCES.md)

Run `bash scripts/linux_evidence.sh` for the personalized Linux report and backup.
Run `bash scripts/api_demo.sh` for real PostgreSQL CRUD and persistence checks.
The API demo removes only its previous studentNumber 37693 demo record, creates
new test data, recreates this project's containers, verifies persistence and
deletes the record. Other student records remain in the database.

## CRUD feature by Sembi Amir

PUT `/api/students/{id}` updates all validated student fields, returns 200 for an
existing ID, 400 for invalid fields, 404 for a missing ID and 409 for a duplicate
student number. An automated lifecycle test verifies the stored update.
