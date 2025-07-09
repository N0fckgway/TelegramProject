package org.telebot.command.runner;

import org.telebot.command.Help;
import org.telebot.command.Setting;
import org.telebot.command.Start;
import org.telebot.command.interfaces.ExecuteCommand;

import java.util.HashMap;

public class Runner {
    public final HashMap<String, ExecuteCommand> commands = new HashMap<>();


    public void registrationCommand() {
        commands.put("/start", new Start());
        commands.put("/setting", new Setting());
        commands.put("/help", new Help());

    }

}
