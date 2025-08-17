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
        log.debug("DBManager инициализирован с DBConnector");
    }
    
    public void sendMessage(long chatId, String text) {
        log.debug("Отправка сообщения пользователю {}: {}", chatId, text);
        SendMessage sendMessage = new SendMessage(String.valueOf(chatId), text);
        try {
            execute(sendMessage);
            log.debug("Сообщение успешно отправлено пользователю {}", chatId);
        } catch (TelegramApiException e) {
            log.error("Ошибка отправки сообщения пользователю {}: {}", chatId, e.getMessage());
            throw new RuntimeException();
        }
    }

    public void addUser(User user) throws SQLException {
        log.info("Добавление пользователя в БД: chatId={}, firstName={}, lastName={}", 
                user.getChatId(), user.getFirstName(), user.getLastName());
        
        dbConnector.handleQuery((Connection conn) -> {
            String insertHuman = "INSERT INTO users(chatid, firstName, lastName, phonenumber, username, birth, age) VALUES (?, ?, ?, ?, ?, ?, ?)";
            log.debug("SQL запрос: {}", insertHuman);
            
            PreparedStatement preparedStatements = conn.prepareStatement(insertHuman);
            preparedStatements.setLong(1, user.getChatId());
            preparedStatements.setString(2, user.getFirstName());
            preparedStatements.setString(3, user.getLastName());
            preparedStatements.setString(4, user.getPhoneNumber());
            preparedStatements.setString(5, user.getUserName());
            preparedStatements.setObject(6, Date.valueOf((user.getBirthday())));
            preparedStatements.setInt(7, user.getAge());
            
            int rowsAffected = preparedStatements.executeUpdate();
            log.info("Пользователь добавлен в БД. Затронуто строк: {}", rowsAffected);
        });
    }

    public void addNotificationStatusForUser(NotificationConfig notificationConfig) throws SQLException {
        log.info("Добавление настроек уведомлений для пользователя: chatId={}", notificationConfig.getChatId());
        
        dbConnector.handleQuery((Connection conn) -> {
            String insertHuman = "INSERT INTO notification_setting(chatid, enabled_for_users, enabled_for_friends) VALUES (?, ?, ?)";
            log.debug("SQL запрос: {}", insertHuman);
            
            PreparedStatement preparedStatements = conn.prepareStatement(insertHuman);
            preparedStatements.setLong(1, notificationConfig.getChatId());
            preparedStatements.setBoolean(2, notificationConfig.getEnabledForUsers());
            preparedStatements.setBoolean(3, notificationConfig.getEnabledForFriends());
            
            int rowsAffected = preparedStatements.executeUpdate();
            log.info("Настройки уведомлений добавлены. Затронуто строк: {}", rowsAffected);
        });
    }

    public boolean checkUserExisting(Long id) {
        log.debug("Проверка существования пользователя: chatId={}", id);
        
        return dbConnector.handleQuery((Connection conn) -> {
            String existingQuery = "SELECT COUNT(*) AS count FROM users WHERE users.chatid = ?";
            log.debug("SQL запрос: {}", existingQuery);
            
            PreparedStatement preparedStatement = conn.prepareStatement(existingQuery);
            preparedStatement.setLong(1, id);
            ResultSet resultSet = preparedStatement.executeQuery();

            resultSet.next();
            int count = resultSet.getInt("count");
            boolean exists = count > 0;
            
            log.debug("Пользователь с chatId={} существует: {}", id, exists);
            return exists;
        });
    }

    public User getUserById(Long chatId) {
        log.debug("Получение пользователя по ID: chatId={}", chatId);
        
        return dbConnector.handleQuery((Connection conn) -> {
            String sqlQuery = "SELECT u.* FROM users AS u WHERE u.chatid = ?";
            log.debug("SQL запрос: {}", sqlQuery);
            
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
                log.debug("Пользователь найден: {}", user);
                return user;
            } else {
                log.debug("Пользователь с chatId={} не найден", chatId);
                return null;
            }
        });
    }

    public void updateEnableUser(Boolean enable, Long chatId) {
        log.info("Обновление настроек уведомлений для пользователя: chatId={}, enabled={}", chatId, enable);
        
        try {
            dbConnector.handleQuery((Connection conn) -> {
                String sqlQuery = "UPDATE notification_setting SET enabled_for_users = ? WHERE chatid = ?";
                log.debug("SQL запрос: {}", sqlQuery);
                
                PreparedStatement preparedStatement = conn.prepareStatement(sqlQuery);
                preparedStatement.setBoolean(1, enable);
                preparedStatement.setLong(2, chatId);
                
                int rowsAffected = preparedStatement.executeUpdate();
                log.info("Настройки уведомлений обновлены. Затронуто строк: {}", rowsAffected);
            });
        } catch (SQLException e) {
            log.error("Ошибка обновления настроек уведомлений для пользователя {}: {}", chatId, e.getMessage());
            System.out.println(e.getMessage());
        }
    }

    public void updateEnableFriend(Boolean enable, Long chatId) {
        log.info("Обновление настроек уведомлений для друзей: chatId={}, enabled={}", chatId, enable);
        
        try {
            dbConnector.handleQuery((Connection conn) -> {
                String sqlQuery = "UPDATE notification_setting SET enabled_for_friends = ? WHERE chatid = ?";
                log.debug("SQL запрос: {}", sqlQuery);
                
                PreparedStatement preparedStatement = conn.prepareStatement(sqlQuery);
                preparedStatement.setBoolean(1, enable);
                preparedStatement.setLong(2, chatId);
                
                int rowsAffected = preparedStatement.executeUpdate();
                log.info("Настройки уведомлений для друзей обновлены. Затронуто строк: {}", rowsAffected);
            });
        } catch (SQLException e) {
            log.error("Ошибка обновления настроек уведомлений для друзей пользователя {}: {}", chatId, e.getMessage());
            System.out.println(e.getMessage());
        }
    }

    public List<User> getAllUsersWithEnabled(boolean enable) {
        log.debug("Получение пользователей с включенными уведомлениями: enabled={}", enable);
        
        return dbConnector.handleQuery((Connection conn) -> {
            String sqlQuery = "SELECT u.chatid, u.firstName, u.lastName, u.username, u.phonenumber, u.birth, u.age " +
                    "FROM users u " +
                    "INNER JOIN notification_setting n ON u.chatid = n.chatid " +
                    "WHERE n.enabled_for_users = ?";
            log.debug("SQL запрос: {}", sqlQuery);
            
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
            
            log.debug("Найдено пользователей с включенными уведомлениями: {}", users.size());
            return users;
        });
    }

    public NotificationConfig getNotificationConfigById(Long chatId) {
        log.debug("Получение настроек уведомлений для пользователя: chatId={}", chatId);
        
        return dbConnector.handleQuery((Connection conn) -> {
            String sqlQuery = "SELECT * FROM notification_setting WHERE chatid = ?";
            log.debug("SQL запрос: {}", sqlQuery);
            
            PreparedStatement preparedStatement = conn.prepareStatement(sqlQuery, Statement.RETURN_GENERATED_KEYS);
            preparedStatement.setLong(1, chatId);
            ResultSet resultSet = preparedStatement.executeQuery();
            
            if (resultSet.next()) {
                NotificationConfig notificationConfig = new NotificationConfig(
                        resultSet.getLong("chatid"),
                        resultSet.getBoolean("enabled_for_users"),
                        resultSet.getBoolean("enabled_for_friends")
                );
                log.debug("Настройки уведомлений найдены: {}", notificationConfig);
                return notificationConfig;
            } else {
                log.debug("Настройки уведомлений для пользователя {} не найдены", chatId);
                return null;
            }
        });
    }

    public void addFriend(Friend friend, Long ownerChatId) throws SQLException {
        log.info("Добавление друга в БД: ownerChatId={}, firstName={}, lastName={}, role={}", 
                ownerChatId, friend.getFirstName(), friend.getLastName(), friend.getRole());
        
        dbConnector.handleQuery((Connection conn) -> {
            String insertFriend = "INSERT INTO friends(owner_chatid, role, firstName, lastName, birth) VALUES (?, ?, ?, ?, ?)";
            log.debug("SQL запрос: {}", insertFriend);
            
            PreparedStatement preparedStatement = conn.prepareStatement(insertFriend);
            preparedStatement.setLong(1, ownerChatId);
            preparedStatement.setString(2, friend.getRole());
            preparedStatement.setString(3, friend.getFirstName());
            preparedStatement.setString(4, friend.getLastName());
            preparedStatement.setObject(5, Date.valueOf(friend.getBirthday()));
            
            int rowsAffected = preparedStatement.executeUpdate();
            log.info("Друг добавлен в БД. Затронуто строк: {}", rowsAffected);
        });
    }

    public Long getFriendId(Long ownerChatId) throws SQLException {
        log.debug("Получение ID последнего добавленного друга: ownerChatId={}", ownerChatId);
        
        return dbConnector.handleQuery((Connection conn) -> {
            String getId = "SELECT id FROM friends WHERE owner_chatid = ? ORDER BY created_at DESC LIMIT 1";
            log.debug("SQL запрос: {}", getId);
            
            PreparedStatement preparedStatement = conn.prepareStatement(getId);
            preparedStatement.setLong(1, ownerChatId);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                Long friendId = resultSet.getLong("id");
                log.debug("ID последнего друга: {}", friendId);
                return friendId;
            } else {
                log.debug("Друзья для пользователя {} не найдены", ownerChatId);
                return null;
            }
        });
    }

    public List<Friend> getAllFriends(Long ownerChatId) {
        log.debug("Получение всех друзей пользователя: ownerChatId={}", ownerChatId);
        
        return dbConnector.handleQuery((Connection conn) -> {
            String sqlQuery = "SELECT f.id, f.firstName, f.lastName, f.role, f.birth " +
                    "FROM friends f " +
                    "WHERE f.owner_chatid = ?";
            log.debug("SQL запрос: {}", sqlQuery);
            
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
            
            log.debug("Найдено друзей для пользователя {}: {}", ownerChatId, friendsList.size());
            return friendsList;
        });
    }

    public List<Friend> getAllFriendsWithEnabled(boolean enable) {
        log.debug("Получение друзей с включенными уведомлениями: enabled={}", enable);
        
        return dbConnector.handleQuery((Connection conn) -> {
            String sqlQuery = "SELECT f.owner_chatid, f.role, f.firstname, f.lastname, f.birth " +
                    "FROM friends f " +
                    "INNER JOIN notification_setting n ON f.owner_chatid = n.chatid " +
                    "WHERE n.enabled_for_friends = ?";
            log.debug("SQL запрос: {}", sqlQuery);
            
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
            
            log.debug("Найдено друзей с включенными уведомлениями: {}", friendsList.size());
            return friendsList;
        });
    }

    public void deleteFriendById(Long id) throws SQLException {
        log.info("Удаление друга по ID: {}", id);
        
        dbConnector.handleQuery((Connection conn) -> {
            String sqlQuery = "DELETE FROM friends WHERE id = ?";
            log.debug("SQL запрос: {}", sqlQuery);
            
            PreparedStatement preparedStatement = conn.prepareStatement(sqlQuery);
            preparedStatement.setLong(1, id);
            
            int rowsAffected = preparedStatement.executeUpdate();
            log.info("Друг удален из БД. Затронуто строк: {}", rowsAffected);
        });
    }

    public boolean isFriendOwnedByUser(Long friendId, Long ownerChatId) {
        log.debug("Проверка владельца друга: friendId={}, ownerChatId={}", friendId, ownerChatId);
        
        return dbConnector.handleQuery((Connection conn) -> {
            String sqlQuery = "SELECT COUNT(*) FROM friends WHERE id = ? AND owner_chatid = ?";
            log.debug("SQL запрос: {}", sqlQuery);
            
            PreparedStatement preparedStatement = conn.prepareStatement(sqlQuery);
            preparedStatement.setLong(1, friendId);
            preparedStatement.setLong(2, ownerChatId);
            ResultSet rs = preparedStatement.executeQuery();
            
            rs.next();
            int count = rs.getInt(1);
            boolean isOwned = count > 0;
            
            log.debug("Друг {} принадлежит пользователю {}: {}", friendId, ownerChatId, isOwned);
            return isOwned;
        });
    }
}
