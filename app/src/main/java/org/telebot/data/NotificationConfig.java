package org.telebot.data;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;


@Getter
@Setter
public class NotificationConfig {

    private Long chatId;

    private Boolean enabled;

    public NotificationConfig(Long chatId, Boolean enabled) {
        this.chatId = chatId;
        this.enabled = enabled;
    }
}
