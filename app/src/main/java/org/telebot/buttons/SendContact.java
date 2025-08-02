package org.telebot.buttons;

import org.telebot.command.Help;
import org.telebot.command.interfaces.ExecuteButton;
import org.telebot.command.interfaces.KeyboardResponse;
import org.telebot.connector.ConnectBot;
import org.telebot.data.User;
import org.telebot.data.database.DBConnector;
import org.telebot.data.database.DBManager;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.ArrayList;
import java.util.List;


public class SendContact extends ConnectBot implements ExecuteButton {
    @Override
    public void applyButton(Update update) {
        DBConnector dbConnector = new DBConnector();
        DBManager dbManager = new DBManager(dbConnector);
        Long chatId = update.getMessage().getChatId();
        if (dbManager.checkUserExisting(chatId)) {
            sendMessage(chatId, "✅Вы уже прошли процесс регистрации!");
            Help helpCommand = new Help();
            helpCommand.apply(update);

        } else if (update.hasMessage() && update.getMessage().hasContact()) {
            String firstName = update.getMessage().getContact().getFirstName();
            String lastName = update.getMessage().getContact().getLastName();
            String phoneNumber = update.getMessage().getContact().getPhoneNumber();
            String username = update.getMessage().getFrom().getUserName();

            User tempUser = new User(chatId, firstName, lastName, username, phoneNumber, null);
            User.saveTempUser(chatId, tempUser);

            sendYearSelection(chatId);
        }

    }


    public void sendMessage(long chatId, String text) {
        SendMessage sendMessage = new SendMessage(String.valueOf(chatId), text);
        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            throw new RuntimeException();
        }
    }


    private void sendYearSelection(Long chatId) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(String.valueOf(chatId));
        sendMessage.setText("Выберите 📅 Год рождения:");
        sendMessage.setReplyMarkup(CalendarKeyboard.createKeyboardForChooseYear());

        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }

}
