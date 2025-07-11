package org.telebot.command;

import lombok.Getter;
import lombok.Setter;
import org.telebot.command.interfaces.ExecuteCommand;
import org.telebot.command.interfaces.InlineKeyboardResponse;
import org.telebot.command.runner.Command;
import org.telebot.connector.ConnectBot;
import org.telebot.data.User;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class Start extends ConnectBot implements ExecuteCommand, InlineKeyboardResponse {
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
                "<b>Чтобы начать</b>, ты можешь зарегистрироваться <strong>(так профиль у тебя будет заполнен сразу!)</strong>, " +
                "но если ты не хочешь делать это прямо сейчас, то перейди <strong>/setting -> 👀Мой профиль</strong>  😊";

        Long chatId = update.getMessage().getChatId();
        try {
            execute(inlineKeyboardResponse(chatId, start));

        } catch (TelegramApiException e) {
            throw new RuntimeException();
        }

    }


    @Override
    public SendMessage inlineKeyboardResponse(long id, String text) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setText(text);
        sendMessage.setChatId(id);
        sendMessage.setParseMode(ParseMode.HTML);
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        InlineKeyboardButton firstButton = new InlineKeyboardButton();
        InlineKeyboardButton secondButton = new InlineKeyboardButton();

        List<List<InlineKeyboardButton>> rowsButtons = new ArrayList<>();
        List<InlineKeyboardButton> firstRowButtons = new ArrayList<>();


        firstButton.setText("💯Да, начнем регистрацию!");
        firstButton.setCallbackData("REGISTRATION");
        firstRowButtons.add(firstButton);

        secondButton.setText("😔Нет, начнем позже!");
        secondButton.setCallbackData("NOT_REGISTRATION");
        firstRowButtons.add(secondButton);

        rowsButtons.add(firstRowButtons);
        inlineKeyboardMarkup.setKeyboard(rowsButtons);
        sendMessage.setReplyMarkup(inlineKeyboardMarkup);


        return sendMessage;




    }

}
