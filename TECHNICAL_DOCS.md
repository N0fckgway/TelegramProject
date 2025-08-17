# 🔧 Техническая документация BirthCalendarBot

## 📋 Содержание
1. [Архитектура системы](#архитектура-системы)
2. [Модели данных](#модели-данных)
3. [API и интерфейсы](#api-и-интерфейсы)
4. [База данных](#база-данных)
5. [Система уведомлений](#система-уведомлений)
6. [Безопасность](#безопасность)
7. [Развертывание](#развертывание)

## 🏗️ Архитектура системы

### **Общая схема**
```
┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│   Telegram API  │◄──►│   ConnectBot    │◄──►│   PostgreSQL   │
└─────────────────┘    └─────────────────┘    └─────────────────┘
                              │
                              ▼
                       ┌─────────────────┐
                       │NotificationSched│
                       │     uler        │
                       └─────────────────┘
```

### **Основные компоненты**

#### **1. TelegramBot (Main Class)**
- **Назначение:** Точка входа в приложение
- **Ответственность:** Инициализация всех компонентов
- **Зависимости:** ConnectBot, BotInitializer, DBConnector, NotificationScheduler

#### **2. ConnectBot**
- **Назначение:** Основной класс бота, наследующий TelegramLongPollingBot
- **Ответственность:** Обработка входящих сообщений и callback-запросов
- **Ключевые методы:** `onUpdateReceived()`, `executeCommand()`, `executeButton()`

#### **3. Runner**
- **Назначение:** Регистрация и управление командами и кнопками
- **Ответственность:** Хранение HashMap'ов с обработчиками
- **Паттерн:** Registry Pattern

#### **4. NotificationScheduler**
- **Назначение:** Планировщик уведомлений
- **Ответственность:** Отправка уведомлений в заданное время
- **Технология:** ScheduledExecutorService

## 📊 Модели данных

### **User.java**
```java
@Getter @Setter @ToString
public class User {
    private Long chatId;           // Telegram Chat ID
    private String firstName;      // Имя пользователя
    private String lastName;       // Фамилия пользователя
    private String userName;       // Telegram username
    private String phoneNumber;    // Номер телефона
    private LocalDate birthday;    // Дата рождения
    private Integer age;           // Вычисляемый возраст
    
    // Статические методы для временного хранения
    public static void saveTempUser(Long chatId, User user)
    public static User getTempUser(Long chatId)
    public static void removeTempUser(Long chatId)
}
```

### **Friend.java**
```java
@Getter @Setter
public class Friend {
    private Long id;               // Уникальный ID друга
    private String firstName;      // Имя друга
    private String lastName;       // Фамилия друга
    private String role;           // Роль (отец, мать, друг)
    private LocalDate birthday;    // Дата рождения
}
```

### **NotificationConfig.java**
```java
public class NotificationConfig {
    private Long chatId;           // ID чата пользователя
    private Boolean enabledForUsers;    // Уведомления для пользователя
    private Boolean enabledForFriends;  // Уведомления для друзей
}
```

## 🔌 API и интерфейсы

### **ExecuteCommand Interface**
```java
public interface ExecuteCommand {
    void apply(Update update);
    Command getCommand();
}
```

**Реализации:**
- `Start` - приветствие и инструкции
- `Help` - справка по командам
- `Registration` - регистрация пользователя
- `Add` - добавление друга
- `Delete` - удаление друга
- `Setting` - настройки уведомлений

### **ExecuteButton Interface**
```java
public interface ExecuteButton {
    void applyButton(Update update);
}
```

**Реализации:**
- `Profile` - профиль пользователя
- `Friends` - список друзей
- `Notification` - настройки уведомлений
- `CalendarHandler` - обработка календаря
- `SendContact` - отправка контакта

### **CalendarKeyboard**
```java
public class CalendarKeyboard {
    // Создание клавиатуры для выбора года
    public static InlineKeyboardMarkup createKeyboardForChooseYear()
    
    // Создание клавиатуры для выбора месяца
    public static InlineKeyboardMarkup createKeyboardForChooseMonth()
    
    // Создание клавиатуры для выбора дня
    public static InlineKeyboardMarkup createKeyboardForChooseDay(int year, int month)
}
```

## 🗄️ База данных

### **Схема базы данных**

#### **Последовательности**
```sql
-- Автоинкремент для пользователей
CREATE SEQUENCE user_id_seq INCREMENT 1 START 1;

-- Автоинкремент для друзей
CREATE SEQUENCE friend_id_seq INCREMENT 1 START 1;
```

#### **Таблица users**
```sql
CREATE TABLE users (
    id BIGINT PRIMARY KEY DEFAULT NEXTVAL('user_id_seq'),
    chatid BIGINT UNIQUE NOT NULL,                    -- Telegram Chat ID
    firstName VARCHAR(255) NOT NULL CHECK(firstName<>''),
    lastName VARCHAR(255) CHECK(lastName<>''),
    userName VARCHAR(255) CHECK(userName<>''),
    phoneNumber VARCHAR(255) NOT NULL CHECK(phoneNumber<>''),
    birth DATE NOT NULL,                              -- Дата рождения
    age INTEGER NOT NULL                              -- Вычисляемый возраст
);
```

#### **Таблица friends**
```sql
CREATE TABLE friends (
    id BIGINT PRIMARY KEY DEFAULT NEXTVAL('friend_id_seq'),
    owner_chatid BIGINT NOT NULL REFERENCES users(chatid), -- Владелец
    role VARCHAR(64) NOT NULL CHECK(role<>''),            -- Роль друга
    firstName VARCHAR(255) NOT NULL CHECK(firstName<>''),
    lastName VARCHAR(255) NOT NULL CHECK(lastName<>''),
    birth DATE NOT NULL,                                  -- Дата рождения
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP         -- Время создания
);
```

#### **Таблица notification_setting**
```sql
CREATE TABLE notification_setting (
    chatid BIGINT PRIMARY KEY REFERENCES users(chatid),
    enabled_for_users BOOLEAN DEFAULT true,               -- Уведомления для пользователя
    enabled_for_friends BOOLEAN DEFAULT true              -- Уведомления для друзей
);
```

### **Основные запросы**

#### **Добавление пользователя**
```sql
INSERT INTO users(chatid, firstName, lastName, phonenumber, username, birth, age) 
VALUES (?, ?, ?, ?, ?, ?, ?)
```

#### **Добавление друга**
```sql
INSERT INTO friends(owner_chatid, role, firstName, lastName, birth) 
VALUES (?, ?, ?, ?, ?)
```

#### **Получение друзей пользователя**
```sql
SELECT f.id, f.firstName, f.lastName, f.role, f.birth 
FROM friends f 
WHERE f.owner_chatid = ?
```

#### **Получение пользователей с включенными уведомлениями**
```sql
SELECT u.chatid, u.firstName, u.lastName, u.username, u.phonenumber, u.birth, u.age 
FROM users u 
INNER JOIN notification_setting n ON u.chatid = n.chatid 
WHERE n.enabled_for_users = ?
```

## 🔔 Система уведомлений

### **Архитектура планировщика**

```java
public class NotificationScheduler extends ConnectBot {
    private final DBManager dbManager;
    private static ScheduledExecutorService scheduler;
    
    // Запуск планировщика
    private void startScheduler() {
        scheduler = Executors.newScheduledThreadPool(1);
        scheduleDailyNotification();
    }
    
    // Планирование ежедневных уведомлений
    private synchronized void scheduleDailyNotification() {
        long initialDelay = getNextRunTime(10, 0); // 10:00 утра
        
        scheduler.schedule(() -> {
            sendDailyNotifications();
            sendBirthdayNotificationForFriends();
            scheduleDailyNotification(); // Перепланирование
        }, initialDelay, TimeUnit.MILLISECONDS);
    }
}
```

### **Типы уведомлений**

#### **1. Уведомления для пользователя**
- **Время:** Ежедневно в 10:00
- **Содержание:** Количество дней до дня рождения
- **Формат:** "До вашего дня рождения осталось X дней! 🎉"

#### **2. Уведомления для друзей**
- **Время:** Ежедневно в 10:00
- **Содержание:** Напоминания о днях рождения друзей
- **Формат:** "Через X дней день рождения у [Имя] [Фамилия]! 🎂"

### **Логика расчета времени**

```java
private long getNextRunTime(int hour, int minute) {
    ZonedDateTime now = ZonedDateTime.now();
    ZonedDateTime nextRun = now.withHour(hour).withMinute(minute).withSecond(0).withNano(0);

    if (now.compareTo(nextRun) > 0) {
        nextRun = nextRun.plusDays(1); // Следующий день
    }

    return Duration.between(now, nextRun).toMillis();
}
```

## 🔒 Безопасность

### **Защита от SQL-инъекций**

#### **Использование PreparedStatement**
```java
public void addUser(User user) throws SQLException {
    dbConnector.handleQuery((Connection conn) -> {
        String insertQuery = "INSERT INTO users(chatid, firstName, lastName, phonenumber, username, birth, age) VALUES (?, ?, ?, ?, ?, ?, ?)";
        PreparedStatement preparedStatement = conn.prepareStatement(insertQuery);
        
        preparedStatement.setLong(1, user.getChatId());
        preparedStatement.setString(2, user.getFirstName());
        // ... остальные параметры
        
        preparedStatement.executeUpdate();
    });
}
```

### **Валидация входных данных**

#### **TextParser.java**
```java
public class TextParser {
    // Валидация ФИО (русские буквы, первая заглавная)
    public Boolean hasFullName(String text) {
        String fullNameRegex = "^[А-ЯЁ][а-яё]+\\s+[А-ЯЁ][а-яё]+$";
        return text.trim().matches(fullNameRegex);
    }
    
    // Валидация даты (формат ДД.ММ.ГГГГ)
    public Boolean hasDate(String text) {
        String dateRegex = "^(0[1-9]|[12]\\d|3[01])\\.(0[1-9]|1[0-2])\\.\\d{4}$";
        return text.trim().matches(dateRegex);
    }
    
    // Валидация роли (любое русское слово)
    public Boolean hasRole(String text) {
        String roleRegex = "^[а-яёА-ЯЁ]+(\\s+[а-яёА-ЯЁ]+)*$";
        return text.trim().matches(roleRegex);
    }
}
```

### **Проверка владельца данных**

```java
public boolean isFriendOwnedByUser(Long friendId, Long ownerChatId) {
    return dbConnector.handleQuery((Connection conn) -> {
        String sqlQuery = "SELECT COUNT(*) FROM friends WHERE id = ? AND owner_chatid = ?";
        PreparedStatement preparedStatement = conn.prepareStatement(sqlQuery);
        preparedStatement.setLong(1, friendId);
        preparedStatement.setLong(2, ownerChatId);
        ResultSet rs = preparedStatement.executeQuery();
        
        rs.next();
        int count = rs.getInt(1);
        boolean isOwned = count > 0;
        
        return isOwned;
    });
}
```

## 🚀 Развертывание

### **Локальная разработка**

#### **1. Настройка окружения**
```bash
# Установка Java 21
brew install openjdk@21

# Установка PostgreSQL
brew install postgresql

# Запуск PostgreSQL
brew services start postgresql
```

#### **2. Создание базы данных**
```bash
# Подключение к PostgreSQL
psql postgres

# Создание БД и пользователя
CREATE DATABASE telegramusersdata;
CREATE USER birthcalendarbot WITH PASSWORD 'secure_password';
GRANT ALL PRIVILEGES ON DATABASE telegramusersdata TO birthcalendarbot;
```

#### **3. Запуск приложения**
```bash
# Сборка проекта
./gradlew build

# Запуск
./gradlew run
```

## 📚 Дополнительные ресурсы

### **Полезные ссылки**
- [Telegram Bot API Documentation](https://core.telegram.org/bots/api)
- [PostgreSQL Documentation](https://www.postgresql.org/docs/)
- [Java 21 Documentation](https://docs.oracle.com/en/java/javase/21/)
- [Gradle User Guide](https://docs.gradle.org/current/userguide/userguide.html)

### **Рекомендуемые инструменты**
- **IDE:** IntelliJ IDEA, Eclipse, VS Code
- **База данных:** pgAdmin, DBeaver
- **API тестирование:** Postman, Insomnia
- **Мониторинг:** Prometheus, Grafana

---

**Эта документация поможет разработчикам быстро понять архитектуру проекта и начать работу с кодом.** 