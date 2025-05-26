# Онлайн магазин

Это веб-приложение онлайн магазина. На реактивном стеке.
Использует сервис оплаты. Кеширует потенциально частые запросы.

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



