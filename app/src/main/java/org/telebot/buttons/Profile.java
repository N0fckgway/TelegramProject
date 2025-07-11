package org.telebot.buttons;


import org.telebot.buttons.enums.UserStatus;
import org.telebot.command.interfaces.ExecuteButton;
import org.telebot.connector.ConnectBot;
import org.telegram.telegrambots.meta.api.objects.Update;


import java.util.HashMap;
import java.util.Map;

public class Profile extends ConnectBot implements ExecuteButton{
    private final static Map<Long, UserStatus> userStatus = new HashMap<>();

    public static Profile getProfile() {
        return new Profile();
    }


    @Override
    public void applyButton(Update update) {

    }


}
