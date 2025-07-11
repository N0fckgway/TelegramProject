package org.telebot.data.exception;


import org.telebot.connector.ConnectBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

public class InvalidCommandException extends RuntimeException {
    public SendMessage sendMessage;
    public ConnectBot connectBot;

    public InvalidCommandException(Long chatId, String message) {
        super(message);
        this.connectBot = new ConnectBot();
        this.sendMessage = new SendMessage(String.valueOf(chatId), message);
    }

    public String getMessage() {
        try {
            connectBot.execute(sendMessage);
        } catch (TelegramApiException e) {
            throw new RuntimeException();
        }
        return "";
    }



}
