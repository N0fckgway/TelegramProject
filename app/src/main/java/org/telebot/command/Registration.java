package org.telebot.command;

import org.telebot.buttons.SendContact;
import org.telebot.command.interfaces.ExecuteCommand;
import org.telebot.command.interfaces.KeyboardResponse;
import org.telebot.command.runner.Command;
import org.telebot.connector.ConnectBot;
import org.telebot.data.User;
import org.telebot.data.database.DBConnector;
import org.telebot.data.database.DBManager;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.ArrayList;
import java.util.List;

public class Registration extends ConnectBot implements ExecuteCommand, KeyboardResponse {
    public Command command;

    public Command getCommand() {
        return new Command("/registration", "Регистрация профиля!");
    }


    @Override
    public void apply(Update update) {
        Long chatId = update.getMessage().getChatId();
        String fullName = (update.getMessage().getFrom().getLastName() == null) ? update.getMessage().getFrom().getFirstName() : update.getMessage().getFrom().getFirstName() + " " + update.getMessage().getFrom().getLastName();
        String message = "<strong>" + fullName + "!</strong>\n" + "\n<strong>Давай зарегистрируемся!</strong> \nНажми на кнопку снизу!";
        try {
            execute(keyboardResponse(chatId, message));
        } catch (TelegramApiException e) {
            throw new RuntimeException();
        }

    }


    @Override
    public SendMessage keyboardResponse(long id, String text) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setParseMode(ParseMode.HTML);
        sendMessage.setText(text);
        sendMessage.setChatId(id);

        KeyboardButton contactButton = new KeyboardButton("📱Поделится контактом!");
        contactButton.setRequestContact(true);

        KeyboardRow row = new KeyboardRow();
        row.add(contactButton);

        List<KeyboardRow> keyboard = new ArrayList<>();
        keyboard.add(row);

        ReplyKeyboardMarkup markup = new ReplyKeyboardMarkup();
        markup.setKeyboard(keyboard);
        markup.setResizeKeyboard(true);
        markup.setOneTimeKeyboard(true);

        sendMessage.setReplyMarkup(markup);
        return sendMessage;

    }
}
