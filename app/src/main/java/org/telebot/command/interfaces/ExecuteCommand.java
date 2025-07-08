package org.telebot.command.interfaces;

import org.telegram.telegrambots.meta.api.objects.Update;

@FunctionalInterface
public interface ExecuteCommand {
    void apply(Update update);
}
