package org.telebot.command;

import lombok.Setter;
import org.telebot.command.runner.Command;

@Setter
public class Delete {
    private Command command;

    public Command getCommand() {
        return new Command("/delete", "Удалить человека из списка!");
    }
}
