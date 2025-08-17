package org.telebot.command;

import lombok.Setter;
import org.telebot.command.interfaces.ExecuteCommand;
import org.telebot.command.runner.Command;
import org.telebot.connector.ConnectBot;
import org.telebot.data.Friend;
import org.telebot.data.StepAdd;
import org.telebot.data.database.DBConnector;
import org.telebot.data.database.DBManager;
import org.telebot.data.parser.TextParser;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.sql.SQLException;
import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

@Setter
public class Add extends ConnectBot implements ExecuteCommand {
    private TextParser textParser = new TextParser();
    private static Map<Long, StepAdd> stepAdd = new HashMap<>();
    private static Map<Long, Friend> tempFriend = new HashMap<>();


    public Command getCommand() {
        return new Command("/add", "Добавить нового человека в список!");
    }

    @Override
    public void apply(Update update) {
        Long chatId = update.getMessage().getChatId();
        String text = update.getMessage().getText();

        if (text.equals("/add")) {
            startAddingFriend(chatId);
            return;
        }



        StepAdd currentStep = stepAdd.get(chatId);

        if (currentStep == null) return;


        switch (currentStep) {
            case WAITING_NAME -> handleFullName(chatId, text);
            case WAITING_BIRTH -> handleBirth(chatId, text);
            case WAITING_ROLE -> handleRole(chatId, text, update);
        }

    }

    private void startAddingFriend(Long chatId) {
        stepAdd.put(chatId, StepAdd.WAITING_NAME);
        tempFriend.put(chatId, new Friend());
        sendMessage(chatId, "Переходим в опцию добавления друзей");
        sendMessage(chatId, "Отправь мне фамилию и имя друга");

    }

    private void handleFullName(Long chatId, String text) {
        Friend friend = tempFriend.get(chatId);
        if (textParser.hasFullName(text)) {
            String[] fullName = textParser.parseFullName(text);
            if (fullName.length == 2) {
                friend.setLastName(fullName[0]);
                friend.setFirstName(fullName[1]);
                stepAdd.remove(chatId);
                stepAdd.put(chatId, StepAdd.WAITING_BIRTH);
                sendMessage(chatId, "Отлично! Фамилия и имя были добавлены в мою память!");
                sendMessage(chatId, "<strong>Теперь отправь дату рождения в формате ДД.ММ.ГГГГ</strong>");

            } else sendMessage(chatId, "Ошибка! Неверный формат фамилии и имени. Попробуйте еще раз!");
        } else sendMessage(chatId, "Ошибка! Мы не видим вашего фамилии и имени! \n" +
                "Введите фамилию и имя еще раз!");
    }

    private void handleBirth(Long chatId, String text) {
        try {
            if (textParser.hasDate(text)) {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
                LocalDate birth = LocalDate.parse(text, formatter);

                Friend friend = tempFriend.get(chatId);
                friend.setBirthday(birth);
                stepAdd.remove(chatId);
                stepAdd.put(chatId, StepAdd.WAITING_ROLE);
                sendMessage(chatId, "✅Отлично! Дата рождения была добавлена в мою память!");
                sendMessage(chatId, "<strong>Теперь укажите кем вам является этот человек!</strong>");
            } else sendMessage(chatId, "Ошибка! Неверный формат даты! Используйте формат ДД.ММ.ГГГГ");

        } catch (DateTimeException e) {
            sendMessage(chatId, "Ошибка! Неверный формат даты рождения! Попробуйте еще раз!");
        }
    }

    private void handleRole(Long chatId, String role, Update update) {
        Friend friend = tempFriend.get(chatId);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
        DBManager dbManager = new DBManager(new DBConnector());
        Help help = new Help();
        Registration registration = new Registration();
        if (textParser.hasRole(role)) {
            String roleFriend = textParser.parseRoleFriend(role);
            friend.setRole(roleFriend);
            if (dbManager.checkUserExisting(chatId)) {
                try {
                    dbManager.addFriend(friend, chatId);
                    friend.setId(dbManager.getFriendId(chatId));
                    sendMessage(chatId, "Друг успешно добавлен! ✅\n" +
                            "Id:" + friend.getId() + "\n" +
                            "Имя: " + friend.getFirstName() + "\n" +
                            "Фамилия: " + friend.getLastName() + "\n" +
                            "Дата рождения: " + friend.getBirthday().format(formatter) + "\n" +
                            "Роль: " + friend.getRole()
                    );
                    stepAdd.remove(chatId);
                    tempFriend.remove(chatId);
                    help.apply(update);
                } catch (SQLException e) {
                    sendMessage(chatId, "❌Ошибка добавления друга в базу данных! Повторите попытку позже!");
                }
            } else {
                sendMessage(chatId, "Добавлять друзей вы сможете только после прохождения регистрации!");
                registration.apply(update);
            }


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

    public static boolean isUserInAddProcess(Long chatId) {
        return stepAdd.containsKey(chatId);
    }





}