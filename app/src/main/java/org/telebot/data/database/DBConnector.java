package org.telebot.data.database;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

@Slf4j
public class DBConnector {

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

        try (InputStream inputStream = Files.newInputStream(Paths.get("TelegramProject/app/src/main/resources/properties/apiBot.properties"))){
            properties.load(inputStream);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        String name = properties.getProperty("nameDB");
        String passwd = properties.getProperty("passwd");
        String dbUrl = properties.getProperty("dbUrl");

        return DriverManager.getConnection(name, passwd, dbUrl);

    }



}
