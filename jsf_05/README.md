# JSF_03 - Internationalization

Проект демонстрирует использование файлов свойств (.properties) и интернационализацию в JSF.

## Функциональность

*   Простой калькулятор (сложение двух чисел).
*   Валидация параметров.
*   Поддержка двух языков (Английский и Польский).
*   Использование AJAX для вывода результатов.
*   Разделение ресурсов на несколько Resource Bundles.

## Структура

*   `src/main/java/resources` - файлы .properties (textMain, textCalc, textCalcErr).
*   `src/main/webapp/templates` - шаблоны страниц (main, page, header, footer).
*   `CalcBB.java` - Managed Bean (@ViewScoped).

## Запуск

1.  Сборка: `mvn clean package`
2.  Развертывание на сервере приложений (WildFly/JBoss).
3.  URL: `http://localhost:8080/jsf_03/`
