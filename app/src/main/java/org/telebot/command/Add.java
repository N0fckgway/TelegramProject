package org.telebot.command;

import lombok.Setter;
import org.telebot.command.interfaces.ExecuteCommand;
import org.telebot.command.runner.Command;
import org.telebot.connector.ConnectBot;
import org.telegram.telegrambots.meta.api.objects.Update;

@Setter
public class Add extends ConnectBot implements ExecuteCommand {
    private Command command;

    public Command getCommand() {
        return new Command("/add", "Добавить нового человека в список!");
    }

    @Override
    public void apply(Update update) {

    }
}
