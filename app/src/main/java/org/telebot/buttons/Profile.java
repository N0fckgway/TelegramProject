package org.telebot.buttons;


import org.telebot.command.interfaces.ExecuteButton;
import org.telebot.connector.ConnectBot;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;



public class Profile extends ConnectBot implements ExecuteButton {


    @Override
    public void applyButton(Update update) {
        Long chatId = update.getCallbackQuery().getMessage().getChatId();

        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Ваш профиль:\n\n");

        /*if (user != null) {

        } else {
            stringBuilder.append("<strong>Пуст</strong> \n\nЗаполните его через кнопку Регистрация!");

        }*/

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
