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
    private Long chatId;
    private String firstName;
    private String lastName;
    private String userName;
    private String phoneNumber;
    private LocalDate birthday;
    @Getter
    private Integer age;

    private static final Map<Long, User> users = new HashMap<>();


    public User(Long chatId, String firstName, String lastName, String userName, String phoneNumber, LocalDate birthday) {
        this.chatId = chatId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.userName = userName;
        this.phoneNumber = phoneNumber;
        this.birthday = birthday;
        this.age = calculateAge(birthday);

    }

    private Integer calculateAge(LocalDate birthday) {
        if (birthday == null) {
            return null;
        }
        return Period.between(birthday, LocalDate.now()).getYears();
    }



    public static void saveTempUser(Long chatId, User user) {
        users.put(chatId, user);
    }

    public static User getTempUser(Long chatId) {
        return users.get(chatId);
    }

    public static void removeTempUser(Long chatId) {
        users.remove(chatId);
    }

    public static void saveUser(Long chatId, User user) {
        users.put(chatId, user);
    }

    public static User getUser(Long chatId) {
        return users.get(chatId);
    }
}
