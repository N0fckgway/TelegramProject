package org.telebot.buttons;

import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.time.Year;
import java.util.ArrayList;
import java.util.List;

public class CalendarKeyboard {

    public static InlineKeyboardMarkup createKeyboardForChooseYear() {
        InlineKeyboardMarkup keyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();
        Year currentYear = Year.now();
        int startYear = 1950;
        int endYear = currentYear.getValue();
        int rowsButtons = 4;

        for (int year = startYear; year <= endYear; year += rowsButtons) {
            List<InlineKeyboardButton> row = new ArrayList<>();
            for (int i = 0; i < rowsButtons && (year + i) <= endYear; i++) {
                int selectYear = year + i;
                row.add(createButton("YEAR_" + selectYear, String.valueOf(selectYear)));

            }
            rows.add(row);
        }
        keyboardMarkup.setKeyboard(rows);
        return keyboardMarkup;
    }

    public static InlineKeyboardMarkup createKeyboardForChooseMonth() {
        InlineKeyboardMarkup keyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();

        String[] month = {
                "Январь", "Февраль", "Март", "Апрель",
                "Май", "Июнь", "Июль", "Август",
                "Сентябрь", "Октябрь", "Ноябрь", "Декабрь"
        };

        List<InlineKeyboardButton> rowFirst = new ArrayList<>();
        rowFirst.add(createButton("MONTH_1", month[0]));
        rowFirst.add(createButton("MONTH_2", month[1]));
        rowFirst.add(createButton("MONTH_3", month[2]));
        rowFirst.add(createButton("MONTH_4", month[3]));
        rows.add(rowFirst);

        List<InlineKeyboardButton> rowSecond = new ArrayList<>();
        rowSecond.add(createButton("MONTH_5", month[4]));
        rowSecond.add(createButton("MONTH_6", month[5]));
        rowSecond.add(createButton("MONTH_7", month[6]));
        rowSecond.add(createButton("MONTH_8", month[7]));
        rows.add(rowSecond);

        List<InlineKeyboardButton> rowThird = new ArrayList<>();
        rowThird.add(createButton("MONTH_9", month[8]));
        rowThird.add(createButton("MONTH_10", month[9]));
        rowThird.add(createButton("MONTH_11", month[10]));
        rowThird.add(createButton("MONTH_12", month[11]));
        rows.add(rowThird);

        keyboardMarkup.setKeyboard(rows);
        return keyboardMarkup;
    }

    public static InlineKeyboardMarkup createKeyboardForChooseDay(int year, int month) {
        InlineKeyboardMarkup keyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();

        int daysInMonth = getDaysInMonth(year, month);

        int rowsButtons = 7;
        for (int day = 1; day < daysInMonth; day += rowsButtons) {
            List<InlineKeyboardButton> row = new ArrayList<>();
            for (int i = 0; i <= rowsButtons && (day + i) <= daysInMonth; i++) {
                int currentDay = day + i;
                row.add(createButton("DATE_" + year + "-" + String.format("%02d", month) + "-" + String.format("%02d", currentDay), String.valueOf(currentDay)));
            }
            rows.add(row);
        }
        keyboardMarkup.setKeyboard(rows);
        return keyboardMarkup;
    }

    public static int getDaysInMonth(int year, int month) {
        return switch (month) {
            case 2 -> (year % 4 == 0 && year % 100 != 0) || (year % 400 == 0) ? 29 : 28;
            case 4, 6, 9, 11 -> 30;
            default -> 31;
        };
    }




    private static InlineKeyboardButton createButton(String data, String text) {
        InlineKeyboardButton button = new InlineKeyboardButton();
        button.setCallbackData(data);
        button.setText(text);
        return button;
    }


}
