package org.telebot.buttons;

import lombok.Getter;
import lombok.Setter;

import org.telebot.command.interfaces.ExecuteButton;
import org.telebot.connector.ConnectBot;
import org.telebot.data.Friend;
import org.telebot.data.User;
import org.telebot.data.database.DBConnector;
import org.telebot.data.database.DBManager;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class Friends extends ConnectBot implements ExecuteButton {
    private List<Friend> friendsList = new ArrayList<>();

    @Override
    public void applyButton(Update update) {
        DBManager dbManager = new DBManager(new DBConnector());
        Long chatId = update.getCallbackQuery().getMessage().getChatId();
        StringBuilder stringBuilder = new StringBuilder();
        friendsList = dbManager.getAllFriends(chatId);
        if (friendsList.isEmpty()) {
            sendMessage(chatId, "У вас пока нет добавленных друзей.\n\nИспользуйте команду /add для добавления нового друга!");
            return;
        }
        stringBuilder.append("📋 <strong>Ваши друзья:</strong>\n\n");
        try {
           for (int i = 0; i < friendsList.size(); i++) {
               Friend friend = friendsList.get(i);
               stringBuilder.append(" 👤 <strong>").append(i + 1).append(".</strong> ");
               stringBuilder.append(friend.getFirstName()).append(" ").append(friend.getLastName()).append("\n");
               stringBuilder.append(" 🍰 Дата рождения: ").append(friend.getBirthday().format(java.time.format.DateTimeFormatter.ofPattern("dd.MM.yyyy"))).append("\n");
               stringBuilder.append(" 🧑‍🧒 Роль: ").append(friend.getRole()).append("\n");
               stringBuilder.append(" 🆔 ID: ").append(friend.getId()).append("\n\n");
           }
           sendMessage(chatId, stringBuilder.toString());

        } catch (Exception e) {
            sendMessageOfMistake(String.valueOf(chatId), update);
        }


    }

    private void sendMessage(Long chatId, String text) {
        SendMessage sendMessage = new SendMessage(String.valueOf(chatId), text);
        sendMessage.setParseMode(ParseMode.HTML);

        try {
            execute(sendMessage);
        } catch (Exception e) {
            System.out.println(e.getMessage());

        }

    }


}
