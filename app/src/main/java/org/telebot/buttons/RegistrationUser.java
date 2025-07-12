package org.telebot.buttons;

import org.telebot.command.Help;
import org.telebot.command.interfaces.ExecuteButton;
import org.telebot.command.runner.Command;
import org.telebot.connector.ConnectBot;
import org.telebot.data.User;
import org.telebot.data.enums.UserStatus;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.HashMap;

public class RegistrationUser extends ConnectBot implements ExecuteButton {
    private static final String unPosRegStatus = "NOT_REGISTRATION";
    private static final String PosRegStatus = "REGISTRATION";
    public static final HashMap<Long, String> regMap = new HashMap<>();
    public static final HashMap<Long, UserStatus> statusRegMap = new HashMap<>();

    @Override
    public void applyButton(Update update) {
        Long chatId = update.getCallbackQuery().getMessage().getChatId();
        SendMessage sendMessage = new SendMessage();
        String data = update.getCallbackQuery().getData();
        regMap.put(chatId, unPosRegStatus);
        if (data.equals(regMap.get(chatId))) {
            String message = "Хорошо, теперь ты можешь использовать мои функции!😉\n\n" +
                    "<strong>Чтобы посмотреть мои команды используй - /help</strong>";
            sendMessage.setChatId(chatId);
            sendMessage.setText(message);
            sendMessage.setParseMode(ParseMode.HTML);
            try {
                execute(sendMessage);

            } catch (TelegramApiException e) {
                throw new RuntimeException(e);
            }

        } else {
            regMap.remove(chatId, unPosRegStatus);
            regMap.put(chatId, PosRegStatus);
            statusRegMap.put(chatId, UserStatus.WAIT_FOR_NAME);
            String message = "Окей, давай начнем!\n" +
                    "Введи свое настоящее имя и фамилию!";
            SendMessage message1 = new SendMessage(String.valueOf(chatId), message);
            try {
                execute(message1);
            } catch (TelegramApiException e) {
                throw new RuntimeException(e);
            }
            User user = new User(null, null);
            if (statusRegMap.get(chatId).equals(UserStatus.WAIT_FOR_NAME)) {
                String fullName = update.getMessage().getText();
                user.setFullName(fullName);
                statusRegMap.put(chatId, UserStatus.WAIT_FOR_BIRTH);

            }
            if (statusRegMap.get(chatId).equals(UserStatus.WAIT_FOR_BIRTH)) {
                String messageOfBirth = "Введи теперь свою дату рождения"
                String birth = update.getMessage().getText();

            }


        }

    }
}
