package org.telebot.command.runner;

import org.telebot.buttons.Profile;
import org.telebot.command.Help;
import org.telebot.command.Setting;
import org.telebot.command.Start;
import org.telebot.command.interfaces.ExecuteButton;
import org.telebot.command.interfaces.ExecuteCommand;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.HashMap;

public class Runner {
    public final HashMap<String, ExecuteCommand> commands = new HashMap<>();
    public final HashMap<String, ExecuteButton> buttons = new HashMap<>();

    public void registrationCommands() {
        commands.put("/start", new Start());
        commands.put("/setting", new Setting());
        commands.put("/help", new Help());

    }

    public void registrationButton(Update update) {
        buttons.put("PROFILE", new Profile());
    }

}
