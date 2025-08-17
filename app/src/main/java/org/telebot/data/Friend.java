package org.telebot.data;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class Friend {
    private Long id;
    private String firstName;
    private String lastName;
    private String role;
    private LocalDate birthday;

    public Friend(Long id, String firstName, String lastName, String role, LocalDate birthday) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.role = role;
        this.birthday = birthday;
    }

    public Friend() {
    }
}
