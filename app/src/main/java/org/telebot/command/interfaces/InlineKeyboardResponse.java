package org.telebot.command.interfaces;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

@FunctionalInterface
public interface InlineKeyboardResponse {
    SendMessage inlineKeyboardResponse(long id, String text);
}
