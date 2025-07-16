package org.telebot.buttons;

import org.telebot.command.interfaces.ExecuteButton;
import org.telebot.connector.ConnectBot;
import org.telebot.data.User;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.time.DateTimeException;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

public class RegistrationUser extends ConnectBot implements ExecuteButton {

    private static final String WAITING_NAME = "WAITING_NAME";
    private static final String WAITING_BIRTH = "WAITING_BIRTH";

    private static final Map<Long, String> registrationStage = new HashMap<>();
    private static final Map<Long, String> tempName = new HashMap<>();

    @Override
    public void applyButton(Update update) {
        Long chatId = update.hasMessage() ? update.getMessage().getChatId() : update.getCallbackQuery().getMessage().getChatId();

        String stage = registrationStage.get(chatId);
        User user = new User(null, null);


    }

    public void sendMessage(long chatId, String text) {
        SendMessage sendMessage = new SendMessage(String.valueOf(chatId), text);
        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            throw new RuntimeException();
        }
    }
}
