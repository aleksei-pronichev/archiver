package ru.pronichev;

import ru.pronichev.exception.ConsoleReadException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class ConsoleHelper {
    private static final BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

    private ConsoleHelper() {
        //utility class
    }

    public static void writeMessage(String message) {
        System.out.println(message);
    }

    public static String readString() {
        try {
            return reader.readLine();
        } catch (IOException e) {
            throw new ConsoleReadException(e.getMessage());
        }
    }

    public static int readInt() {
        return Integer.parseInt(readString().trim());
    }
}
