# User Service

## Обзор
`user-service` — это консольное приложение на Java для управления данными пользователей, построенное с использованием Maven, Hibernate и PostgreSQL. Приложение предоставляет операции CRUD (создание, чтение, обновление, удаление) для пользователей через консольный интерфейс. Проект использует многослойную архитектуру: слой сервиса (`UserService`) для бизнес-логики и слой DAO (`UserDaoHibernate`) для работы с базой данных.

Проект включает:
- **Юнит-тесты** для слоя сервиса с использованием JUnit 5 и Mockito.
- **Интеграционные тесты** для DAO-слоя с использованием Testcontainers для эмуляции базы данных PostgreSQL.
- **Логирование** с использованием SLF4J и Log4j2, логи сохраняются в `logs/app.log`.

## Требования
- **Java 17** или выше
- **Maven 3.8.0** или выше
- **PostgreSQL 15** или выше (для работы приложения)
- **Docker** (для интеграционных тестов с Testcontainers)
- База данных PostgreSQL с именем `user_service`, пользователем `user_service_user` и паролем `password`

## Структура проекта
```
user-service/
├── src/
│   ├── main/
│   │   ├── java/com/example/userservice/
│   │   │   ├── app/Main.java           # Точка входа консольного приложения
│   │   │   ├── dao/                    # Объекты доступа к данным (DAO)
│   │   │   ├── exception/              # Пользовательские исключения
│   │   │   ├── model/User.java         # Сущность пользователя
│   │   │   ├── service/UserService.java # Бизнес-логика
│   │   │   ├── util/HibernateUtil.java  # Конфигурация Hibernate
│   │   ├── resources/
│   │   │   ├── hibernate.cfg.xml       # Конфигурация Hibernate
│   │   │   ├── log4j2.xml             # Конфигурация Log4j2
│   ├── test/
│   │   ├── java/com/example/userservice/
│   │   │   ├── dao/UserDaoHibernateTest.java # Интеграционные тесты
│   │   │   ├── service/UserServiceTest.java   # Юнит-тесты
├── pom.xml                                 # Конфигурация Maven
├── logs/app.log                            # Логи приложения
```

## Зависимости
- **Hibernate**: 6.5.2.Final (ORM для работы с базой данных)
- **PostgreSQL JDBC Driver**: 42.7.4
- **SLF4J/Log4j2**: 2.22.1 (логирование)
- **JUnit 5**: 5.11.3 (юнит- и интеграционные тесты)
- **Mockito**: 5.14.2 (моки для юнит-тестов)
- **Testcontainers**: 1.20.2 (контейнер PostgreSQL для интеграционных тестов)

## Настройка
1. **Клонируйте репозиторий**:
   ```powershell
   git clone <repository-url>
   cd user-service
   ```

2. **Настройте PostgreSQL**:
    - Убедитесь, что PostgreSQL запущена.
    - Создайте базу данных:
      ```sql
      CREATE DATABASE user_service;
      ```
    - Проверьте учетные данные в `src/main/resources/hibernate.cfg.xml`:
      ```xml
      <property name="hibernate.connection.url">jdbc:postgresql://localhost:5432/user_service</property>
      <property name="hibernate.connection.username">user_service_user</property>
      <property name="hibernate.connection.password">password</property>
      ```

3. **Установите зависимости**:
   ```powershell
   mvn clean install
   ```

4. **Убедитесь, что Docker запущен** (для интеграционных тестов):
   ```powershell
   docker --version
   docker ps
   ```

## Запуск приложения
1. **Соберите проект**:
   ```powershell
   mvn clean package
   ```

2. **Запустите приложение**:
    - Через Maven:
      ```powershell
      mvn exec:java
      ```
    - Или через JAR-файл:
      ```powershell
      java -jar target/user-service-1.0.0.jar
      ```

3. **Взаимодействие с консолью**:
    - После запуска появится меню:
      ```
      === USER SERVICE ===
      1. Создать пользователя
      2. Прочитать пользователя по ID
      3. Прочитать всех пользователей
      4. Обновить пользователя
      5. Удалить пользователя
      0. Выход
      Выберите:
      ```
    - Следуйте инструкциям для выполнения операций CRUD.

4. **Проверка логов**:
    - Логи записываются в `logs/app.log` и содержат информацию об операциях и ошибках.

## Запуск тестов
Проект включает юнит-тесты (`UserServiceTest`) и интеграционные тесты (`UserDaoHibernateTest`).

1. **Запуск всех тестов**:
   ```powershell
   mvn test
   ```
    - Выполняется 19 тестов (9 юнит-тестов, 10 интеграционных тестов).
    - Убедитесь, что Docker запущен для интеграционных тестов с Testcontainers.

2. **Запуск конкретных тестов** (опционально):
    - Только юнит-тесты:
      ```powershell
      mvn test -Dtest=com.example.userservice.service.UserServiceTest
      ```
    - Только интеграционные тесты:
      ```powershell
      mvn test -Dtest=com.example.userservice.dao.UserDaoHibernateTest
      ```

3. **Просмотр отчетов о тестах**:
    - Результаты тестов доступны в `target/surefire-reports`.
