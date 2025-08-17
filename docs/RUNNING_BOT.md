# 🚀 Работа с запущенным BirthCalendarBot

## 📋 Содержание
1. [Запуск бота](#запуск-бота)
2. [Мониторинг работы](#мониторинг-работы)
3. [Устранение неполадок](#устранение-неполадок)
4. [Остановка бота](#остановка-бота)
5. [Перезапуск](#перезапуск)

## 🚀 Запуск бота

### **Локальный запуск**
```bash
# В корне проекта
./gradlew run
```

### **Запуск в фоновом режиме**
```bash
# Запуск в фоне
./gradlew run &

# Проверка процесса
ps aux | grep "gradlew run"
```

### **Запуск через JAR**
```bash
# Сборка
./gradlew build

# Запуск
java -jar app/build/libs/app.jar
```

## 📊 Мониторинг работы

### **Проверка статуса процесса**
```bash
# Поиск Java процессов
ps aux | grep java

# Поиск процессов бота
ps aux | grep BirthCalendarBot

# Проверка портов (если используется)
lsof -i :8080
```

### **Проверка подключения к базе данных**
```bash
# Подключение к PostgreSQL
psql -h localhost -U birthcalendarbot -d telegramusersdata

# Проверка таблиц
\dt

# Проверка данных
SELECT COUNT(*) FROM users;
SELECT COUNT(*) FROM friends;
```

### **Мониторинг уведомлений**
```bash
# Проверка времени (уведомления в 10:00)
date

# Проверка планировщика
ps aux | grep NotificationScheduler
```

## 🐛 Устранение неполадок

### **1. Конфликт экземпляров бота**

#### **Симптомы:**
```
Error: [409] Conflict: terminated by other getUpdates request
make sure that only one bot instance is running
```

#### **Решение:**
```bash
# Поиск всех процессов бота
ps aux | grep java | grep -i bot

# Завершение процессов
pkill -f "gradlew run"
pkill -f "BirthCalendarBot"

# Очистка кэша
./gradlew clean

# Перезапуск
./gradlew run
```

### **2. Бот не отвечает на команды**

#### **Проверка:**
1. **Статус процесса:**
   ```bash
   ps aux | grep java
   ```

2. **Подключение к Telegram API:**
   - Проверьте интернет соединение
   - Убедитесь, что токен бота действителен
   - Проверьте, что бот не заблокирован

3. **Консоль приложения:**
   - Ищите ошибки в выводе консоли
   - Проверьте сообщения об инициализации

### **3. Ошибки подключения к базе данных**

#### **Проверка PostgreSQL:**
```bash
# Статус службы
brew services list | grep postgresql

# Запуск службы
brew services start postgresql

# Проверка подключения
psql -h localhost -U birthcalendarbot -d telegramusersdata
```

#### **Проверка конфигурации:**
```bash
# Проверка файла конфигурации
cat app/src/main/resources/properties/apiBot.properties

# Проверка прав доступа
ls -la app/src/main/resources/properties/
```

### **4. Проблемы с уведомлениями**

#### **Проверка планировщика:**
1. **Время запуска:** Уведомления отправляются в 10:00 утра
2. **Статус процесса:** Планировщик должен быть активен
3. **Настройки пользователей:** Проверьте включены ли уведомления

#### **Тестирование уведомлений:**
```sql
-- Проверка настроек уведомлений
SELECT * FROM notification_setting WHERE enabled_for_users = true;

-- Проверка пользователей с датами рождения
SELECT chatid, firstName, birth FROM users WHERE birth IS NOT NULL;
```

## 🛑 Остановка бота

### **Корректная остановка**
```bash
# В терминале с запущенным ботом
Ctrl + C

# Или поиск и завершение процесса
ps aux | grep "gradlew run"
kill -TERM <PID>
```

### **Принудительная остановка**
```bash
# Поиск процесса
ps aux | grep java

# Принудительное завершение
kill -9 <PID>

# Или завершение всех процессов
pkill -f "gradlew run"
```

## 🔄 Перезапуск

### **Полный перезапуск**
```bash
# 1. Остановка
pkill -f "gradlew run"

# 2. Очистка
./gradlew clean

# 3. Перезапуск
./gradlew run
```

### **Быстрый перезапуск**
```bash
# Остановка и запуск в одной команде
pkill -f "gradlew run" && sleep 2 && ./gradlew run
```

## 📈 Производительность

### **Мониторинг ресурсов**
```bash
# Использование памяти
top -p $(pgrep -f "gradlew run")

# Использование CPU
htop -p $(pgrep -f "gradlew run")

# Дисковые операции
iotop -p $(pgrep -f "gradlew run")
```

### **Оптимизация**
1. **Память:** Установите `JAVA_OPTS="-Xmx512m -Xms256m"`
2. **База данных:** Регулярно выполняйте `VACUUM` и `ANALYZE`
3. **Кэш:** Очищайте кэш Gradle при проблемах

## 🔧 Отладка

### **Включение debug режима**
```bash
# Запуск с дополнительными параметрами
./gradlew run --debug

# Или через переменные окружения
export GRADLE_OPTS="-Dorg.gradle.debug=true"
./gradlew run
```

### **Проверка конфигурации**
```bash
# Проверка файлов конфигурации
ls -la app/src/main/resources/

# Проверка зависимостей
./gradlew dependencies

# Проверка сборки
./gradlew build --info
```

## 📚 Полезные команды

### **Мониторинг в реальном времени**
```bash
# Слежение за процессами
watch -n 1 'ps aux | grep java'

# Слежение за файлами
watch -n 1 'ls -la logs/'

# Слежение за базой данных
watch -n 5 'psql -h localhost -U birthcalendarbot -d telegramusersdata -c "SELECT COUNT(*) FROM users;"'
```

### **Диагностика**
```bash
# Проверка Java версии
java -version

# Проверка Gradle версии
./gradlew --version

# Проверка PostgreSQL версии
psql --version

# Проверка свободного места
df -h
```

---

**Эта документация поможет вам эффективно управлять запущенным ботом и решать возникающие проблемы.** 