# Sembi Amir Student API

Student management API for the DevOps midterm. It uses Spring Boot, PostgreSQL
and Gradle, with Docker Compose for the application and database and Jenkins
for the build pipeline.

Sembi Amir · IT2-2307 · Student ID 37693

## Run the application

Start Docker Desktop with Ubuntu WSL2 integration enabled. From the project
directory, copy `.env.example` to `.env` and replace `DB_PASSWORD` with a random
password. Then run:

```bash
docker compose up --build -d
docker compose ps
curl http://localhost:8093/
```

The API is available at `http://localhost:8093/api/students`. The application
connects to PostgreSQL through `db:5432` on the Compose network. Database data
is stored in a named volume and survives container recreation.

## API

Each student has `fullName`, `groupName`, `studentNumber` and `email`.
The server assigns the record ID. Student numbers must contain digits and be unique.

| Method | Endpoint | Successful response | Other responses |
| --- | --- | --- | --- |
| POST | /api/students | 201 with a Location header | 400 invalid data, 409 duplicate number |
| GET | /api/students | 200 with an array of students | |
| GET | /api/students/{id} | 200 with one student | 404 missing record |
| PUT | /api/students/{id} | 200 with the updated student | 400 invalid data, 404 missing record, 409 duplicate number |
| DELETE | /api/students/{id} | 204 | 404 missing record |

The CRUD feature branch adds the PUT operation and a lifecycle test that checks
the updated fields before deleting the record.

## Build and test

For a local Gradle build, install JDK 21 and use the included wrapper:

```bash
./gradlew clean build
```

Docker builds provide their own JDK. The wrapper downloads Gradle 8.14.3.
The HTTP tests run against an isolated H2 database in PostgreSQL compatibility
mode. To check the running API against PostgreSQL, use:

```bash
bash scripts/api_demo.sh
```

This script tests CRUD responses and checks that an updated record survives
`docker compose down` followed by `up -d`. It creates and deletes test data for
student number `37693`; other student records are preserved.

## Linux and Jenkins

`bash scripts/linux_evidence.sh` creates the system report, searches the log for
Sembi and backs up this README. The report script uses permission mode `744`.

The Jenkins job is `Sembi_Amir_Student_API`. It checks out the repository, builds
the application, runs tests, builds `sembi-amir-student-api:v1.0-37693` and checks
that the image exists. A failed build or test stops the remaining stages.

## Configuration and credentials

Keep `.env` and `infra/secrets/` out of Git. The example environment file contains
only a placeholder. A public GitHub checkout needs no credential; for a private
repository, select a credential stored in Jenkins Credentials.

## Demonstration and defense

- [Commands for each part of the midterm](docs/DEMONSTRATION_RU.md)
- [Verification checklist](docs/VERIFICATION_RU.md)
- [Defense questions and answers](docs/DEFENSE_RU.md)
- [Official references](docs/REFERENCES.md)
