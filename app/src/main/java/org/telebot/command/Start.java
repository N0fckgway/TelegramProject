package org.telebot.command;

import lombok.Getter;
import lombok.Setter;
import org.telebot.command.interfaces.ExecuteCommand;
import org.telebot.command.runner.Command;
import org.telebot.connector.ConnectBot;
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
        String fullName = (update.getMessage().getFrom().getLastName() == null) ? update.getMessage().getFrom().getFirstName() : update.getMessage().getFrom().getFirstName() + " " + update.getMessage().getFrom().getLastName();
        String start = "<b>Привет, " + fullName + "!</b> 👋\n\n" +
                "Теперь ты — мой новый друг! 🎉" +
                "\n\n" +
                "<b>Я помогу тебе не забывать о днях рождениях родных, друзей и всех, кто тебе дорог.</b>\n\n" +
                "<b>Чтобы начать</b>, нажми на эту команду /registration - и пройди регистрацию для заполнения профиля о себе!";
        Long chatId = update.getMessage().getChatId();
        SendMessage sendMessage = new SendMessage(String.valueOf(chatId), start);
        sendMessage.setParseMode(ParseMode.HTML);
        try {
            execute(sendMessage);

        } catch (TelegramApiException e) {
            throw new RuntimeException();
        }

    }


}
