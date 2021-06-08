package ru.pronichev.exception;

public class ConsoleReadException extends RuntimeException {
    public ConsoleReadException(String message) {
        super(message);
    }
}