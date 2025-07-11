package org.telebot.command.runner;

import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;

@Getter
@Setter
public class Buttons  {
    private String name;
    private String data;
    private static final HashMap<String, Buttons> buttonsMap = new HashMap<>();

    public Buttons(String data, String name) {
        this.data = data;
        this.name = name;
    }



}
