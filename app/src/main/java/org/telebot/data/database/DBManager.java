package org.telebot.data.database;

import lombok.extern.slf4j.Slf4j;
import org.telebot.command.runner.Command;
import org.telebot.connector.ConnectBot;
import org.telebot.data.User;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.sql.*;
import java.util.function.LongFunction;

@Slf4j
public class DBManager extends ConnectBot {
    private static DBConnector dbConnector;

    public DBManager(DBConnector dbConnector) {
        this.dbConnector = dbConnector;
    }
    public void sendMessage(long chatId, String text) {
        SendMessage sendMessage = new SendMessage(String.valueOf(chatId), text);
        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            throw new RuntimeException();
        }
    }

  /*  public static void loadCollection() throws SQLException {
        return dbConnector.handleQuery((Connection connection) -> {

        });
    }

    public static Integer getChatId() throws SQLException {
        return dbConnector.handleQuery((Connection connection) -> {
            String response = "SELECT chatid FROM users WHERE ";
            PreparedStatement preparedStatement = connection.prepareStatement(response);
            preparedStatement.executeQuery();
        });
    }*/

    public void addUser(Update update, User user) {
        dbConnector.handleQuery((Connection conn) -> {
            String insertHuman = "INSERT INTO users(chatid, firstName, lastName, phonenumber, username, birth) VALUES (?, ?, ?, ?, ?, ?)";
            PreparedStatement preparedStatements = conn.prepareStatement(insertHuman, Statement.RETURN_GENERATED_KEYS);
            preparedStatements.setLong(1, user.getChatId());
            preparedStatements.setString(2, user.getFirstName());
            preparedStatements.setString(3, user.getLastName());
            preparedStatements.setString(4, user.getPhoneNumber());
            preparedStatements.setString(5, user.getUserName());
            preparedStatements.setObject(6, (user.getBirthday()));
            preparedStatements.executeUpdate();
            ResultSet resHuman = preparedStatements.getGeneratedKeys();
            if (!resHuman.next()) {
                Long chatId = update.getMessage().getChatId();
                sendMessage(chatId, "Ошибка: вы не добавлены в базу данных! (подождите мы все поправим)");
                throw new SQLException();
            }
            return resHuman.getLong(2);
        });
    }
    public boolean checkUserExisting(Long id) {
        return dbConnector.handleQuery((Connection conn) -> {
                String existingQuery = "SELECT COUNT(*) AS count FROM users WHERE users.chatid = ?";
                PreparedStatement preparedStatement = conn.prepareStatement(existingQuery);
                preparedStatement.setLong(1, id);
                ResultSet resultSet = preparedStatement.executeQuery();

                resultSet.next();
                return resultSet.getInt("count") > 0;

        });
    }

}
