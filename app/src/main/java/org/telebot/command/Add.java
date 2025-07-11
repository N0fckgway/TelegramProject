package org.telebot.command;

import lombok.Setter;
import org.telebot.command.runner.Command;

@Setter
public class Add {
    private Command command;

    public Command getCommand() {
        return new Command("/add", "Добавить нового человека в список!");
    }
}
