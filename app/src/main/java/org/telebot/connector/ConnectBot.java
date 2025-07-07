package org.telebot.connector;




import lombok.Getter;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Getter
public class ConnectBot extends TelegramLongPollingBot {
    private String botToken;
    private String username;
    private final ConcurrentMap<Long, String> listUsers = new ConcurrentHashMap<>();



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
            String message = update.getMessage().getText();
            long chat_id = update.getMessage().getChatId();
            String firstName = update.getMessage().getFrom().getFirstName();
            switch (message) {
                case ("/start"):
                    startCommandMessage(chat_id, firstName);
                    break;
                default:
                    throw new IllegalStateException("Unexpected value: " + message);
            }
        }
    }

    public void startCommandMessage(Long longId, String firstName) {
        String start = "Приветствую " + firstName + ", теперь ты мой новый друг! Я тебе предоставлю возможность не забывать" +
                " о днях рождениях своих родных, друзей и вовсе очень близких тебе людей! " +
                "Давай для начала с тобой познакомимся, напиши как мне к тебе обращаться! ";
        sendMessage(longId, start);
    }

    public void sendMessage(Long chatId, String text) {
        String chatIdStr = String.valueOf(chatId);
        SendMessage sendMessage = new SendMessage(chatIdStr, text);
        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

}
