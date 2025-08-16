package org.telebot.data;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class Friend {

    private String firstName;
    private String lastName;
    private String userName;
    private LocalDate birthday;


}
