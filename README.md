# Онлайн магазин

Это веб-приложение онлайн магазина. \
Реализовано на реактивном стеке.\
Кеширует потенциально частые запросы.\
Использует сервис оплаты, интеграция проходит при помощи Keycloak (Client Credentials Flow).\
Также на сервисе есть аутентификация/авторизация пользотелей. 

На данный момент добавлено 2 роли ADMIN, USER
Для не авторизованных пользователей доступен просмотр продуктов.

ADMIN: terminator/123456\
USER#1: dobby/qwerty\
USER#2: gollum/qwerty

## Запуск тестов

Для запуска тестов выполните следующую команду:

```bash
mvn test
```

Для сборки исполняемого JAR
```bash
mvn clean install
```

Для локального запуска (нужна БД)
```bash
mvn clean package -DskipTests
java -jar client-service/target/client-service-*.jar & java -jar payment-service/target/payment-service-*.jar
```

Для запуска приложения в контейнере:
```bash
mkdir "postgres-data"
docker-compose up --build
```

Для доступа к приложению откройте следующий URL в браузере:

[http://localhost:8080/intershop/product](http://localhost:8080/intershop/product)



