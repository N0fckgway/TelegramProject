# 🎂 BirthCalendarBot - Telegram Bot

**BirthCalendarBot** - это интеллектуальный Telegram бот для управления днями рождения друзей и близких. Бот автоматически напоминает о предстоящих праздниках и помогает не забывать важные даты.

## 🌟 Основные возможности

### 👤 **Управление профилем**
- Регистрация пользователей с указанием даты рождения
- Автоматический расчет возраста
- Настройка уведомлений

### 👥 **Управление друзьями**
- Добавление друзей с указанием роли (отец, мать, лучший друг и т.д.)
- Хранение дат рождения друзей
- Удаление друзей по ID

### 🔔 **Система уведомлений**
- Ежедневные уведомления в 10:00 утра
- Напоминания за 7 дней до дня рождения
- Напоминания за 1 день до дня рождения
- Настраиваемые уведомления для пользователя и друзей

### 📅 **Интерактивный календарь**
- Выбор года (1950-текущий год)
- Выбор месяца (1-12)
- Выбор дня для регистрации

## 🏗️ Архитектура проекта

### **Структура пакетов**
```
org.telebot/
├── TelegramBot.java              # Главный класс приложения
├── config/                       # Конфигурация бота
├── command/                      # Команды бота
│   ├── runner/                   # Регистрация команд
│   └── interfaces/               # Интерфейсы команд
├── buttons/                      # Обработчики кнопок
├── connector/                    # Основная логика
│   ├── ConnectBot.java          # Основной класс бота
│   └── NotificationScheduler.java # Планировщик уведомлений
└── data/                        # Модели данных
    ├── database/                 # Работа с БД
    ├── parser/                   # Парсинг текста
    └── interfaces/               # Интерфейсы данных
```

## 🚀 Быстрый старт

### **Предварительные требования**
- Java 21+
- PostgreSQL 12+
- Gradle 8.0+
- Telegram Bot Token (от @BotFather)

### **1. Клонирование репозитория**
```bash
git clone <your-repo-url>
cd TelegramProject
```

### **2. Настройка базы данных**
```sql
-- Создание базы данных
CREATE DATABASE telegramusersdata;

-- Создание пользователя (замените на свои данные)
CREATE USER birthcalendarbot WITH PASSWORD 'your_secure_password';

-- Предоставление прав
GRANT ALL PRIVILEGES ON DATABASE telegramusersdata TO birthcalendarbot;
```

### **3. Настройка конфигурации**
```bash
# Скопируйте шаблон конфигурации
cp app/src/main/resources/properties/apiBot.properties.template \
   app/src/main/resources/properties/apiBot.properties

# Отредактируйте файл с вашими данными
nano app/src/main/resources/properties/apiBot.properties
```

**Пример конфигурации:**
```properties
# Telegram Bot Configuration
httpApi=YOUR_BOT_TOKEN_HERE
username=YOUR_BOT_USERNAME_HERE

# Database Configuration
nameDB=birthcalendarbot
passwd=your_secure_password
dbUrl=jdbc:postgresql://localhost:5432/telegramusersdata
```

### **4. Инициализация базы данных**
```bash
# Запустите приложение один раз для создания таблиц
./gradlew run
```

### **5. Запуск бота**
```bash
./gradlew run
```

**📚 Подробная документация по работе с запущенным ботом:** [docs/RUNNING_BOT.md](docs/RUNNING_BOT.md)

## 📱 Команды бота

### **Основные команды**
| Команда | Описание | Функциональность |
|---------|----------|------------------|
| `/start` | Начать работу | Приветствие и инструкции |
| `/help` | Справка | Список всех команд |
| `/registration` | Регистрация | Создание профиля пользователя |
| `/add` | Добавить друга | Пошаговое добавление друга |
| `/delete` | Удалить друга | Удаление по ID |
| `/setting` | Настройки | Управление уведомлениями |

### **Процесс добавления друга**
1. **Команда `/add`** → Установка состояния `WAITING_NAME`
2. **Ввод ФИО** → Парсинг и валидация имени
3. **Ввод даты рождения** → Парсинг в формате ДД.ММ.ГГГГ
4. **Ввод роли** → Указание роли друга
5. **Сохранение в БД** → Завершение процесса

### **Процесс удаления друга**
1. **Команда `/delete`** → Установка состояния `WAITING_ID`
2. **Ввод ID друга** → Проверка существования и владельца
3. **Удаление из БД** → Завершение процесса

## 🗄️ Структура базы данных

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

## 🔧 Технические детали

### **Система состояний**
- **`StepAdd`** - состояния для добавления друга
- **`IdEnum`** - состояния для удаления друга
- **Временное хранение** данных в `HashMap` во время процесса

### **Парсинг данных**
- **`TextParser`** - валидация ФИО и дат
- **Регулярные выражения** для проверки форматов
- **Валидация дат** в формате ДД.ММ.ГГГГ

### **Планировщик уведомлений**
- **ScheduledExecutorService** для фоновых задач
- **Ежедневный запуск** в 10:00 утра
- **Автоматическое перепланирование** задач

### **Обработка команд**
- **Runner** - регистрация команд и кнопок
- **HashMap** для хранения обработчиков
- **Интерфейсы** `ExecuteCommand` и `ExecuteButton`

## 🚀 Развертывание

### **Локальная разработка**
```bash
./gradlew run
```

## 🐛 Устранение неполадок

### **Частые проблемы**

#### **Ошибка подключения к БД**
```bash
# Проверьте статус PostgreSQL
sudo systemctl status postgresql

# Проверьте подключение
psql -h localhost -U birthcalendarbot -d telegramusersdata
```

#### **Бот не отвечает**
```bash
# Проверьте токен в @BotFather
# Убедитесь, что бот не заблокирован
# Проверьте консоль приложения
```

#### **Ошибки валидации**
- **ФИО:** Только русские буквы, первая заглавная
- **Дата:** Формат ДД.ММ.ГГГГ
- **Роль:** Любое слово на русском языке

**📖 Подробное руководство по устранению неполадок:** [docs/RUNNING_BOT.md](docs/RUNNING_BOT.md)

## 📚 Документация

### **Доступная документация:**
- **📖 [Техническая документация](docs/TECHNICAL_DOCS.md)** - подробное описание архитектуры и API
- **👤 [Руководство пользователя](docs/USER_GUIDE.md)** - инструкции по использованию бота
- **🚀 [Работа с запущенным ботом](docs/RUNNING_BOT.md)** - мониторинг и устранение неполадок

## 🤝 Вклад в проект

### **Структура коммитов**
```
feat: добавление новой функциональности
fix: исправление ошибок
docs: обновление документации
refactor: рефакторинг кода
test: добавление тестов
```

### **Создание Pull Request**
1. Создайте ветку для новой функции
2. Внесите изменения
3. Создайте Pull Request
4. Опишите изменения

## 📞 Поддержка

### **Контакты разработчика**
- **Telegram:** @N0fckgway
- **Email:** 472530@edu.itmo.ru

### **Сообщение об ошибках**
При возникновении проблем:
1. Проверьте консоль приложения
2. Убедитесь в правильности конфигурации
3. Проверьте подключение к базе данных
4. Обратитесь к разработчику с описанием проблемы

## 📄 Лицензия

Проект разработан в образовательных целях в ИТМО.

## 🎯 Roadmap

### **Планируемые функции**
- [ ] Экспорт данных в CSV/Excel
- [ ] Импорт друзей из файла

---

**BirthCalendarBot** - ваш надежный помощник в управлении важными датами! 🎉 