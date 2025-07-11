package org.telebot.command;

import lombok.Getter;
import lombok.Setter;
import org.telebot.command.interfaces.ExecuteCommand;
import org.telebot.command.runner.Command;
import org.telebot.connector.ConnectBot;
import org.telebot.data.User;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Setter
public class Help extends ConnectBot implements ExecuteCommand {

    public Command getCommand() {
        return new Command("/help", "Показать это меню!");
    }

    @Override
    public void apply(Update update) {
        String fullName = (update.getMessage().getFrom().getLastName() == null) ? update.getMessage().getFrom().getFirstName() : update.getMessage().getFrom().getFirstName() + " " + update.getMessage().getFrom().getLastName();
        Command command = getCommand();
        Long chatId = update.getMessage().getChatId();
        getCommand().commandFill();
        String message = "<b>🆘Помощь по боту!</b>\n\n" +
                fullName + ", вот что я умею:\n\n" +
                "<b>🚩Основные команды:</b>\n\n" +
                command.commandPrintln(update) +
                "\n<b>💡Как работает бот?</b>\n\n" +
                "Ты добавляешь имена и даты рождения своих близких, а я заранее напомню тебе об их празднике.\n" +
                "В настройках ты сможешь настроить уведомления, я даже могу напоминать тебе каждый день, сколько дней осталось до твоего дня рождения!\n" +
                "Никаких забытых поздравлений — только тёплые слова вовремя! 😊 \n\n" +
                "<b>📧Есть вопросы или предложения как улучшить мой проект!?</b>\n" +
                "<strong>Пиши сюда: @N0fckgway</strong>"; ///Отформатировать текст!
        SendMessage sendMessage = new SendMessage(String.valueOf(chatId), message);
        sendMessage.setParseMode(ParseMode.HTML);

        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            throw new  RuntimeException();
        }

    }
}
