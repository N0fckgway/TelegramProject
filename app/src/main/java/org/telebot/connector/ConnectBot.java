package org.telebot.connector;




import lombok.Getter;
import org.telebot.buttons.Profile;
import org.telebot.command.runner.Command;
import org.telebot.command.runner.Runner;
import org.telebot.data.User;
import org.telebot.data.exception.InvalidCommandException;
import org.telebot.data.exception.InvalidDataException;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

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
        try (InputStream inputStream = Files.newInputStream(Paths.get("/Users/n0fckgway/Desktop/TelegramProject/app/src/main/resources/properties/apiBot.properties"))) {
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

        } else if (update.hasCallbackQuery()) {



        }
    }

    public void executeCommand(Update update) {
        Runner runner = new Runner();
        String message = update.getMessage().getText();
        Command command = new Command(message);
        runner.registrationCommands();
        if (!runner.commands.containsKey(message)) {
            String response = "<strong>Такой команды не существует!</strong>\nСписок команд можно посмотреть здесь - /help";
            sendMessageOfMistake(response, update);
        }
        runner.commands.get(command.getName()).apply(update);

    }

    public void executeButton(Update update) {
        Runner runner = new Runner();
        String data = update.getCallbackQuery().getData();

    }

    public void sendMessageOfMistake(String response, Update update) {
        Long chatId = update.getMessage().getChatId();
        SendMessage sendMessage = new SendMessage(String.valueOf(chatId), response);
        sendMessage.setParseMode(ParseMode.HTML);
        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }


}
