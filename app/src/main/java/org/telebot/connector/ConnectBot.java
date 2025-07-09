package org.telebot.connector;




import lombok.Getter;
import org.telebot.command.runner.Command;
import org.telebot.command.runner.Runner;
import org.telebot.data.exception.InvalidCommandException;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Properties;


@Getter
public class ConnectBot extends TelegramLongPollingBot {
    private String botToken;
    private String username;


    public void setBot() throws IOException {
        Properties properties = new Properties();
        try (InputStream inputStream = Files.newInputStream(Paths.get("/Users/n0fckgway/Desktop/TelegramProject/.gradle/properties/apiBot.properties"))) {
            properties.load(inputStream);

        } catch (IOException e) {
            throw new IOException(e.getMessage());

        }

        this.botToken = properties.getProperty("httpApi");
        this.username = properties.getProperty("username");
    }

    @Override
    public String getBotUsername() {
        try {
            setBot();
        } catch (IOException e){
            throw new RuntimeException(e.getMessage());
        }
        return username;

    }

    @Override
    public String getBotToken() {
        try {
            setBot();

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return botToken;

    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            executeCommand(update);

        }
    }

    public void executeCommand(Update update) {
        Runner runner = new Runner();
        String message = update.getMessage().getText();
        Command command = new Command(message);
        if (message.equals(command.getName())) {
            command.setName(message);
        } else throw new InvalidCommandException(this, "Команда не в коллекции!");
        runner.registrationCommand();
        runner.commands.get(command.getName()).apply(update);


    }


}
