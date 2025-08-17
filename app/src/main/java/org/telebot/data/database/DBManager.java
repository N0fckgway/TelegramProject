package org.telebot.data.database;

import lombok.extern.slf4j.Slf4j;
import org.telebot.buttons.Friends;
import org.telebot.connector.ConnectBot;
import org.telebot.data.Friend;
import org.telebot.data.NotificationConfig;
import org.telebot.data.User;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;


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


    public void addUser(User user) throws SQLException {
        dbConnector.handleQuery((Connection conn) -> {
            String insertHuman = "INSERT INTO users(chatid, firstName, lastName, phonenumber, username, birth, age) VALUES (?, ?, ?, ?, ?, ?, ?)";
            PreparedStatement preparedStatements = conn.prepareStatement(insertHuman);
            preparedStatements.setLong(1, user.getChatId());
            preparedStatements.setString(2, user.getFirstName());
            preparedStatements.setString(3, user.getLastName());
            preparedStatements.setString(4, user.getPhoneNumber());
            preparedStatements.setString(5, user.getUserName());
            preparedStatements.setObject(6, Date.valueOf((user.getBirthday())));
            preparedStatements.setInt(7, user.getAge());
            preparedStatements.executeUpdate();
        });
    }

    public void addNotificationStatusForUser(NotificationConfig notificationConfig) throws SQLException {
        dbConnector.handleQuery((Connection conn) -> {
            String insertHuman = "INSERT INTO notification_setting(chatid, enabled_for_users, enabled_for_friends) VALUES (?, ?, ?)";
            PreparedStatement preparedStatements = conn.prepareStatement(insertHuman);
            preparedStatements.setLong(1, notificationConfig.getChatId());
            preparedStatements.setBoolean(2, notificationConfig.getEnabledForUsers());
            preparedStatements.setBoolean(3, notificationConfig.getEnabledForFriends());
            preparedStatements.executeUpdate();


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
                user.setAge(rs.getInt("age"));
                return user;
            } else {
                return null;
            }
        });

    }

    public void updateEnableUser(Boolean enable, Long chatId) {
        try {
            dbConnector.handleQuery((Connection conn) -> {
                String sqlQuery = "UPDATE notification_setting SET enabled_for_users = ? WHERE chatid = ?";
                PreparedStatement preparedStatement = conn.prepareStatement(sqlQuery);
                preparedStatement.setBoolean(1, enable);
                preparedStatement.setLong(2, chatId);
                preparedStatement.executeUpdate();


            });
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public void updateEnableFriend(Boolean enable, Long chatId) {
        try {
            dbConnector.handleQuery((Connection conn) -> {
                String sqlQuery = "UPDATE notification_setting SET enabled_for_friends = ? WHERE chatid = ?";
                PreparedStatement preparedStatement = conn.prepareStatement(sqlQuery);
                preparedStatement.setBoolean(1, enable);
                preparedStatement.setLong(2, chatId);
                preparedStatement.executeUpdate();


            });
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public List<User> getAllUsersWithEnabled(boolean enable) {
        return dbConnector.handleQuery((Connection conn) -> {
            String sqlQuery = "SELECT u.chatid, u.firstName, u.lastName, u.username, u.phonenumber, u.birth, u.age " +
                    "FROM users u " +
                    "INNER JOIN notification_setting n ON u.chatid = n.chatid " +
                    "WHERE n.enabled_for_users = ?";
            PreparedStatement preparedStatement = conn.prepareStatement(sqlQuery);
            preparedStatement.setBoolean(1, enable);
            ResultSet rs = preparedStatement.executeQuery();
            List<User> users = new ArrayList<>();
            while (rs.next()) {
                User user = new User(
                        rs.getLong("chatid"),
                        rs.getString("firstName"),
                        rs.getString("lastName"),
                        rs.getString("username"),
                        rs.getString("phonenumber"),
                        rs.getObject("birth", LocalDate.class)

                );
                user.setAge(rs.getInt("age"));
                users.add(user);
            }
            return users;
        });

    }

    public NotificationConfig getNotificationConfigById(Long chatId) {
        return dbConnector.handleQuery((Connection conn) -> {
            String sqlQuery = "SELECT * FROM notification_setting WHERE chatid = ?";
            PreparedStatement preparedStatement = conn.prepareStatement(sqlQuery, Statement.RETURN_GENERATED_KEYS);
            preparedStatement.setLong(1, chatId);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                NotificationConfig notificationConfig = new NotificationConfig(
                        resultSet.getLong("chatid"),
                        resultSet.getBoolean("enabled_for_users"),
                        resultSet.getBoolean("enabled_for_friends")
                );
                return notificationConfig;
            } else return null;

        });

    }

    public void addFriend(Friend friend, Long ownerChatId) throws SQLException {
        dbConnector.handleQuery((Connection conn) -> {
            String insertFriend = "INSERT INTO friends(owner_chatid, role, firstName, lastName, birth) VALUES (?, ?, ?, ?, ?)";
            PreparedStatement preparedStatement = conn.prepareStatement(insertFriend);
            preparedStatement.setLong(1, ownerChatId);
            preparedStatement.setString(2, friend.getRole());
            preparedStatement.setString(3, friend.getFirstName());
            preparedStatement.setString(4, friend.getLastName());
            preparedStatement.setObject(5, Date.valueOf(friend.getBirthday()));
            preparedStatement.executeUpdate();
        });
    }

    public Long getFriendId(Long ownerChatId) throws SQLException {
        return dbConnector.handleQuery((Connection conn) -> {
            String getId = "SELECT id FROM friends WHERE owner_chatid = ? ORDER BY created_at DESC LIMIT 1";
            PreparedStatement preparedStatement = conn.prepareStatement(getId);
            preparedStatement.setLong(1, ownerChatId);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                return resultSet.getLong("id");

            } else {
                return null;
            }

        });
    }

    public List<Friend> getAllFriends(Long ownerChatId) {
        return dbConnector.handleQuery((Connection conn) -> {
            String sqlQuery = "SELECT f.id, f.firstName, f.lastName, f.role, f.birth " +
                    "FROM friends f " +
                    "WHERE f.owner_chatid = ?";
            PreparedStatement preparedStatement = conn.prepareStatement(sqlQuery);
            preparedStatement.setLong(1, ownerChatId);
            ResultSet rs = preparedStatement.executeQuery();
            List<Friend> friendsList = new ArrayList<>();
            while (rs.next()) {
                Friend friend = new Friend(
                        rs.getLong("id"),
                        rs.getString("firstName"),
                        rs.getString("lastName"),
                        rs.getString("role"),
                        rs.getObject("birth", LocalDate.class)

                );
                friendsList.add(friend);
            }
            return friendsList;
        });

    }

    public List<Friend> getAllFriendsWithEnabled(boolean enable) {
        return dbConnector.handleQuery((Connection conn) -> {
            String sqlQuery = "SELECT f.owner_chatid, f.role, f.firstname, f.lastname, f.birth " +
                    "FROM friends f " +
                    "INNER JOIN notification_setting n ON f.owner_chatid = n.chatid " +
                    "WHERE n.enabled_for_friends = ?";
            PreparedStatement preparedStatement = conn.prepareStatement(sqlQuery);
            preparedStatement.setBoolean(1, enable);
            ResultSet rs = preparedStatement.executeQuery();
            List<Friend> friendsList = new ArrayList<>();
            while (rs.next()) {
                Friend friend = new Friend(
                        rs.getLong("owner_chatid"),
                        rs.getString("firstname"),
                        rs.getString("lastname"),
                        rs.getString("role"),
                        rs.getObject("birth", LocalDate.class)

                );
                friendsList.add(friend);
            }
            return friendsList;

        });

    }

    public void deleteFriendById(Long id) throws SQLException {
        dbConnector.handleQuery((Connection conn) -> {
            String sqlQuery = "DELETE FROM friends WHERE id = ?";
            PreparedStatement preparedStatement = conn.prepareStatement(sqlQuery);
            preparedStatement.setLong(1, id);
            preparedStatement.executeUpdate();
        });
    }

    public boolean isFriendOwnedByUser(Long friendId, Long ownerChatId) {
        return dbConnector.handleQuery((Connection conn) -> {
            String sqlQuery = "SELECT COUNT(*) FROM friends WHERE id = ? AND owner_chatid = ?";
            PreparedStatement preparedStatement = conn.prepareStatement(sqlQuery);
            preparedStatement.setLong(1, friendId);
            preparedStatement.setLong(2, ownerChatId);
            ResultSet rs = preparedStatement.executeQuery();
            rs.next();
            return rs.getInt(1) > 0;
        });
    }

}
