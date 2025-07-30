package org.telebot;



import org.telebot.config.BotInitializer;
import org.telebot.connector.ConnectBot;
import org.telebot.data.database.DBConnector;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.sql.SQLException;

public class TelegramBot {
    public static void main(String[] args) throws TelegramApiException, SQLException {
        ConnectBot connectBot = new ConnectBot();
        BotInitializer botInitializer = new BotInitializer(connectBot);
        botInitializer.init();
        DBConnector.initDB();
        System.out.println("Ботик запущен!");
        System.out.println("Базочка инициализирована!");

    }


}
