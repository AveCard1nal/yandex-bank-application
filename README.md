# yandex-bank-application

Учебный микросервисный проект на Java 25. Развёртывание в Kubernetes и настройка CI/CD, плюс интеграция Apache Kafka для взаимодействия сервисов.

## Цели проекта

- Построить микросервисную архитектуру.
- Использовать Spring Boot, Spring Security, OAuth2, Resilience4j.
- Реализовать фронт на Thymeleaf по готовым HTML-шаблонам.
- Покрыть приложение юнит-, интеграционными и контрактными тестами.
- Собрать приложение в Docker-образы.
- Настроить развёртывание приложения в Kubernetes с помощью Helm.
- Настроить автоматическое развёртывание через Jenkins (CI/CD).
- Добавить Apache Kafka и перевести отправку уведомлений в notifications-service на обмен сообщениями через Kafka (без REST).

## Архитектура проекта

### Сервисы

- accounts-service — пользователи и балансы
- cash-service — операции пополнения/снятия
- transfer-service — переводы между пользователями
- notifications-service — обработка уведомлений (Kafka consumer)
- front-ui — веб-фронт
- auth-server — OAuth2 Authorization Server
- common — общие DTO (Kafka события)

### В Kubernetes

- Service Discovery через Kubernetes Service + DNS
- Внешний трафик через Gateway API
- Конфигурация через ConfigMap и Secret
- Apache Kafka развёрнут через Helm в namespace `kafka`

## Основные взаимодействия

- cash-service и transfer-service вызывают accounts-service по HTTP
- cash-service и transfer-service публикуют события в Kafka
- notifications-service читает события из Kafka topic `bank.notifications`
- Стратегия доставки: at-least-once

## Технологии

- Java 25
- Gradle (multi-module)
- Spring Boot
- PostgreSQL
- Docker, Kubernetes
- Helm
- Gateway API
- Apache Kafka
- Jenkins
- Testcontainers (Kafka)

## Развёртывание в Kubernetes

### Развёртывание Kafka

```bash
helm dependency update kafka-helm
helm upgrade --install bank-kafka kafka-helm   --namespace kafka --create-namespace
```

В процессе установки автоматически создаётся topic:

```
bank.notifications
```

Данные Kafka сохраняются в PersistentVolume.

### Развёртывание приложения

```bash
helm upgrade --install bank-app-dev helm   --namespace dev --create-namespace

helm test bank-app-dev --namespace dev
```

### Продакшн

```bash
helm upgrade --install bank-app-prod helm   --namespace prod --create-namespace
```

## CI/CD

- `kafka-helm/Jenkinsfile` — деплой Kafka
- Корневой `Jenkinsfile` — сборка и деплой приложения

## Локальный запуск

```bash
docker compose build
docker compose up
```