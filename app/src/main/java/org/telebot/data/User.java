package org.telebot.data;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.telebot.data.exception.InvalidDataException;

import java.time.LocalDate;
import java.time.Period;

@Getter
@Setter
@ToString
public class User {
    private String name;
    private LocalDate birthday;

    public User(String name, LocalDate birthday) {
        this.name = name;
        this.birthday = birthday;

    }

    public User(String name) {
        this.name = name;
    }

    public void validate() throws InvalidDataException {
        if (name == null || getName().isEmpty()) throw new InvalidDataException(this, "Имя не может быть null или пустым. Перезапишите значение!");
        if (birthday == null) throw new InvalidDataException(this, "День рождения должен быть указан корректно. Перезапишите значение!");

    }

    public Integer getAge() {
        if (birthday == null) return 0;
        return Period.between(birthday, LocalDate.now()).getYears();
    }
}
