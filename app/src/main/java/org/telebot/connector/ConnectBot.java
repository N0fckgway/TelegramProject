package org.telebot.connector;




import lombok.Getter;
import org.telebot.buttons.SendContact;
import org.telebot.command.Add;
import org.telebot.command.Delete;
import org.telebot.command.Help;
import org.telebot.command.interfaces.ExecuteButton;
import org.telebot.command.runner.Runner;
import org.telebot.data.User;
import org.telebot.data.database.DBConnector;
import org.telebot.data.database.DBManager;
import org.telebot.data.parser.TextParser;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
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
    private final TextParser textParser = new TextParser();



    public void setBot() throws IOException {
        Properties properties = new Properties();
        try (InputStream inputStream = Files.newInputStream(Paths.get("../app/src/main/resources/properties/apiBot.properties"))) {
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
            String text = update.getMessage().getText();
            if (text.startsWith("/")) {
                executeCommand(update);

            } else {
                Long chatId = update.getMessage().getChatId();
                Add addCommand = new Add();

                if (Add.isUserInAddProcess(chatId)) {
                    addCommand.apply(update);
                    return;
                }
                Delete deleteCommand = new Delete();
                if (Delete.isUserInDeleteProcess(chatId)) {
                    deleteCommand.apply(update);
                    return;
                }
                return;
            }

        } else if (update.hasCallbackQuery()) {
            answerCallBackFunc(update);
            executeButton(update);

        } else if (update.getMessage().hasContact()) {
            SendContact sendContact = new SendContact();
            sendContact.applyButton(update);

        }
    }

    public void executeCommand(Update update) {
        Runner runner = new Runner();
        String message = update.getMessage().getText();
        runner.registrationCommands();
        if (!runner.commands.containsKey(message)) {
            String response = "<strong>Такой команды не существует!</strong>\nСписок команд можно посмотреть здесь - /help";
            sendMessageOfMistake(response, update);

        }
        if (runner.commands.get(message) != null) {
            runner.commands.get(message).apply(update);
        }


    }

    public void executeButton(Update update) {
        Runner runner = new Runner();
        String data = update.getCallbackQuery().getData();
        runner.registrationButton();

        ExecuteButton button = runner.buttons.get(data);

        if (button == null && data.startsWith("DATE_")) {
            button = runner.buttons.get("DATE_");
        }
        if (button != null) {
            button.applyButton(update);
        } else {
            sendMessageOfMistake("Нет обработчика для кнопки: ", update);
        }


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

    public void answerCallBackFunc(Update update) {
        AnswerCallbackQuery answerCallbackQuery = new AnswerCallbackQuery();
        answerCallbackQuery.setCallbackQueryId(update.getCallbackQuery().getId());
        try {
            execute(answerCallbackQuery);

        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }


}
