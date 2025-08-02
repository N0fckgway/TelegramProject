package org.telebot.command.runner;

import org.telebot.buttons.*;
import org.telebot.command.*;
import org.telebot.command.interfaces.ExecuteButton;
import org.telebot.command.interfaces.ExecuteCommand;

import java.time.Year;
import java.util.HashMap;

public class Runner {
    public final HashMap<String, ExecuteCommand> commands = new HashMap<>();
    public final HashMap<String, ExecuteButton> buttons = new HashMap<>();

    public void registrationCommands() {
        commands.put("/start", new Start());
        commands.put("/setting", new Setting());
        commands.put("/help", new Help());
        commands.put("/registration", new Registration());
        commands.put("/add", new Add());

    }

    public void registrationButton() {
        buttons.put("PROFILE", new Profile());
        buttons.put("FRIENDS", new Friends());
        buttons.put("NOTIFICATIONS", new Notification());

        CalendarHandler calendarHandler = new CalendarHandler();
        Year currentYear = Year.now();
        for (int year = 1950; year <= currentYear.getValue(); year++) {
            buttons.put("YEAR_" + year, calendarHandler);
        }

        for (int month = 1; month <= 12; month ++) {
            buttons.put("MONTH_" + month, calendarHandler);
        }

        buttons.put("DATE_", calendarHandler);

    }

}
