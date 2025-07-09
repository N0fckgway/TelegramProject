package org.telebot.command;

import org.telebot.command.interfaces.ExecuteCommand;
import org.telebot.command.runner.Command;
import org.telebot.connector.ConnectBot;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

public class Setting extends ConnectBot implements ExecuteCommand {

    public Command getCommand() {
        return new Command("/setting", "Вывод настроек бота!");
    }

    @Override
    public void apply(Update update) {
        Long chatId = update.getMessage().getChatId();
        String settingMessage = "🛠 <strong>Настройки:</strong>\n\n" +
                "<i>1</i>. 👀 Мой профиль\n" +
                "<i>2</i>. 👥 Мои друзья\n" +
                "<i>3</i>. ⏰ Уведомления\n";

        String chatIdStr = String.valueOf(chatId);
        SendMessage sendMessage = new SendMessage(chatIdStr, settingMessage);
        sendMessage.setParseMode(ParseMode.HTML);

        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }


    }
}
