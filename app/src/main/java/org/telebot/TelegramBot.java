package org.telebot;



import org.telebot.config.BotInitializer;
import org.telebot.connector.ConnectBot;
import org.telebot.connector.NotificationScheduler;
import org.telebot.data.database.DBConnector;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.sql.SQLException;
import java.time.LocalDateTime;

public class TelegramBot {
    public static void main(String[] args) throws TelegramApiException, SQLException {
        ConnectBot connectBot = new ConnectBot();
        BotInitializer botInitializer = new BotInitializer(connectBot);
        botInitializer.init();
        DBConnector.initDB();
        NotificationScheduler notificationScheduler = new NotificationScheduler();

        System.out.println("Ботик запущен!");
        System.out.println("Базочка инициализирована!");
        System.out.println("Планировщик уведомлений запущен!");

    }

}
