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

    public User(String fullName, LocalDate birthday) {
        this.fullName = fullName;
        this.birthday = birthday;
    }

    public User(String fullName) {
        this.fullName = fullName;
    }



    public static User getUser(String fullName) {
        return new User(fullName);
    }
}
