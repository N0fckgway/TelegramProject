package org.telebot.buttons;

import org.telebot.command.interfaces.ExecuteButton;
import org.telebot.command.interfaces.KeyboardResponse;
import org.telebot.connector.ConnectBot;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.ArrayList;
import java.util.List;

public class Notification extends ConnectBot implements ExecuteButton, KeyboardResponse {
    @Override
    public void applyButton(Update update) {
        String message = """
                <strong>Добро пожаловать в раздел 🔔Уведомления!</strong>
                
                <i>Выберите для кого вы будете настраивать уведомления!</i>""";
        Long chatId = update.getCallbackQuery().getMessage().getChatId();
        sendMessage(keyboardResponse(chatId, message));

    }


    @Override
    public SendMessage keyboardResponse(long id, String text) {
        SendMessage sendMessage = new SendMessage(String.valueOf(id), text);
        sendMessage.setParseMode(ParseMode.HTML);

        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();
        List<InlineKeyboardButton> firstRow = new ArrayList<>();

        firstRow.add(createButton("FOR_ME", "💆🏻Для себя!"));
        firstRow.add(createButton("FOR_FRIENDS", "👯Для друзей"));
        rows.add(firstRow);
        inlineKeyboardMarkup.setKeyboard(rows);
        sendMessage.setReplyMarkup(inlineKeyboardMarkup);

        return sendMessage;

    }

    private static InlineKeyboardButton createButton(String data, String text) {
        InlineKeyboardButton button = new InlineKeyboardButton();
        button.setCallbackData(data);
        button.setText(text);
        return button;
    }

    private void sendMessage(SendMessage sendMessage) {
        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }
}
