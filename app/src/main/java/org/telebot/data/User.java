package org.telebot.data;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.telebot.data.exception.InvalidDataException;

import java.time.LocalDateTime;
@Getter
@Setter
@ToString
public class User {
    private String name;
    private Integer age;
    private LocalDateTime birthday;

    public User(String name, int age, LocalDateTime birthday) {
        this.name = name;
        this.age = age;
        this.birthday = birthday;

    }

    public User(String name) {
        this.name = name;
    }

    public void validate() throws InvalidDataException {
        if (name == null && getName().isEmpty()) throw new InvalidDataException(this, "Имя не может быть null или пустым. Перезапишите значение!");
        if (age == null && getAge() < 0) throw new InvalidDataException(this, "Возраст не может быть null или меньше 0. Перезапишите значение!");
        if (birthday == null) throw new InvalidDataException(this, "День рождения должен быть указан корректно. Перезапишите значение!");

    }








}
