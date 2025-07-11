package org.telebot.buttons;

import org.telebot.command.interfaces.ExecuteButton;
import org.telebot.connector.ConnectBot;
import org.telegram.telegrambots.meta.api.objects.Update;

public class Notification extends ConnectBot implements ExecuteButton {
    @Override
    public void applyButton(Update update) {

    }
}
