package org.telebot;



import org.telebot.config.BotInitializer;
import org.telebot.connector.ConnectBot;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

public class TelegramBot {
    public static void main(String[] args) throws TelegramApiException {
        ConnectBot connectBot = new ConnectBot();
        BotInitializer botInitializer = new BotInitializer(connectBot);
        botInitializer.init();
        System.out.println("Ботик запущен!");


    }
}
