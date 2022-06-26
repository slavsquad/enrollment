Mega Market Open API
============

Вступительное задание в Летнюю Школу Бэкенд Разработки Яндекса 2022.

TECHNOLOGY STACK
----------------

        Java 17 SE
        SpringBoot
        JdbcTemplate
        Postgres
        Docker
        Zonky
        Gradle
        Springfox Swagger UI

REQUIREMENTS
------------

        Docker 20.10.16
        Gradle 7.4.2
        Git 2.25.1

QUICK START
-----------
Для сборки приложения сначала его необходимо клонировать:

        $ git clone https://github.com/slavsquad/enrollment.git

Затем собрать при помощи инструмента сборки:

        $ cd enrollment
        $ gradle clean build

Запустить приложение:

        $ docker-compose up -d --build

Documentation
-----------
    Springfox Swagger UI 2.9.2
После запуска приложения будет доступен инструмент для автоматического описания методов взаимодействия с приложением:
    
        http//:[ваш_url:80]/swagger-ui.html#

https://losses-1910.usr.yandex-academy.ru/swagger-ui.html#

Developers
-----------

        Stepanenko Stas
        e-mail: stepanenkostas87@yandex.ru
