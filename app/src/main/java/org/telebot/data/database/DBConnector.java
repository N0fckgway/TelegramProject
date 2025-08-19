package org.telebot.data.database;

import lombok.extern.slf4j.Slf4j;
import org.telebot.data.exception.DataBaseException;
import org.telebot.data.interfaces.DBConnectable;
import org.telebot.data.interfaces.SQLConsumer;
import org.telebot.data.interfaces.SQLFunction;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

@Slf4j
public class DBConnector implements DBConnectable {

    public DBConnector() {
        log.info("Инициализация DBConnector...");
        try {
            Class.forName("org.postgresql.Driver");
            log.debug("PostgreSQL драйвер загружен успешно");
            
            try (Connection connection = getConnection()) {
                log.info("Тестовое подключение к базе данных успешно");
            } catch (SQLException e) {
                log.error("Ошибка тестового подключения к БД: {}", e.getMessage());
                throw new RuntimeException(e);
            }
        } catch (ClassNotFoundException e) {
            log.error("PostgreSQL драйвер не найден: {}", e.getMessage());
            throw new RuntimeException(e);
        }
        log.info("DBConnector инициализирован успешно");
    }

    public static Connection getConnection() throws SQLException {
        log.debug("Попытка подключения к базе данных...");
        Properties properties = new Properties();

        try (InputStream inputStream = Files.newInputStream(Paths.get("app/src/main/resources/properties/apiBot.properties"))){
            properties.load(inputStream);
            log.debug("Конфигурация БД загружена из файла");
        } catch (IOException e) {
            log.error("Ошибка загрузки конфигурации БД: {}", e.getMessage());
            throw new RuntimeException(e);
        }
        
        String name = properties.getProperty("nameDB");
        String passwd = properties.getProperty("passwd");
        String dbUrl = properties.getProperty("dbUrl");
        
        log.debug("Подключение к БД: {} как пользователь {}", dbUrl, name);

        try {
            Connection connection = DriverManager.getConnection(dbUrl, name, passwd);
            log.debug("Подключение к БД установлено успешно");
            return connection;
        } catch (SQLException e) {
            log.error("Ошибка подключения к БД: {} - {}", dbUrl, e.getMessage());
            throw e;
        }
    }

    @Override
    public void handleQuery(SQLConsumer<Connection> queryBody) throws SQLException {
        log.debug("Выполнение SQL операции (SQLConsumer)...");
        try (Connection connection = getConnection()) {
            log.debug("SQL операция начата");
            queryBody.accept(connection);
            log.debug("SQL операция выполнена успешно");
        } catch (SQLException e) {
            log.error("Ошибка выполнения SQL операции: {}", e.getMessage());
            log.error("SQL State: {}, Error Code: {}", e.getSQLState(), e.getErrorCode());
            throw new DataBaseException("DBConnector.java mistake In method void handleQuery 53-61!");
        }
    }

    @Override
    public <T> T handleQuery(SQLFunction<Connection, T> queryBody) throws DataBaseException {
        log.debug("Выполнение SQL операции (SQLFunction)...");
        try (Connection connection = getConnection()){
            log.debug("SQL операция с возвратом значения начата");
            T result = queryBody.apply(connection);
            log.debug("SQL операция с возвратом значения выполнена успешно");
            return result;
        } catch (SQLException e) {
            log.error("Ошибка выполнения SQL операции с возвратом значения: {}", e.getMessage());
            log.error("SQL State: {}, Error Code: {}", e.getSQLState(), e.getErrorCode());
            throw new RuntimeException(e);
        }
    }

    public static void initDB() throws SQLException {
        log.info("Инициализация базы данных...");
        Connection connection = getConnection();
        
        try (Statement statement = connection.createStatement()) {
            log.debug("Создание последовательности user_id_seq...");
            statement.execute("CREATE SEQUENCE IF NOT EXISTS user_id_seq INCREMENT 1 START 1 MINVALUE 1 MAXVALUE 2147483647 CACHE 1");
            log.debug("Последовательность user_id_seq создана/проверена");
            
            log.debug("Создание последовательности friend_id_seq...");
            statement.execute("CREATE SEQUENCE IF NOT EXISTS friend_id_seq INCREMENT 1 START 1 MINVALUE 1 MAXVALUE 2147483647 CACHE 1");
            log.debug("Последовательность friend_id_seq создана/проверена");

            log.debug("Создание таблицы users...");
            statement.execute("CREATE TABLE IF NOT EXISTS users" +
                    "(id BIGINT NOT NULL PRIMARY KEY DEFAULT NEXTVAL('user_id_seq')," +
                    "chatid BIGINT NOT NULL UNIQUE," +
                    "firstName VARCHAR(255) NOT NULL CHECK(firstName<>'')," +
                    "lastName VARCHAR(255) CHECK(lastName<>'')," +
                    "userName VARCHAR(255) CHECK(userName<>'')," +
                    "phoneNumber VARCHAR(255) NOT NULL CHECK(phoneNumber<>'')," +
                    "birth DATE NOT NULL, " +
                    "age INTEGER NOT NULL" +
                            ")"
                    );
            log.debug("Таблица users создана/проверена");
            
            log.debug("Создание таблицы notification_setting...");
            statement.execute("CREATE TABLE IF NOT EXISTS notification_setting" +
                    "(" +
                    "chatid BIGINT PRIMARY KEY REFERENCES users(chatid)," +
                    "enabled_for_users BOOLEAN DEFAULT true," +
                    "enabled_for_friends BOOLEAN DEFAULT true" +
                    ")"
            );
            log.debug("Таблица notification_setting создана/проверена");

            log.debug("Создание таблицы friends...");
            statement.execute("CREATE TABLE IF NOT EXISTS friends" +
                    "(" +
                    "id BIGINT NOT NULL PRIMARY KEY DEFAULT NEXTVAL('friend_id_seq')," +
                    "owner_chatid BIGINT NOT NULL REFERENCES users(chatid)," +
                    "role VARCHAR(64) NOT NULL CHECK(role<>'')," +
                    "firstName VARCHAR(255) NOT NULL CHECK(firstName<>'')," +
                    "lastName VARCHAR(255) NOT NULL CHECK(lastName<>'')," +
                    "birth DATE NOT NULL," +
                    "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP" +
                    ")"
            );
            log.debug("Таблица friends создана/проверена");
            
            log.info("База данных инициализирована успешно");
        } catch (SQLException e) {
            log.error("Ошибка инициализации БД: {}", e.getMessage());
            log.error("SQL State: {}, Error Code: {}", e.getSQLState(), e.getErrorCode());
            throw e;
        } finally {
            connection.close();
            log.debug("Соединение с БД закрыто");
        }
    }
}
