# Онлайн магазин

Это веб-приложение онлайн магазина. На реактивном стеке.

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
mvn clean install
chmod 777 ./target/intershop-DEV-SNAPSHOT.jar
./target/intershop-DEV-SNAPSHOT.jar
```

Для запуска приложения в контейнере:
```bash
mkdir "postgres-data"
docker-compose up --build
```

Для доступа к приложению откройте следующий URL в браузере:

[http://localhost:8080/intershop/product](http://localhost:8080/intershop/product)



