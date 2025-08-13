package org.telebot.data.interfaces;

import org.telebot.data.exception.DataBaseException;

import java.sql.SQLException;

@FunctionalInterface
public interface SQLFunction<T, R> {
    /**
     * Функциональный интерфейс, представляющий SQL функцию, которая принимает объект типа T в качестве аргумента
     * и возвращает объект типа R. Может генерировать SQLException и DatabaseException.
     * @param t тип входного параметра
     * @param <R> тип возвращаемого значения
     */
    R apply(T t) throws SQLException, DataBaseException;
}
