package org.telebot.command;

import org.telebot.command.interfaces.ExecuteCommand;
import org.telebot.connector.ConnectBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

public class Setting extends ConnectBot implements ExecuteCommand {

    @Override
    public void apply(Update update) {
        Long chatId = update.getMessage().getChatId();
        String settingMessage = "🛠 Настройки:\n\n" +
                "1. 👀 Мой профиль\n" +
                "2. 👥 Мои друзья\n" +
                "3. ⏰ Уведомления\n";

        String chatIdStr = String.valueOf(chatId);
        SendMessage sendMessage = new SendMessage(chatIdStr, settingMessage);

        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }


    }
}
