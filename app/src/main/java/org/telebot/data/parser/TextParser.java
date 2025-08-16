package org.telebot.data.parser;



public class TextParser {

    public String[] parseFullName(String text) {
        String normalizedName = text.trim().replaceAll("\\s+", " ");
        return normalizedName.split(" ");

    }

    public Boolean hasFullName(String text) {
        String fullNameRegex = "^[А-ЯЁ][а-яё]+\\s+[А-ЯЁ][а-яё]+$";
        return text.trim().matches(fullNameRegex);

    }

    public Boolean hasUserName(String text) {
        return text.charAt(0) == '@';
    }

    public Boolean hasDate(String text) {
        String regExp = "[0-9]{2}\\.[0-9]{2}\\.[0-9]{4}";
        return text.trim().matches(regExp);
    }

    public Boolean hasCommand(String text) {
        return text.charAt(0) == '/';
    }

}
