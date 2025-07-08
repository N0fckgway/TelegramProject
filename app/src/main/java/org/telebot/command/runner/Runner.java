package org.telebot.command.runner;

import org.telebot.command.Setting;
import org.telebot.command.Start;
import org.telebot.command.interfaces.ExecuteCommand;
import org.telebot.connector.ConnectBot;

import java.util.HashMap;

public class Runner {
    public final HashMap<String, ExecuteCommand> commandCollection = new HashMap<>();
    public ConnectBot connectBot;


    public void registrationCommand() {
        commandCollection.put("/start", new Start(connectBot));
        commandCollection.put("/setting", new Setting());

    }

}
