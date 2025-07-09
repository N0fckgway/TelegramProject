package org.telebot.command;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.checkerframework.checker.units.qual.A;
import org.telebot.command.interfaces.CallbackHandleQuery;
import org.telebot.command.interfaces.ExecuteCommand;
import org.telebot.command.runner.Command;
import org.telebot.connector.ConnectBot;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.ArrayList;
import java.util.List;
@Getter
@Slf4j
public class Setting extends ConnectBot implements ExecuteCommand, CallbackHandleQuery {
    public String settingMessage = "🛠 <strong>Настройки:</strong>\n\n" +
            "<i>1</i>. 👀 Мой профиль\n" +
            "<i>2</i>. 👥 Мои друзья\n" +
            "<i>3</i>. ⏰ Уведомления\n";

    public Command getCommand() {
        return new Command("/setting", "Вывод настроек бота!");
    }

    @Override
    public void apply(Update update) {
        Long chatId = update.getMessage().getChatId();
        try {
            execute(inlineKeyboardResponse(chatId));

        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }

    }

    public SendMessage inlineKeyboardResponse(long chatId) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setParseMode(ParseMode.HTML);
        sendMessage.setChatId(chatId);
        sendMessage.setText(getSettingMessage());

        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowOfButtons = new ArrayList<>();

        List<InlineKeyboardButton> firstRowButtons = new ArrayList<>();
        List<InlineKeyboardButton> secondRowButtons = new ArrayList<>();

        /// Первая кнопка
        InlineKeyboardButton firstButton = new InlineKeyboardButton();
        firstButton.setText("👀 Мой профиль");
        firstButton.setCallbackData("PROFIlE");

        /// Вторая кнопка
        InlineKeyboardButton secondButton = new InlineKeyboardButton();
        secondButton.setText("👥 Мои друзья");
        secondButton.setCallbackData("FRIENDS");

        /// Третья кнопка
        InlineKeyboardButton thirdButton = new InlineKeyboardButton();
        thirdButton.setText("⏰ Уведомления");
        thirdButton.setCallbackData("NOTIFICATIONS");

        firstRowButtons.add(firstButton);
        firstRowButtons.add(secondButton);
        secondRowButtons.add(thirdButton);

        rowOfButtons.add(firstRowButtons);
        rowOfButtons.add(secondRowButtons);

        inlineKeyboardMarkup.setKeyboard(rowOfButtons);
        sendMessage.setReplyMarkup(inlineKeyboardMarkup);


        return sendMessage;

    }


    @Override
    public void handleInlineButtons(Update update) {
        if (update.hasCallbackQuery()) {
            CallbackQuery callbackQuery = update.getCallbackQuery();
            String answer = callbackQuery.getData();

            switch (answer) {
                case ("PROFIlE"):
                    ///
                case ("FRIENDS"):
                    ///
                case ("NOTIFICATIONS"):
                    ///
            }
        }
    }
}