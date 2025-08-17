package org.telebot.data;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;


@Getter
@Setter
public class NotificationConfig {

    private Long chatId;
    private Boolean enabledForUsers;
    private Boolean enabledForFriends;


    public NotificationConfig(Long chatId, Boolean enabledForUsers, Boolean enabledForFriends) {
        this.chatId = chatId;
        this.enabledForUsers = enabledForUsers;
        this.enabledForFriends = enabledForFriends;
    }
}
