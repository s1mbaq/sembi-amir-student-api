# Проверка результатов DevOps midterm

Sembi Amir, IT2-2307, Student ID 37693.

Результаты выполненных проверок и номера сборок записаны в файле
`evidence/SUBMISSION_STATUS_RU.md` внутри архива сдачи. Папка evidence содержит выводы команд, логи
и ответы API. Эти файлы включены в архив сдачи и исключены из Git.

## Проверки

1. `bash scripts/linux_evidence.sh`: отчёт системы, лог с фамилией, chmod 744 и backup README.
2. `git log --graph --oneline --all --decorate`: персональные коммиты, feature-ветки и merge.
3. `git tag -n` и `git remote -v`: версия v1.0-37693 и персональный GitHub.
4. `docker compose ps`: app и здоровый db; `docker compose top app`: Java-процесс.
5. `bash scripts/api_demo.sh`: PostgreSQL CRUD, корректные HTTP-коды и сохранение записи после down/up.
6. В основной задаче Jenkins проходят все пять стадий. Отчёт JUnit содержит пять успешных тестов.
7. В задаче Failure Demo сборка ошибочного коммита завершается FAILURE на стадии Test,
   Docker Build/Verify пропускаются; после исправления та же ветка проходит.
8. Сделайте новый коммит в main и дождитесь pollSCM. У автоматически запущенной сборки будет причина
   Started by an SCM change / SCMTriggerCause.

## Среда

Проект работает в Ubuntu WSL2 с Docker Desktop. JDK 21 установлен в образе сборки и на агенте Jenkins.
При таком запуске Java и Maven на хосте не нужны. Сборка использует Gradle Wrapper.
Автоматические тесты используют H2. Проверка через curl выполняется отдельно с PostgreSQL.

## Ошибки Jenkins

Ошибка `No such DSL method junit` означает, что не установлен плагин JUnit, даже если
Build, Test, Docker Build и Verify прошли. В infra/Jenkins.Dockerfile плагин `junit`
установлен явно. После установки плагина перезапустите Jenkins и повторите сборку.
При контролируемой ошибке теста выводится: `expected 37693 but was 0`; исправление
в отдельной CI feature-ветке удаляет намеренно неверный тест, сохраняя рабочие тесты.

Файл .env и каталог infra/secrets исключены из Git и архива. Для скачивания публичного
репозитория GitHub учётные данные не нужны. Пароль Jenkins для этого ноутбука остаётся в локальном
infra/secrets/admin-password; на другом компьютере создайте новый пароль по инструкции.
