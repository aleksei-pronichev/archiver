package ru.pronichev;

import ru.pronichev.exception.WrongZipFileException;

public class Archiver {
    public static void main(String[] args) {
        Operation operation = null;
        do {
            try {
                operation = askOperation();
                CommandExecutor.execute(operation);
            } catch (WrongZipFileException e) {
                ConsoleHelper.writeMessage("Вы не выбрали файл архива или выбрали неверный файл.");
            } catch (Exception e) {
                ConsoleHelper.writeMessage("Произошла ошибка. Проверьте введенные данные.");
            }
        } while (operation != Operation.EXIT);
    }

    public static Operation askOperation() {
        ConsoleHelper.writeMessage("");
        ConsoleHelper.writeMessage("Выберите операцию:");
        for (Operation operation : Operation.values()) {
            ConsoleHelper.writeMessage(
                    String.format("%d - %s", operation.ordinal(), operation)
            );
        }
        return Operation.values()[ConsoleHelper.readInt()];
    }
}
