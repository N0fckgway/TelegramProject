package org.telebot.data.exception;


public class InvalidCommandException extends RuntimeException {
    public Object object;

    public InvalidCommandException(Object o, String message) {
        super(message);
        this.object = o;
    }

    @Override
    public String getMessage() {
        return object.getClass() + " : " + super.getMessage();
    }



}
