package org.telebot.config;

import org.telebot.connector.ConnectBot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

public class BotInitializer {
    private final ConnectBot connectBot;

    public BotInitializer(ConnectBot connectBot) {
        this.connectBot = connectBot;
    }

    public void init() throws TelegramApiException {
        TelegramBotsApi telegramBotsApi = new TelegramBotsApi(DefaultBotSession.class);
        try {
            telegramBotsApi.registerBot(connectBot);
        } catch (TelegramApiException e) {
            throw new TelegramApiException(e.getMessage());
        }

    }


}
