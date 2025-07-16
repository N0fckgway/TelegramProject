package org.telebot.buttons;


import org.telebot.command.interfaces.ExecuteButton;
import org.telebot.connector.ConnectBot;
import org.telebot.data.User;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;



public class Profile extends ConnectBot implements ExecuteButton {


    @Override
    public void applyButton(Update update) {
        Long chatId = update.getCallbackQuery().getMessage().getChatId();
        User user = User.getUser(chatId);

        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Ваш профиль:\n\n");

        if (user != null) {
            stringBuilder.append("Имя: ").append(user.getFullName() != null ? user.getFullName() : "не указано").append("\n");
            if (user.getBirthday() != null) {
                stringBuilder.append("День рождения: ").append(user.getBirthday()).append("\n");
                stringBuilder.append("Возраст: ").append(user.getAge()).append("\n");
            } else {
                stringBuilder.append("День рождения: не указан\n");
                stringBuilder.append("Возраст: не указан\n");
            }
        } else {
            stringBuilder.append("Профиль не заполнен. Заполните его через кнопку Регистрация!");

        }

        SendMessage sendMessage = new SendMessage();
        sendMessage.setParseMode(ParseMode.HTML);
        sendMessage.setChatId(String.valueOf(chatId));
        sendMessage.setText(stringBuilder.toString());

        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }

    }


}
