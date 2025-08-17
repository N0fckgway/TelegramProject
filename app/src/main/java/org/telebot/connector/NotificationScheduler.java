package org.telebot.connector;

import org.telebot.data.Friend;
import org.telebot.data.User;
import org.telebot.data.database.DBConnector;
import org.telebot.data.database.DBManager;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import java.sql.SQLException;
import java.time.Duration;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class NotificationScheduler extends ConnectBot {

    private final DBManager dbManager;
    private static ScheduledExecutorService scheduler;

    public NotificationScheduler() {
        DBConnector dbConnector = new DBConnector();
        this.dbManager = new DBManager(dbConnector);
        startScheduler();
    }

    private void startScheduler() {
        scheduler = Executors.newScheduledThreadPool(1);
        scheduleDailyNotification();

    }

    private synchronized void scheduleDailyNotification() {
        long initialDelay = getNextRunTime(10, 0);

        scheduler.schedule(
                () -> {
                    sendDailyNotifications();
                    sendBirthdayNotificationForFriends();
                    scheduleDailyNotification();
                },
                initialDelay,
                TimeUnit.MILLISECONDS
        );
    }

    public void changeButtonStatusUser(Boolean enable, Long chatId) {
        dbManager.updateEnableUser(enable, chatId);
    }

    public void changeButtonStatusFriend(Boolean enable, Long chatId) {
        dbManager.updateEnableFriend(enable, chatId);
    }

    private long getNextRunTime(int hour, int minute) {
        ZonedDateTime now = ZonedDateTime.now();
        ZonedDateTime nextRun = now.withHour(hour).withMinute(minute).withSecond(0).withNano(0);

        if (now.compareTo(nextRun) > 0) {
            nextRun = nextRun.plusDays(1);
        }

        return Duration.between(now, nextRun).toMillis();
    }

    private void sendDailyNotifications() {
        try {
            List<User> users = dbManager.getAllUsersWithEnabled(true);
            for (User user : users) {
                if (user.getBirthday() != null) {
                    sendBirthdayNotification(user);

                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void sendBirthdayNotification(User user) {
        try {
            long daysUntil = daysUntilBirthday(user.getBirthday());

            String message = createBirthdayMessage(user, daysUntil);

            SendMessage sendMessage = new SendMessage();
            sendMessage.setChatId(String.valueOf(user.getChatId()));
            sendMessage.setText(message);
            sendMessage.setParseMode(ParseMode.HTML);

            execute(sendMessage);
            System.out.println("Отправлено уведомление пользователю " + user.getChatId());

        } catch (Exception e) {
            System.out.println("Ошибка отправки уведомления: " + e.getMessage());
        }
    }

    private void sendBirthdayNotificationForFriends() {
        try {
            List<User> users = dbManager.getAllUsersWithEnabled(true);
            for (User user : users) {
                List<Friend> friends = dbManager.getAllFriendsWithEnabled(true);
                for (Friend friend : friends) {
                    if (friend.getBirthday() != null) {
                        sendFriendBirthdayNotification(user, friend);
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("Ошибка при отправке уведомлений о друзьях: " + e.getMessage());

        }
    }

    private void sendFriendBirthdayNotification(User user, Friend friend) {
        try {
            long daysUntil = daysUntilBirthday(friend.getBirthday());


            if (daysUntil == 7 || daysUntil == 1) {
                String message = createFriendBirthdayMessage(user, friend, daysUntil);

                SendMessage sendMessage = new SendMessage();
                sendMessage.setChatId(String.valueOf(user.getChatId()));
                sendMessage.setText(message);
                sendMessage.setParseMode(ParseMode.HTML);

                execute(sendMessage);
                System.out.println("Отправлено уведомление о друге " + friend.getFirstName() +
                        " пользователю " + user.getChatId() + " (осталось дней: " + daysUntil + ")");
            }
        } catch (Exception e) {
            System.out.println("Ошибка отправки уведомления о друге: " + e.getMessage());
        }
    }

    private String createFriendBirthdayMessage(User user, Friend friend, long daysUntil) {
        String friendName = friend.getFirstName();
        if (friend.getLastName() != null) {
            friendName += " " + friend.getLastName();
        }
        String userName = user.getFirstName() != null ? user.getFirstName() : "пользователь";

        if (daysUntil == 7) {
            return "🧑‍🧒 <b>Напоминание о дне рождения друга</b>\n\n" +
                    "Привет, " + userName + "!\n\n" +
                    "У вашего друга <b>" + friendName + "</b> (" + friend.getRole() + ") " +
                    "через <b>7 дней</b> день рождения! 🎉\n\n" +
                    "🎂 Дата: " + friend.getBirthday().format(java.time.format.DateTimeFormatter.ofPattern("dd.MM.yyyy")) + "\n" +
                    "🏷️ Роль: " + friend.getRole() + "\n\n" +
                    "Не забудьте подготовить поздравление! 🎁";
        } else if (daysUntil == 1) {
            return "🚨 <b>СРОЧНОЕ НАПОМИНАНИЕ!</b>\n\n" +
                    "Привет, " + user.getFirstName() + "!\n\n" +
                    "У вашего друга <b>" + friendName + "</b> (" + friend.getRole() + ") " +
                    "<b>ЗАВТРА</b> день рождения! 🎉\n\n" +
                    "🎂 Дата: " + friend.getBirthday().format(java.time.format.DateTimeFormatter.ofPattern("dd.MM.yyyy")) + "\n" +
                    "🏷️ Роль: " + friend.getRole() + "\n\n" +
                    "Пора поздравлять! 🎁✨";
        }

        return "";
    }


    private long daysUntilBirthday(LocalDate birthday) {
        LocalDate today = LocalDate.now();
        LocalDate nextBirthday = birthday.withYear(today.getYear());

        if (nextBirthday.isBefore(today)) {
            nextBirthday = nextBirthday.plusYears(1);
        }

        return ChronoUnit.DAYS.between(today, nextBirthday);
    }

    private String createBirthdayMessage(User user, long daysUntil) {
        String name = user.getFirstName();
        if (user.getLastName() != null) {
            name += " " + user.getLastName();
        }
        String motivation = getMotivationalPhrase(daysUntil);

        if (daysUntil == 0) {
            return "🎉<b>С ДНЁМ РОЖДЕНИЯ, " + name + "!</b>\n\n" +
                    "Поздравляем вас с днём рождения!\n" +
                    "Желаем счастья, здоровья и всего самого лучшего! 🎁✨\n\n" +
                    motivation;
        } else if (daysUntil == 1) {
            return "🥳 <b>Завтра ваш день рождения!</b>\n\n" +
                    "Привет, " + name + "!\n" +
                    "Завтра ваш особенный день! Готовьтесь к празднику! 🎉\n\n" +
                    motivation;
        } else {
            return "📅 <b>Ежедневное напоминание</b>\n\n" +
                    "Привет, " + name + "!\n" +
                    "До вашего дня рождения осталось <b>" + daysUntil + " дней</b>! 🎉\n\n" +
                    motivation;
        }
    }

    private String getMotivationalPhrase(long daysUntil) {
        if (daysUntil == 0) {
            return "🐎 Сегодня ты на коне! Наслаждайся каждым моментом!";
        } else if (daysUntil <= 7) {
            return "🔥 Меньше недели до праздника! Начинай готовиться к самому лучшему дню в году!";
        } else if (daysUntil <= 30) {
            return "🌟 Меньше месяца до твоего дня! Каждый день - это шаг к новым достижениям!";
        } else if (daysUntil <= 100) {
            return "💪 Меньше ста дней до праздника! Ты можешь многое успеть до своего дня рождения!";
        } else if (daysUntil <= 200) {
            return "🚀 Меньше двухсот дней! У тебя есть время воплотить в жизнь все мечты!";
        } else {
            return "🌍 У тебя осталось уже меньше года! Каждый день - это возможность стать лучше!";
        }
    }


}
