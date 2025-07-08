package org.telebot.command;

import lombok.Getter;
import lombok.Setter;
import org.telebot.command.interfaces.ExecuteCommand;
import org.telebot.connector.ConnectBot;
import org.telebot.data.User;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Getter
@Setter
public class Start extends ConnectBot implements ExecuteCommand {
    private ConnectBot connectBot;

    public Start(ConnectBot connectBot) {
        this.connectBot = connectBot;
    }

    @Override
    public void apply(Update update) {
        User user = new User(update.getMessage().getFrom().getFirstName());
        Long chatId = update.getMessage().getChatId();
        String start = "Приветствую " + user.getName() +  ", теперь ты мой новый друг! Я тебе предоставлю возможность не забывать" +
                " о днях рождениях своих родных, друзей и вовсе очень близких тебе людей! " +
                "Давай для начала с тобой познакомимся, напиши как мне к тебе обращаться! ";
        String chatIdStr = String.valueOf(chatId);
        SendMessage sendMessage = new SendMessage(chatIdStr, start);


        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }

    }


}
