package org.telebot.buttons;

import org.telebot.command.Help;
import org.telebot.command.interfaces.ExecuteButton;
import org.telebot.connector.ConnectBot;
import org.telebot.data.User;
import org.telebot.data.database.DBConnector;
import org.telebot.data.database.DBManager;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.time.LocalDate;
import java.time.Period;
import java.util.HashMap;
import java.util.Map;

public class CalendarHandler extends ConnectBot implements ExecuteButton {
    private static final Map<Long, Integer> selectedYears = new HashMap<>();
    private static final Map<Long, Integer> selectedMonths = new HashMap<>();


    @Override
    public void applyButton(Update update) {
        Long chatId = update.getCallbackQuery().getMessage().getChatId();
        String callBackData = update.getCallbackQuery().getData();

        if (callBackData.startsWith("YEAR_")) {
            handleYearSelection(chatId, callBackData);
        } else if (callBackData.startsWith("MONTH_")) {
            handleMonthSelection(chatId, callBackData);
        } else if (callBackData.startsWith("DATE_")) {
            handleDateSelection(chatId, callBackData, update);
        }
    }

    private void handleYearSelection(Long chatId, String callbackData) {
        String yearStr = callbackData.replace("YEAR_", "");
        int year = Integer.parseInt(yearStr);

        selectedYears.put(chatId, year);
        sendMonthSelection(chatId, year);
    }

    private void handleMonthSelection(Long chatId, String callbackData) {
        String monthStr = callbackData.replace("MONTH_", "");
        int month = Integer.parseInt(monthStr);

        Integer year = selectedYears.get(chatId);
        if (year != null) {
            selectedMonths.put(chatId, month);

            sendDaySelection(chatId, year, month);
        }
    }

    private void handleDateSelection(Long chatId, String callbackData, Update update) {
        String dateStr = callbackData.replace("DATE_", "");
        User tempUser = User.getTempUser(chatId);
        if (tempUser != null) {
            try {
                LocalDate birthday = LocalDate.parse(dateStr);
                tempUser.setBirthday(birthday);
                tempUser.setAge(Period.between(birthday, LocalDate.now()).getYears());
                DBConnector dbConnector = new DBConnector();
                DBManager dbManager = new DBManager(dbConnector);
                dbManager.addUser(update, tempUser);
                User.saveUser(chatId, tempUser);
                User.removeTempUser(chatId);
                selectedYears.remove(chatId);
                selectedMonths.remove(chatId);

                sendMessage(chatId, "✅ Регистрация завершена!\n" +
                        "Ваша дата рождения: " + birthday + "\n" +
                        "Ваш возраст: " + tempUser.getAge() + " лет");
                sendMessage(chatId, "Используйте /help для просмотра доступных команд");

            } catch (Exception e) {
                sendMessage(chatId, "❌ Ошибка при сохранении даты. Попробуйте ещё раз.");
            }
        }
    }


    private void sendMonthSelection(Long chatId, int year)  {
        SendMessage sendMessage = new SendMessage(String.valueOf(chatId), "📆Теперь выберите месяц:");
        sendMessage.setReplyMarkup(CalendarKeyboard.createKeyboardForChooseMonth());

        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            sendMessage(chatId, "Ошибка, в выборе года!");
        }
    }

    private void sendDaySelection(Long chatId, int year, int month) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(String.valueOf(chatId));
        sendMessage.setText("📆Выберите день рождения:");
        sendMessage.setReplyMarkup(CalendarKeyboard.createKeyboardForChooseDay(year, month));

        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }


    private void sendMessage(Long chatId, String text) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setText(text);
        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            throw new RuntimeException();
        }

    }


}
