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
        try {
            Class.forName("org.postgresql.Driver");
            try (Connection connection = getConnection()) {
                log.info("DB connection successful");
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }


    public static Connection getConnection() throws SQLException {
        Properties properties = new Properties();

        try (InputStream inputStream = Files.newInputStream(Paths.get("../app/src/main/resources/properties/apiBot.properties"))){
            properties.load(inputStream);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        String name = properties.getProperty("nameDB");
        String passwd = properties.getProperty("passwd");
        String dbUrl = properties.getProperty("dbUrl");

        return DriverManager.getConnection(dbUrl, name, passwd);

    }

    @Override
    public void handleQuery(SQLConsumer<Connection> queryBody) throws SQLException {
        try (Connection connection = getConnection()) {
            queryBody.accept(connection);

        } catch (SQLException e) {
            throw new DataBaseException("DBConnector.java mistake In method void handleQuery 53-61!");
        }
    }

    @Override
    public <T> T handleQuery(SQLFunction<Connection, T> queryBody) throws DataBaseException {
        try (Connection connection = getConnection()){
            return queryBody.apply(connection);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    public static void initDB() throws SQLException {
        Connection connection = getConnection();

        Statement statement = connection.createStatement();
        statement.execute("CREATE SEQUENCE IF NOT EXISTS user_id_seq INCREMENT 1 START 1 MINVALUE 1 MAXVALUE 2147483647 CACHE 1");
        statement.execute("CREATE SEQUENCE IF NOT EXISTS friend_id_seq INCREMENT 1 START 1 MINVALUE 1 MAXVALUE 2147483647 CACHE 1");

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
        statement.execute("CREATE TABLE IF NOT EXISTS notification_setting" +
                "(" +
                "chatid BIGINT PRIMARY KEY REFERENCES users(chatid)," +
                "enabled BOOLEAN DEFAULT true" +
                ")"
        );

        statement.execute("CREATE TABLE IF NOT EXISTS friends" +
                "(" +
                "id BIGINT NOT NULL PRIMARY KEY DEFAULT NEXTVAL('friend_id_seq')," +
                "owner_chatid BIGINT NOT NULL REFERENCES users(chatid)," +
                "firstName VARCHAR(255) NOT NULL CHECK(firstName<>'')," +
                "lastName VARCHAR(255) NOT NULL CHECK(lastName<>'')," +
                "birth DATE NOT NULL," +
                "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP" +
                ")"
        );
        connection.close();
    }




}
