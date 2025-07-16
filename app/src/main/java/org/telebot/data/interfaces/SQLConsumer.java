package org.telebot.data.interfaces;

import org.telebot.data.exception.DataBaseException;

import java.sql.SQLException;

@FunctionalInterface
public interface SQLConsumer <T> {
    /**
     * Функциональный интерфейс, представляющий операцию над объектом типа T, выполняющую операции с базой данных.
     * В отличие от стандартного интерфейса Consumer, может бросать SQLException.
     * @param <T> тип объекта, который будет обрабатываться операцией
     */
    void accept(T t) throws SQLException;

}
