package org.telebot.command.runner;

import lombok.Getter;
import lombok.Setter;
import org.telebot.command.*;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.HashMap;

@Getter
@Setter
public class Command {
    private String name;
    private String description;
    public final  HashMap<String, Command> commandCollection = new HashMap<>();
    private boolean flag = true;

    public Command(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public Command(String name) {
        this.name = name;
    }


    public void register(String name, Command command) {
        commandCollection.put(name, command);
    }

    public HashMap<String, Command> commandFill() {
        register("/start", new Start().getCommand());
        register("/setting", new Setting().getCommand());
        register("/help", new Help().getCommand());
        register("/add", new Add().getCommand());
        register("/show", new Show().getCommand());
        register("/delete", new Delete().getCommand());
        register("/edit", new Edit().getCommand());
        register("/next", new Next().getCommand());
        return commandCollection;
    }

    public String commandPrintln(Update update) {
        StringBuilder stringBuilder = new StringBuilder();
        Long chatId = update.getMessage().getChatId();
        for (Command command : commandFill().values()) {
            stringBuilder.append(command.getName())
                    .append(" — ")
                    .append("<b>")
                    .append(command.getDescription())
                    .append("</b>")
                    .append("\n\n");

        }
        String chatIdStr = String.valueOf(chatId);
        SendMessage sendMessage = new SendMessage(chatIdStr, stringBuilder.toString());
        sendMessage.setParseMode(ParseMode.HTML);
        return stringBuilder.toString();
    }

}
