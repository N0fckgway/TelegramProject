package org.telebot.buttons;

import org.telebot.command.interfaces.ExecuteButton;
import org.telebot.command.interfaces.KeyboardResponse;
import org.telebot.connector.ConnectBot;
import org.telebot.data.database.DBConnector;
import org.telebot.data.database.DBManager;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.ArrayList;
import java.util.List;

public class NotificationForFriends extends ConnectBot implements ExecuteButton, KeyboardResponse {
    @Override
    public void applyButton(Update update) {
        DBConnector dbConnector = new DBConnector();
        DBManager dbManager = new DBManager(dbConnector);
        Long chatId = update.getCallbackQuery().getMessage().getChatId();
        String name = dbManager.getUserById(chatId).getFirstName();
        String message = name + ", здесь вы можете управлять уведомлениями для друзей!\n\n" +
                "Эти уведомления включены по умолчанию!\n\n";
        sendMessage(keyboardResponse(chatId, message));

    }

    private void sendMessage(SendMessage sendMessage) {
        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e.getMessage());
        }
    }



    private static InlineKeyboardButton createButton(String data, String text) {
        InlineKeyboardButton button = new InlineKeyboardButton();
        button.setCallbackData(data);
        button.setText(text);
        return button;
    }


    @Override
    public SendMessage keyboardResponse(long id, String text) {
        SendMessage sendMessage = new SendMessage(String.valueOf(id), text);
        sendMessage.setParseMode(ParseMode.HTML);

        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();
        List<InlineKeyboardButton> firstRow = new ArrayList<>();

        firstRow.add(createButton("YES_FOR_FRIENDS", "✅Включить!"));
        firstRow.add(createButton("NO_FOR_FRIENDS", "❌Выключить!"));
        rows.add(firstRow);
        inlineKeyboardMarkup.setKeyboard(rows);
        sendMessage.setReplyMarkup(inlineKeyboardMarkup);

        return sendMessage;
    }
}
