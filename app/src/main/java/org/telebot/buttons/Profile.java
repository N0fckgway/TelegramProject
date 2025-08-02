package org.telebot.buttons;


import org.telebot.command.interfaces.ExecuteButton;
import org.telebot.connector.ConnectBot;
import org.telebot.data.User;
import org.telebot.data.database.DBConnector;
import org.telebot.data.database.DBManager;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;



public class Profile extends ConnectBot implements ExecuteButton {

    @Override
    public void applyButton(Update update) {
        Long chatId = update.getCallbackQuery().getMessage().getChatId();
        DBConnector dbConnector = new DBConnector();
        DBManager dbManager = new DBManager(dbConnector);
        StringBuilder profile = new StringBuilder();
        User user = dbManager.getUserById(chatId);

        if (dbManager.checkUserExisting(chatId)) {
            profile.append("👨‍💻 <b>ПРОФИЛЬ ПОЛЬЗОВАТЕЛЯ</b>\n\n");

            if (user.getLastName() == null) {
                profile.append("📝 <b>Имя:</b> ").append(user.getFirstName()).append("\n\n");
            } else {
                profile.append("📝 <b>Имя:</b> ").append(user.getFirstName()).append(" ").append(user.getLastName()).append("\n\n");
            }

            profile.append("🔗 <b>Никнейм:</b> @").append(user.getUserName()).append("\n\n");
            profile.append("📱 <b>Телефон:</b> ").append(user.getPhoneNumber()).append("\n\n");
            profile.append("🎂 <b>Дата рождения:</b> ").append(user.getBirthday()).append("\n\n");
            profile.append("🎈 <b>Возраст:</b> ").append(user.getAge()).append(" лет\n\n");
            profile.append("✅ <b>Статус:</b> Зарегистрирован\n\n");

        } else {
            profile.append("❌ <b>Профиль не найден!</b>\n\n");
            profile.append("Возможно, вы ещё не зарегистрированы.\n");
            profile.append("Используйте /registration для создания профиля.");
        }

        SendMessage sendMessage = new SendMessage();
        sendMessage.setParseMode(ParseMode.HTML);
        sendMessage.setChatId(String.valueOf(chatId));
        sendMessage.setText(profile.toString());

        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }


}
