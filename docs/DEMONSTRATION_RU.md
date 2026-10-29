# Демонстрация DevOps midterm Sembi Amir

Группа IT2-2307, Student ID 37693. Все команды выполняются из корня проекта в Ubuntu WSL2.
Приложение написано на Spring Boot и хранит записи студентов в PostgreSQL.

## Подготовка

Для сборки непосредственно в Ubuntu нужен JDK 21. При сборке через Docker или Jenkins используется JDK из образа.
Проект собирается через Gradle Wrapper из репозитория; Maven не требуется. Запустите Docker Desktop и включите интеграцию с Ubuntu WSL2.

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
Адрес localhost внутри app указывает на контейнер приложения. Для соединения с PostgreSQL используется имя сервиса db.

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

Режим 744 даёт владельцу права rwx, группе и остальным пользователям r--. Git сохраняет признак исполняемости,
поэтому после клонирования снова выполните chmod 744. `>` перезаписывает файл, `>>` дописывает в конец.
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

В ветке CRUD операция PUT добавлена отдельным коммитом. Следующий коммит содержит тест и описание изменения.
В ветке documentation и в main изменена одна строка README. При разрешении конфликта сохранены сведения из обеих версий.
Ветки разработки сохранены на GitHub. В истории видны конфликт и коммит с его разрешением.
`.env` и infra/secrets исключены из Git. Jenkins может скачать публичный репозиторий без учётных данных GitHub.
Для закрытого репозитория сохраните токен или ключ в Jenkins Credentials и укажите credentialsId в настройках SCM.

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

Скрипт проверяет создание записи (201), получение списка и записи по ID (200), изменение данных (200) и сохранение записи после
`docker compose down` и `up -d` (200). Затем он проверяет удаление (204), ответ для отсутствующего ID (404) и обработку неправильных данных (400).
Данные хранятся в именованном томе `student-data`; не используйте `down -v` для проверки сохранения.

После изменения `version` в build.gradle и info.app.version в application.properties выполните
`docker compose build app` и `docker compose up -d app`. Это новая сборка после изменения версии.

## Часть 4 Jenkins

Jenkins на этом ноутбуке доступен по адресу http://localhost:8094. Задача называется `Sembi_Amir_Student_API`.
Учётная запись `amir`; пароль хранится локально в `infra/secrets/admin-password` и не включается в архив.
При установке на другом компьютере создайте файл со случайным паролем и запустите Jenkins:

```bash
mkdir -p infra/secrets
openssl rand -base64 32 > infra/secrets/admin-password
docker compose -f infra/jenkins-compose.yml up --build -d
```

Контроллер управляет задачами, а сборки выполняет отдельный подключаемый агент. На агенте есть JDK 21, Git, Docker CLI и метка
`sembi-amir-docker`; Gradle запускается через wrapper. Агент собирает образы в Docker Desktop через Docker socket.
Этот учебный агент предназначен для собственного кода. Вход в Jenkins защищён паролем, анонимное чтение закрыто.
Порт доступен только с этого компьютера.

В настройках Pipeline from SCM укажите URL репозитория, branch `*/main`, script path `Jenkinsfile`.
Стадии: Checkout → Build → Test → Docker Build → Verify. Ошибка команды останавливает pipeline.
Jenkins публикует результаты JUnit и сохраняет HTML-отчёт тестов в артефактах сборки.

Для проверки неуспешной сборки используется ветка `feature/sembi-amir-ci-failure`:
в ней добавлен JUnit-тест с заведомо неверным ожидаемым значением. Отдельная задача собирает эту ветку и падает на тесте.
Исправление сохранено следующим коммитом в той же ветке. В main ошибочный тест не добавлялся.
Для автоматического запуска включён pollSCM `H/2 * * * *`; после первой ручной сборки
новый коммит в main запускает сборку с причиной SCMTriggerCause.
В Jenkins покажите Build History, Console Output, Test Result и причину автоматического запуска.

CI регулярно объединяет и проверяет код: сборка, тесты, проверка образа.
При Continuous Delivery прошедшую проверки версию можно выпустить после ручного подтверждения.
При Continuous Deployment версия выпускается автоматически после успешных проверок.
Автоматическое развёртывание в этой работе не требуется.

## Материалы для сдачи

Покажите исходники, README, Dockerfile, docker-compose.yml, Jenkinsfile, Bash-скрипты,
логи и резервную копию README. На GitHub сохранены коммиты, слияния, feature-ветки и теги.
В папке evidence находятся выводы команд Linux, ответы curl и логи Docker и Jenkins.
Для защиты откройте приложение, репозиторий и Jenkins. Команды выше позволяют повторить проверки по каждому пункту задания.
