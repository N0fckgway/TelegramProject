package org.telebot.command;

import org.telebot.command.runner.Command;

public class Next {
    private Command command;

    public Command getCommand() {
        return new Command("/next", "Показать ближайшие дни рождения! (Близжайшие три события!)");
    }
}
