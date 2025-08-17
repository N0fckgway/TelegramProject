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

    public Boolean hasRole(String text) {
        String roleRegex = "^[а-яёА-ЯЁ]+(\\s+[а-яёА-ЯЁ]+)*$";
        return text.trim().matches(roleRegex);
    }

    public String parseRoleFriend(String text) {
        return text.trim().replaceAll("\\s+", " ");
    }

    public Boolean hasDate(String text) {
        String regExp = "[0-9]{2}\\.[0-9]{2}\\.[0-9]{4}";
        return text.trim().matches(regExp);
    }

    public Boolean hasCommand(String text) {
        return text.charAt(0) == '/';
    }

}
