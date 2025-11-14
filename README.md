# yandex-bank-application

Учебный микросервисный проект на Java 25, спринт 9.

## Цели проекта

- Построить микросервисную архитектуру.
- Использовать Spring Boot, Spring Cloud, OAuth2, Resilience4j.
- Реализовать фронт на Thymeleaf по готовым HTML-шаблонам.
- Покрыть приложение юнит-, интеграционными и контрактными тестами.
- Собрать всё в Docker / docker-compose.

---

## Архитектура проекта

### Сервисы

- discovery-server — Eureka Server
- config-server — Spring Cloud Config (native backend)
- auth-server — OAuth2 Authorization Server
- gateway — Spring Cloud Gateway
- accounts-service — пользователи и балансы
- cash-service — операции PUT/GET
- transfer-service — переводы
- notifications-service — уведомления
- front-ui — веб-фронт на Spring MVC + Thymeleaf
- common — DTO

Все бизнес-сервисы регистрируются в Eureka.

### Основные взаимодействия

- front-ui ⟶ gateway ⟶ backend
- cash-service и transfer-service вызывают accounts-service через gateway
- после успешной операции cash/transfer отправляют уведомления в notifications-service
- сервисы авторизуются через auth-server (client_credentials)

## Технологии

- Java 25
- Gradle (multi-module)
- Spring Boot 3.5.7
- Spring Cloud: Eureka, Gateway, Config, OpenFeign
- Spring Authorization Server
- Spring Security (JWT resource server + OAuth2 client)
- Resilience4j
- PostgreSQL
- JPA / Hibernate
- Thymeleaf
- Docker, docker-compose
- JUnit 5, Spring Boot Test, Spring Cloud Contract

---

## Структура проекта

- discovery-server
- config-server
- auth-server
- gateway
- accounts-service
- cash-service
- transfer-service
- notifications-service
- front-ui
- common


---

## Порты

| Сервис                 | Порт |
|------------------------|------|
| discovery-server       | 8761 |
| config-server          | 8888 |
| auth-server            | 9000 |
| gateway                | 8085 |
| accounts-service       | 8081 |
| cash-service           | 8082 |
| transfer-service       | 8083 |
| notifications-service  | 8084 |
| front-ui               | 8080 |
| PostgreSQL             | 5432 |


---

# 📌 Часть 4 — функциональность микросервисов

## accounts-service — API

- POST /api/accounts/signup  
- POST /api/accounts/auth  
- GET /api/accounts/me  
- GET /api/accounts/me/balance  
- GET /api/accounts/users?exclude=login  
- POST /api/accounts/{login}  
- POST /api/accounts/{login}/password  
- POST /api/accounts/adjust  ← внутренний контракт

---

## cash-service

- POST /api/cash  
- PUT → пополнение  
- GET → снятие  
- вызывает /api/accounts/adjust через gateway  
- отправляет уведомление  
- `@CircuitBreaker(name = "cash")`

---

## transfer-service

- POST /api/transfers  
- списывает у отправителя, зачисляет получателю  
- отправляет 2 уведомления  
- `@CircuitBreaker(name = "transfer")`

---

## notifications-service

- POST /api/notifications  
- логирование текста уведомления  
- `@CircuitBreaker(name = "notify")`

---

## front-ui

Работает по готовым шаблонам signup.html и main.html.  
Реализует формы:

- регистрация  
- вход  
- обновление профиля  
- смена пароля  
- пополнение / снятие  
- перевод  


## Тестирование

### Юнит-тесты

- AccountServiceTest
- CashServiceTest
- TransferServiceTest
- NotificationServiceTest

### Интеграционные тесты

- AccountRepositoryTest (`@DataJpaTest`)
- AccountsControllerIntegrationTest (`@SpringBootTest`)

### Контрактные тесты (Spring Cloud Contract)

В `accounts-service`:

- adjust_balance.groovy (контракт на POST /api/accounts/adjust)
- BaseContractTest + MockMvc
- gradle task `contractTest`

Контракт гарантирует совместимость cash/transfer → accounts-service.

## Docker и docker-compose

docker-compose.yml поднимает:

- postgres
- discovery-server
- config-server
- auth-server
- gateway
- accounts-service
- cash-service
- transfer-service
- notifications-service
- front-ui

Каждый сервис содержит Dockerfile с multi-stage сборкой (Gradle → JRE).

### Запуск через Docker

```bash
docker compose build
docker compose up
