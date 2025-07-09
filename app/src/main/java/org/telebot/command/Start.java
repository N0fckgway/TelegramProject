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

@Getter
@Setter
public class Start extends ConnectBot implements ExecuteCommand {
    public Command command;


    public Command getCommand() {
        return new Command("/start", "Начать работу с ботом!");
    }

    @Override
    public void apply(Update update) {
        User user = new User(update.getMessage().getFrom().getFirstName());
        Long chatId = update.getMessage().getChatId();
        String start = "<b>Привет, " + user.getName() + "!</b> 👋\n" +
                "Теперь ты — мой новый друг! 🎉" +
                "\n" +
                "<b>Я помогу тебе не забывать о днях рождениях родных, друзей и всех, кто тебе дорог</b>.\n" +
                "<b>Чтобы начать, напиши команду /help, чтобы вывести подробную инструкцию о боте!</b> 😊";
        String chatIdStr = String.valueOf(chatId);
        SendMessage sendMessage = new SendMessage(chatIdStr, start);
        sendMessage.setParseMode(ParseMode.HTML);

        try {
            execute(sendMessage);

        } catch (TelegramApiException e) {
            e.printStackTrace();
        }

    }


}
