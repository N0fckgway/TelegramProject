package org.telebot.command.interfaces;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

@FunctionalInterface
public interface KeyboardResponse {
    SendMessage keyboardResponse(long id, String text);

}
