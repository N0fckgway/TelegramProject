package org.telebot.command;


import lombok.Setter;
import org.telebot.command.runner.Command;

@Setter
public class Edit  {
    private Command command;

    public Command getCommand() {
        return new Command("/edit - в следующем обновлении будет реализована функция", "Изменить данные о человеке!");
    }


}
