package org.telebot;

import lombok.extern.slf4j.Slf4j;
import org.telebot.config.BotInitializer;
import org.telebot.connector.ConnectBot;
import org.telebot.connector.NotificationScheduler;
import org.telebot.data.database.DBConnector;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.sql.SQLException;
import java.time.LocalDateTime;

@Slf4j
public class TelegramBot {
    public static void main(String[] args) throws TelegramApiException, SQLException {
        log.info("=== Запуск BirthCalendarBot ===");
        log.info("Время запуска: {}", LocalDateTime.now());
        
        try {
            log.info("Инициализация ConnectBot...");
            ConnectBot connectBot = new ConnectBot();
            log.info("ConnectBot инициализирован успешно");
            
            log.info("Инициализация BotInitializer...");
            BotInitializer botInitializer = new BotInitializer(connectBot);
            botInitializer.init();
            log.info("BotInitializer инициализирован успешно");
            
            log.info("Инициализация базы данных...");
            DBConnector.initDB();
            log.info("База данных инициализирована успешно");
            
            log.info("Запуск планировщика уведомлений...");
            NotificationScheduler notificationScheduler = new NotificationScheduler();
            log.info("Планировщик уведомлений запущен успешно");

            log.info("=== BirthCalendarBot успешно запущен! ===");
            log.info("Ботик запущен!");
            log.info("Базочка инициализирована!");
            log.info("Планировщик уведомлений запущен!");
            
        } catch (Exception e) {
            log.error("Критическая ошибка при запуске BirthCalendarBot: {}", e.getMessage(), e);
            throw e;
        }
    }
}
