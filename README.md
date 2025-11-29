# yandex-bank-application

Учебный микросервисный проект на Java 25. 10 спринт: развёртывание в Kubernetes и настройка CI/CD.

## Цели проекта

- Построить микросервисную архитектуру.
- Использовать Spring Boot, Spring Security, OAuth2, Resilience4j.
- Реализовать фронт на Thymeleaf по готовым HTML-шаблонам.
- Покрыть приложение юнит-, интеграционными и интеграционными тестами, а также контрактными тестами для взаимодействия сервисов.
- Собрать приложение в Docker-образы.
- Настроить развёртывание всего приложения в Kubernetes с помощью Helm.
- Настроить автоматическое развёртывание через Jenkins (CI/CD).

---

## Архитектура проекта

### Сервисы

Бизнес-сервисы:

- accounts-service — пользователи и балансы
- cash-service — операции пополнения/снятия
- transfer-service — переводы между пользователями
- notifications-service — уведомления
- front-ui — веб-фронт на Spring MVC + Thymeleaf
- auth-server — OAuth2 Authorization Server
- common — общие DTO и вспомогательные классы

Инфраструктурные сервисы (используются в ранних частях проекта, для docker-compose):

- discovery-server — Eureka Server
- config-server — Spring Cloud Config (native backend)
- gateway — Spring Cloud Gateway

Во 2 версии, при развёртывании в Kubernetes:

- сервисы не используют Eureka (service discovery обеспечивается Service и DNS Kubernetes);
- внешний входной трафик маршрутизируется не через Spring Cloud Gateway, а через Gateway API Kubernetes (Gateway + HTTPRoute);
- конфигурация приложений вынесена в ConfigMap и Secret, вместо Spring Cloud Config.

### Основные взаимодействия

В Kubernetes:

- внешний клиент → Gateway API (Gateway + HTTPRoute) → front-ui
- front-ui вызывает backend-сервисы по HTTP через DNS имён сервисов Kubernetes:
    - accounts-service
    - cash-service
    - transfer-service
    - notifications-service
- cash-service и transfer-service вызывают accounts-service напрямую через HTTP (по сервисному имени в Kubernetes)
- cash-service и transfer-service после успешной операции отправляют уведомления в notifications-service
- все backend-сервисы используют auth-server для авторизации (JWT / OAuth2)

В docker-compose (предыдущие этапы):

- front-ui → gateway → backend
- бизнес-сервисы регистрируются в Eureka и используют Spring Cloud Gateway

---

## Технологии

- Java 25
- Gradle (multi-module)
- Spring Boot 3.5.7
- Spring Security, Spring Authorization Server (OAuth2)
- Resilience4j
- PostgreSQL
- JPA / Hibernate
- Thymeleaf
- Docker, docker-compose
- Kubernetes
- Helm (umbrella chart + subcharts)
- Gateway API (Gateway, HTTPRoute)
- Jenkins (Declarative Pipeline)
- JUnit 5, Spring Boot Test, Spring Cloud Contract

---

## Структура проекта

Модули Gradle:

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

Helm-чарты и Kubernetes-манифесты:

- helm/
    - Chart.yaml — зонтичный (umbrella) Helm-чарт bank-app
    - values.yaml — общие значения и настройки сервисов
    - charts/
        - accounts-service — чарт для accounts-service
        - cash-service — чарт для cash-service
        - transfer-service — чарт для transfer-service
        - notifications-service — чарт для notifications-service
        - front-ui — чарт для фронтенда
        - auth-server — чарт для authorization-server
        - postgres-accounts — чарт для PostgreSQL, используемой accounts-service
    - templates/
        - gateway.yaml — ресурс Gateway API
        - httproutes.yaml — HTTPRoute для маршрутизации запросов к сервисам

CI/CD:

- Jenkinsfile — общий pipeline для сборки всех сервисов и деплоя всего приложения
- accounts-service/Jenkinsfile — pipeline для accounts-service
- cash-service/Jenkinsfile — pipeline для cash-service
- transfer-service/Jenkinsfile — pipeline для transfer-service
- notifications-service/Jenkinsfile — pipeline для notifications-service
- front-ui/Jenkinsfile — pipeline для front-ui
- auth-server/Jenkinsfile — pipeline для auth-server

---

## Порты

Порты контейнеров (используются в docker-compose и как targetPort в Kubernetes):

| Сервис                 | Порт |
|------------------------|------|
| discovery-server       | 8761 |
| config-server          | 8888 |
| auth-server            | 9000 |
| gateway                | 8085 |
| accounts-service       | 8080 |
| cash-service           | 8080 |
| transfer-service       | 8080 |
| notifications-service  | 8080 |
| front-ui               | 8080 |
| PostgreSQL             | 5432 |

В Kubernetes для всех HTTP-сервисов создаются Service с портом 80 и targetPort, соответствующим порту приложения в контейнере (обычно 8080, для auth-server — 9000).

---

## API микросервисов

### accounts-service

- POST /api/accounts/signup
- POST /api/accounts/auth
- GET /api/accounts/me
- GET /api/accounts/me/balance
- GET /api/accounts/users?exclude=login
- POST /api/accounts/{login}
- POST /api/accounts/{login}/password
- POST /api/accounts/adjust — внутренний контракт для изменения баланса

### cash-service

- POST /api/cash
    - PUT → пополнение
    - GET → снятие
- вызывает /api/accounts/adjust
- отправляет уведомление в notifications-service
- использует Resilience4j: @CircuitBreaker(name = "cash")

### transfer-service

- POST /api/transfers
- списывает у отправителя, зачисляет получателю
- отправляет два уведомления через notifications-service
- использует Resilience4j: @CircuitBreaker(name = "transfer")

### notifications-service

- POST /api/notifications — логирование текста уведомления
- использует Resilience4j: @CircuitBreaker(name = "notify")

### front-ui

Spring MVC + Thymeleaf, работает по готовым шаблонам signup.html и main.html.

Реализует формы:

- регистрация
- вход
- обновление профиля
- смена пароля
- пополнение / снятие
- перевод

---

## Тестирование

### Юнит-тесты

- AccountServiceTest
- CashServiceTest
- TransferServiceTest
- NotificationServiceTest

### Интеграционные тесты

- AccountRepositoryTest (@DataJpaTest)
- AccountsControllerIntegrationTest (@SpringBootTest)

### Контрактные тесты (Spring Cloud Contract)

В модуле accounts-service:

- adjust_balance.groovy — контракт на POST /api/accounts/adjust
- BaseContractTest + MockMvc
- Gradle task: contractTest

Контракт гарантирует совместимость между cash-service, transfer-service и accounts-service.

---

## Развёртывание в Kubernetes

### Предварительные условия

- установлен Helm
- настроено подключение к Kubernetes-кластеру (kubectl context)
- в кластере установлен реализационный класс Gateway API (например, nginx-gateway)

### Обновление зависимостей Helm-чартов

Из корня репозитория:

```bash
cd helm
helm dependency update
```

### Развёртывание в namespace dev

```bash
helm upgrade --install bank-app-dev helm   --namespace dev --create-namespace
```

После этого в namespace dev будут развернуты:

- PostgreSQL (postgres-accounts)
- accounts-service
- cash-service
- transfer-service
- notifications-service
- front-ui
- auth-server
- ресурсы Gateway API (Gateway и HTTPRoute)

Сервисный домен для Gateway настроен как bank.ru.yandex (учебный домен).

### Развёртывание в namespace prod

```bash
helm upgrade --install bank-app-prod helm   --namespace prod --create-namespace
```

### Helm-тесты

Для бизнес-сервисов настроены простые Helm tests, которые проверяют доступность HTTP эндпоинтов здоровье-сервиса:

```bash
helm test bank-app-dev --namespace dev
```

---

## Конфигурация и секреты

В Kubernetes конфигурирование приложения вынесено из Spring Cloud Config в стандартные объекты Kubernetes:

- ConfigMap — для не секретных настроек (например, SPRING_PROFILES_ACTIVE).
- Secret — для конфиденциальных данных (логин/пароль БД).

Пример:

- accounts-service:
    - ConfigMap содержит SPRING_PROFILES_ACTIVE.
    - Secret (accounts-db-secret) содержит username и password для подключения к PostgreSQL.
    - параметры подключения к базе (host, port, database) передаются через переменные окружения.

---

## CI/CD через Jenkins

### Общий pipeline

В корне репозитория находится файл Jenkinsfile, который описывает сборку и развёртывание всего приложения.

Основные шаги:

1. Получение исходного кода.
2. Сборка и тестирование всех модулей Gradle.
3. Сборка Docker-образов для всех сервисов.
4. Публикация Docker-образов в реестр registry.ru.yandex/bank (учебный реестр).
5. Деплой Helm-релиза в namespace dev.
6. Запуск Helm-тестов.
7. По подтверждению — деплой в namespace prod.

### Pipeline для сервиса

Для каждого микросервиса есть отдельный Jenkinsfile внутри модуля:

- accounts-service/Jenkinsfile
- cash-service/Jenkinsfile
- transfer-service/Jenkinsfile
- notifications-service/Jenkinsfile
- front-ui/Jenkinsfile
- auth-server/Jenkinsfile

Каждый такой pipeline:

1. Собирает и тестирует только свой модуль Gradle.
2. Собирает Docker-образ сервиса.
3. Публикует образ в реестр.
4. Обновляет Helm-релиз, передавая новый tag образа только для этого сервиса.
5. При необходимости может использоваться для изолированного деплоя одного сервиса.

---

## Запуск через Docker (локально)

Для локального развёртывания без Kubernetes предусмотрен docker-compose.

Из корня проекта:

```bash
docker compose build
docker compose up
```

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

Этот режим использует Spring Cloud Config, Eureka и Spring Cloud Gateway и соответствует предыдущим этапам задания. Для финального задания по Kubernetes используется Helm и Gateway API.
