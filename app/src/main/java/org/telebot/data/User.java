package org.telebot.data;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.telebot.data.enums.UserStatus;
import org.telebot.data.exception.InvalidDataException;

import java.time.LocalDate;
import java.time.Period;
import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
@ToString
public class User {
    private String fullName;
    private LocalDate birthday;
    private static final Map<Long, User> users = new HashMap<>();

    public static void saveUser(Long chatId, User user) {
        users.put(chatId, user);
    }

    public static User getUser(Long chatId) {
        return users.get(chatId);
    }

    public User(String fullName, LocalDate birthday) {
        this.fullName = fullName;
        this.birthday = birthday;
    }

    public User(String fullName) {
        this.fullName = fullName;
    }


    public int getAge() {
        return Period.between(birthday, LocalDate.now()).getYears();
    }
}
