package org.telebot.connector;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
public class ConnectBot extends TelegramLongPollingBot {
    private String botToken;
    private String username;
    private final TextParser textParser = new TextParser();

    public void setBot() throws IOException {
        log.info("Настройка конфигурации бота...");
        Properties properties = new Properties();
        try (InputStream inputStream = Files.newInputStream(Paths.get("../app/src/main/resources/properties/apiBot.properties"))) {
            properties.load(inputStream);
            log.debug("Файл конфигурации загружен успешно");
        } catch (IOException e) {
            log.error("Ошибка загрузки файла конфигурации: {}", e.getMessage());
            throw new IOException(e.getMessage());
        }

        this.botToken = properties.getProperty("httpApi");
        this.username = properties.getProperty("username");
        log.info("Конфигурация бота загружена. Username: {}", username);
    }

    @Override
    public String getBotUsername() {
        try {
            setBot();
        } catch (IOException e){
            log.error("Ошибка получения username бота: {}", e.getMessage());
            throw new RuntimeException(e.getMessage());
        }
        return username;
    }

    @Override
    public String getBotToken() {
        try {
            setBot();
        } catch (IOException e) {
            log.error("Ошибка получения токена бота: {}", e.getMessage());
            throw new RuntimeException(e);
        }
        return botToken;
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            String text = update.getMessage().getText();
            Long chatId = update.getMessage().getChatId();
            String userName = update.getMessage().getFrom().getUserName();
            
            log.info("Получено текстовое сообщение от пользователя {} (chatId: {}): {}", 
                    userName != null ? userName : "Unknown", chatId, text);
            
            if (text.startsWith("/")) {
                log.debug("Обработка команды: {}", text);
                executeCommand(update);
            } else {
                log.debug("Обработка обычного сообщения: {}", text);
                Add addCommand = new Add();

                if (Add.isUserInAddProcess(chatId)) {
                    log.debug("Пользователь {} находится в процессе добавления друга", chatId);
                    addCommand.apply(update);
                    return;
                }
                Delete deleteCommand = new Delete();
                if (Delete.isUserInDeleteProcess(chatId)) {
                    log.debug("Пользователь {} находится в процессе удаления друга", chatId);
                    deleteCommand.apply(update);
                    return;
                }
                log.debug("Сообщение от пользователя {} не обработано (не в процессе)", chatId);
                return;
            }
        } else if (update.hasCallbackQuery()) {
            String callbackData = update.getCallbackQuery().getData();
            Long chatId = update.getCallbackQuery().getMessage().getChatId();
            String userName = update.getCallbackQuery().getFrom().getUserName();
            
            log.info("Получен callback от пользователя {} (chatId: {}): {}", 
                    userName != null ? userName : "Unknown", chatId, callbackData);
            
            answerCallBackFunc(update);
            executeButton(update);
        } else if (update.getMessage().hasContact()) {
            Long chatId = update.getMessage().getChatId();
            log.info("Получен контакт от пользователя (chatId: {})", chatId);
            
            SendContact sendContact = new SendContact();
            sendContact.applyButton(update);
        } else {
            log.debug("Получен update неизвестного типа: {}", update);
        }
    }

    public void executeCommand(Update update) {
        Runner runner = new Runner();
        String message = update.getMessage().getText();
        Long chatId = update.getMessage().getChatId();
        
        log.debug("Выполнение команды '{}' для пользователя {}", message, chatId);
        
        runner.registrationCommands();
        if (!runner.commands.containsKey(message)) {
            log.warn("Команда '{}' не найдена для пользователя {}", message, chatId);
            String response = "<strong>Таккой команды не существует!</strong>\nСписок команд можно посмотреть здесь - /help";
            sendMessageOfMistake(response, update);
        }
        if (runner.commands.get(message) != null) {
            log.info("Выполнение команды '{}' для пользователя {}", message, chatId);
            runner.commands.get(message).apply(update);
        }
    }

    public void executeButton(Update update) {
        Runner runner = new Runner();
        String data = update.getCallbackQuery().getData();
        Long chatId = update.getCallbackQuery().getMessage().getChatId();
        
        log.debug("Обработка кнопки '{}' для пользователя {}", data, chatId);
        
        runner.registrationButton();

        ExecuteButton button = runner.buttons.get(data);

        if (button == null && data.startsWith("DATE_")) {
            log.debug("Обработка календарной кнопки для пользователя {}", chatId);
            button = runner.buttons.get("DATE_");
        }
        if (button != null) {
            log.info("Выполнение действия кнопки '{}' для пользователя {}", data, chatId);
            button.applyButton(update);
        } else {
            log.warn("Не найден обработчик для кнопки '{}' пользователя {}", data, chatId);
            sendMessageOfMistake("Нет обработчика для кнопки: ", update);
        }
    }

    public void sendMessageOfMistake(String response, Update update) {
        Long chatId = update.getMessage().getChatId();
        log.warn("Отправка сообщения об ошибке пользователю {}: {}", chatId, response);
        
        SendMessage sendMessage = new SendMessage(String.valueOf(chatId), response);
        sendMessage.setParseMode(ParseMode.HTML);
        try {
            execute(sendMessage);
            log.debug("Сообщение об ошибке отправлено пользователю {}", chatId);
        } catch (TelegramApiException e) {
            log.error("Ошибка отправки сообщения об ошибке пользователю {}: {}", chatId, e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public void answerCallBackFunc(Update update) {
        String callbackQueryId = update.getCallbackQuery().getId();
        log.debug("Ответ на callback query: {}", callbackQueryId);
        
        AnswerCallbackQuery answerCallbackQuery = new AnswerCallbackQuery();
        answerCallbackQuery.setCallbackQueryId(callbackQueryId);
        try {
            execute(answerCallbackQuery);
            log.debug("Callback query {} обработан успешно", callbackQueryId);
        } catch (TelegramApiException e) {
            log.error("Ошибка обработки callback query {}: {}", callbackQueryId, e.getMessage());
            throw new RuntimeException(e);
        }
    }
}
