package org.telebot.command;

import lombok.Setter;
import org.telebot.command.runner.Command;

@Setter
public class Show {
    private Command command;

    public Command getCommand() {
        return new Command("/show", "Показать список всех сохраненных людей!");
    }
}
