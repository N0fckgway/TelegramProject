package org.telebot.buttons;

import org.telebot.command.interfaces.ExecuteButton;
import org.telebot.connector.ConnectBot;
import org.telebot.connector.NotificationScheduler;
import org.telebot.data.database.DBConnector;
import org.telebot.data.database.DBManager;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

public class NoButtonForFriends extends ConnectBot implements ExecuteButton {
    @Override
    public void applyButton(Update update) {
        DBConnector dbConnector = new DBConnector();
        DBManager dbManager = new DBManager(dbConnector);
        Long chatId = update.getCallbackQuery().getMessage().getChatId();
        String text = "✅Выключили уведомления для друзей! ";
        NotificationScheduler notificationScheduler = new NotificationScheduler();
        if (!dbManager.getNotificationConfigById(chatId).getEnabledForFriends()) {
            sendMessage(chatId, "🤓Уведомления для друзей уже выключены!");
            return;

        }
        notificationScheduler.changeButtonStatusFriend(false, chatId);
        sendMessage(chatId, text);

    }

    private void sendMessage(Long chatId, String text) {
        SendMessage sendMessage = new SendMessage(String.valueOf(chatId), text);
        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }
}
