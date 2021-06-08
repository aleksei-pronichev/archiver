package ru.pronichev.exception;

import java.io.IOException;

public class IORunException extends RuntimeException {
    public IORunException(IOException exception) {
        super(exception);
    }
}