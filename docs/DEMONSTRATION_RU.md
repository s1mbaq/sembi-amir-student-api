# Демонстрация DevOps midterm Sembi Amir

Группа IT2-2307, Student ID 37693. Все команды выполняются из корня проекта в Ubuntu WSL2.
Приложение написано на Spring Boot и хранит записи студентов в PostgreSQL.

## Подготовка

Для сборки на хосте нужен JDK 21; Maven не требуется. В Dockerfile и Jenkins agent JDK уже установлен.
Gradle Wrapper входит в репозиторий. Docker Desktop должен быть запущен с интеграцией Ubuntu WSL2.

```bash
git clone https://github.com/s1mbaq/sembi-amir-student-api.git
cd sembi-amir-student-api
cp .env.example .env
# Укажите собственный случайный DB_PASSWORD в .env
chmod +x gradlew scripts/*.sh
docker compose up --build -d
docker compose ps
curl -s http://localhost:8093/
```

Приложение доступно на порту 8093; запрос к `/` возвращает имя, группу и ID студента.
Приложение работает в контейнере на 8080, PostgreSQL доступен приложению как `db:5432`.
localhost внутри app обозначал бы сам контейнер app, а не PostgreSQL.

## Часть 1 Linux

```bash
mkdir -p scripts logs backups src evidence
touch logs/Sembi_Amir_system_report.log
chmod 744 scripts/Sembi_Amir_system_report.sh
./scripts/linux_evidence.sh | tee evidence/01-linux.txt
ls -l scripts/Sembi_Amir_system_report.sh
cat backups/Sembi_Amir_README.md
cmp README.md backups/Sembi_Amir_README.md
ps -ef
pgrep -af java
docker compose top app
tail -n 5 logs/Sembi_Amir_system_report.log
grep Sembi logs/Sembi_Amir_system_report.log > logs/Sembi_Amir_log_analysis.txt
```

Режим 744 означает rwx для владельца и r-- для группы и остальных. Git хранит только исполняемость,
поэтому после clone повторите chmod 744. `>` перезаписывает файл, `>>` дописывает в конец.
Резервную копию README обновляйте после изменения README.

## Часть 2 Git

```bash
git status
git remote -v
git branch -a
git log --graph --oneline --all --decorate
git tag -n
git show --stat v1.0-37693
```

Ветка CRUD содержит реализацию PUT и отдельный коммит с тестом и документацией.
В ветке documentation и в main изменена одна строка README. При разрешении конфликта сохранены сведения из обеих версий.
Feature-ветки сохранены на GitHub; конфликт и его разрешение видны в истории.
`.env` и infra/secrets исключены из Git. Публичному checkout Jenkins не нужен GitHub credential.
При работе с private repository добавьте credential в Jenkins Credentials и credentialsId в SCM.

## Часть 3 Docker и API

```bash
docker image inspect sembi-amir-student-api:v1.0-37693 --format 'ID={{.Id}} Size={{.Size}} Created={{.Created}}'
docker compose ps
docker compose logs --tail 40 app
docker inspect sembi-amir-student-api-app-1 --format '{{.Name}} {{.Config.Image}} {{json .NetworkSettings.Networks}}'
docker compose exec -T app java -version
docker compose stop app
docker compose start app
bash scripts/api_demo.sh | tee evidence/03-api.txt
```

API demo проверяет создание (201), список и ID (200), изменение (200), сохранение записи после
`docker compose down` и `up -d` (200), удаление (204), отсутствующий ID (404), неправильные данные (400).
Данные остаются в named volume `student-data`; не используйте `down -v` для проверки сохранения.

После изменения `version` в build.gradle и info.app.version в application.properties выполните
`docker compose build app` и `docker compose up -d app`. Это новая сборка после изменения версии.

## Часть 4 Jenkins

Локальный Jenkins открыт на http://localhost:8094, job `Sembi_Amir_Student_API`.
Учётная запись `amir`; пароль хранится локально в `infra/secrets/admin-password` и не включается в архив.
Для чистой установки создайте файл с собственным случайным паролем и выполните:

```bash
mkdir -p infra/secrets
openssl rand -base64 32 > infra/secrets/admin-password
docker compose -f infra/jenkins-compose.yml up --build -d
```

Controller не выполняет сборки. Отдельный inbound agent имеет JDK 21, Git, Docker CLI и label
`sembi-amir-docker`; команды Gradle выполняет wrapper. Docker socket позволяет агенту собирать образы
на локальном Docker Desktop. Этот локальный учебный агент используется для собственного кода.
Jenkins имеет password login и закрытое анонимное чтение; порт опубликован только на loopback.

Pipeline from SCM: URL репозитория, branch `*/main`, script path `Jenkinsfile`.
Стадии: Checkout → Build → Test → Docker Build → Verify. Ошибка команды останавливает pipeline.
JUnit публикуется в Jenkins, HTML отчёт тестов сохраняется как artifact.

Для проверки неуспешной сборки используется ветка `feature/sembi-amir-ci-failure`:
в ней добавлен JUnit-тест с заведомо неверным ожидаемым значением. Отдельная job собирает эту ветку и завершается с ошибкой,
затем исправление коммитится в ту же ветку. Main остаётся рабочей.
Для автоматического запуска включён pollSCM `H/2 * * * *`; после первой ручной сборки
новый commit в main запускает следующую сборку с причиной SCMTriggerCause.
В Jenkins покажите Build History, Console Output, Test Result и причину автоматического запуска.

CI регулярно объединяет и проверяет код: сборка, тесты, проверка образа.
Continuous Delivery готовит пригодный к выпуску результат с ручным решением о выпуске.
Continuous Deployment автоматически выпускает каждый прошедший проверки результат.
Автоматический deployment в этой работе не требуется.

## Материалы для сдачи

Покажите исходники, README, Dockerfile, docker-compose.yml, Jenkinsfile, Bash-скрипты,
логи и backup. На GitHub сохранены коммиты, слияния, feature-ветки и теги.
В папке evidence находятся выводы команд Linux, ответы curl и логи Docker и Jenkins.
Для защиты откройте приложение, репозиторий и Jenkins. Команды выше позволяют повторить проверки по каждому пункту задания.
