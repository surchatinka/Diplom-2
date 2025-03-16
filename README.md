# Diplom-2
## Задание 2: API
Нужно протестировать эндпоинты API для Stellar Burgers.
[Документация API](https://code.s3.yandex.net/qa-automation-engineer/java/cheatsheets/paid-track/diplom/api-documentation.pdf).

## Тестируемые эндпоинты
1) Создание пользователя
2) Логин пользователя
3) Изменение данных пользователя
4) Создание заказа
5) Получение заказов конкретного пользователя

## Использованые технологии
| Название      | Версия  |
|:--------------|:--------|
| Java          | 11      |
| JUnit         | 4.13.2  |
| Maven         | 4.0     |
| RestAssured   | 4.4.0   |
| Allure        | 2.15.0  |
| Lombok        | 1.18.20 |
| DataFaker     | 1.8.0   |
| GSon          | 2.10.1  |
| assertj       | 3.25.0   |

## Порядок запуска проекта
1) Перекомпиляция `mvn clean test`
2) Отчет Allure `mvn allure:serve`


