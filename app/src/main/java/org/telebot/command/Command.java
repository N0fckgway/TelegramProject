package org.telebot.command;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Command {
    private String name;
    private String description;

    public Command(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public Command(String name) {
        this.name = name;
    }

    public Command getCommand() {
        return new Command(name, description);
    }

}
