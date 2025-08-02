package org.telebot.data.database;

import lombok.extern.slf4j.Slf4j;
import org.telebot.command.runner.Command;
import org.telebot.connector.ConnectBot;
import org.telebot.data.User;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import javax.print.attribute.standard.Chromaticity;
import java.sql.*;
import java.time.LocalDate;
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


    public void addUser(Update update, User user) {
        dbConnector.handleQuery((Connection conn) -> {
            String insertHuman = "INSERT INTO users(chatid, firstName, lastName, phonenumber, username, birth, age) VALUES (?, ?, ?, ?, ?, ?, ?)";
            PreparedStatement preparedStatements = conn.prepareStatement(insertHuman, Statement.RETURN_GENERATED_KEYS);
            preparedStatements.setLong(1, user.getChatId());
            preparedStatements.setString(2, user.getFirstName());
            preparedStatements.setString(3, user.getLastName());
            preparedStatements.setString(4, user.getPhoneNumber());
            preparedStatements.setString(5, user.getUserName());
            preparedStatements.setObject(6, (user.getBirthday()));
            preparedStatements.setObject(7, user.getAge());
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

    public User getUserById(Long chatId) {
        return dbConnector.handleQuery((Connection conn) -> {
            String sqlQuery = "SELECT u.* " +
                    "FROM users AS u " +
                    "WHERE u.chatid = ?";
            PreparedStatement preparedStatement = conn.prepareStatement(sqlQuery);
            preparedStatement.setLong(1, chatId);
            ResultSet rs = preparedStatement.executeQuery();
            if (rs.next()) {
                User user = new User(
                        rs.getLong("chatid"),
                        rs.getString("firstName"),
                        rs.getString("lastName"),
                        rs.getString("username"),
                        rs.getString("phonenumber"),
                        rs.getObject("birth", LocalDate.class)

                );
                return user;
            } else {
                return null;
            }
        });
    }

}
