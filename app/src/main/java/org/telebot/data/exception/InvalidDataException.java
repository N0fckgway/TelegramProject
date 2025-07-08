package org.telebot.data.exception;

public class InvalidDataException extends RuntimeException {
    public Object object;

    public InvalidDataException(Object o, String message) {
        super(message);
        this.object = o;
    }

    @Override
    public String getMessage() {
        return object.getClass() + ": " + super.getMessage();
    }

}
