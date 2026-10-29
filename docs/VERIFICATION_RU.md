# Проверка результатов DevOps midterm

Sembi Amir, IT2-2307, Student ID 37693.

Подробные результаты реальных запусков и номера сборок находятся в локальном
`evidence/SUBMISSION_STATUS_RU.md` внутри архива сдачи. Выводы команд, логи
и результаты API сохраняются в evidence; локальные evidence не публикуются в Git.

## Проверки

1. `bash scripts/linux_evidence.sh`: отчёт системы, лог с фамилией, chmod 744 и backup README.
2. `git log --graph --oneline --all --decorate`: персональные коммиты, feature-ветки и merge.
3. `git tag -n` и `git remote -v`: версия v1.0-37693 и персональный GitHub.
4. `docker compose ps`: app и здоровый db; `docker compose top app`: Java-процесс.
5. `bash scripts/api_demo.sh`: PostgreSQL CRUD, корректные HTTP-коды и сохранение записи после down/up.
6. Jenkins main job: все пять стадий проходят; JUnit публикует пять успешных тестов.
7. Failure Demo job: build из намеренно ошибочного коммита завершается FAILURE в Test,
   Docker Build/Verify пропускаются; после исправления та же ветка проходит.
8. В main сделать новый commit; дождаться pollSCM. У новой сборки должна быть причина
   Started by an SCM change / SCMTriggerCause.

## Среда

Ubuntu WSL2, Docker Desktop. JDK 21 установлен в build-образе и Jenkins agent;
локальные Java/Maven для контейнерного workflow не требуются. Сборка использует Gradle Wrapper.
Тесты приложения работают с H2; curl демонстрация отдельно проверяет настоящий PostgreSQL.

## Jenkins troubleshooting

Ошибка `No such DSL method junit` означает отсутствие JUnit plugin, даже если
Build, Test, Docker Build и Verify прошли. В infra/Jenkins.Dockerfile плагин `junit`
установлен явно. После установки плагина Jenkins нужно перезапустить и повторить pipeline.
Ошибка контролируемого теста отличается: `expected 37693 but was 0`; исправление
в отдельной CI feature-ветке удаляет намеренно неверный тест, сохраняя рабочие тесты.

Секреты .env и infra/secrets исключены из Git и архива. Публичный GitHub checkout
не требует credential. Пароль Jenkins для этого ноутбука остаётся в локальном
infra/secrets/admin-password; на другом компьютере создайте новый пароль по инструкции.
