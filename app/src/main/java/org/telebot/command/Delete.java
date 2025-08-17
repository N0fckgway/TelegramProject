package org.telebot.command;

import lombok.Setter;
import org.telebot.command.interfaces.ExecuteButton;
import org.telebot.command.interfaces.ExecuteCommand;
import org.telebot.command.runner.Command;
import org.telebot.connector.ConnectBot;
import org.telebot.data.Friend;
import org.telebot.data.IdEnum;
import org.telebot.data.StepAdd;
import org.telebot.data.User;
import org.telebot.data.database.DBConnector;
import org.telebot.data.database.DBManager;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

@Setter
public class Delete extends ConnectBot implements ExecuteCommand {

    public Command getCommand() {
        return new Command("/delete", "Удалить человека из списка!");
    }
    private static Map<Long, IdEnum> step = new HashMap<>();

    @Override
    public void apply(Update update) {
        Long chatId = update.getMessage().getChatId();


        String text = update.getMessage().getText();

        if (text.equals("/delete")) {
            startDeletingFriend(chatId);
            return;
        }

        if (step == null) {
            return;
        }

        IdEnum currentStep = step.get(chatId);

        switch (currentStep) {
            case WAITING_ID -> handleId(chatId, text, update);
        }

    }

    private void startDeletingFriend(Long chatId) {
        step.put(chatId, IdEnum.WAITING_ID);
        sendMessage(chatId, "🗑️ <strong>Удаление друга</strong>");
        sendMessage(chatId, "🆔 Введите ID друга, которого хотите удалить:");
        sendMessage(chatId, "💡 <em>ID можно посмотреть в списке друзей</em>");

    }

    private void handleId(Long chatId, String friendId, Update update) {
        Registration registration = new Registration();
        try {
            DBManager dbManager = new DBManager(new DBConnector());
            if (!dbManager.checkUserExisting(chatId)) {
                sendMessage(chatId, "❌ Чтобы удалять друзей, нужно зарегистрироваться!");
                registration.apply(update);
                return;
            }
            Long id;
            try {
                id = Long.parseLong(friendId);
            } catch (NumberFormatException e) {
                sendMessage(chatId, "❌ Вы ввели не число! Введите корректный ID!");
                return;
            }
            if (dbManager.isFriendOwnedByUser(id, chatId)) {
                dbManager.deleteFriendById(id);
                sendMessage(chatId, "✅ Друг успешно удален!");
                step.remove(chatId);
            } else {
                sendMessage(chatId, "❌ Друга с таким ID не найдено или он не принадлежит вам!");
            }

        } catch (SQLException e) {
            sendMessage(chatId, "Ошибка удаления! Попробуйте позже!");
        } catch (NumberFormatException e) {
            sendMessage(chatId, "Вы ввели не число! Введите число!");
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

    public static Boolean isUserInDeleteProcess(Long chatId) {
        return step.containsKey(chatId);
    }
}
