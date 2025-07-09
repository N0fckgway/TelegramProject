package org.telebot.command.interfaces;

import org.telegram.telegrambots.meta.api.objects.Update;

public interface CallbackHandleQuery {
    void handleInlineButtons(Update update);
}
