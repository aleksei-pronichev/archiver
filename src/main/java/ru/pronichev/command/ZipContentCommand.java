package ru.pronichev.command;

import ru.pronichev.ConsoleHelper;
import ru.pronichev.FileProperties;
import ru.pronichev.ZipFileManager;

import java.util.List;

public class ZipContentCommand implements ZipCommand {
    @Override
    public void execute() {
        ConsoleHelper.writeMessage("Просмотр содержимого архива.");

        ZipFileManager zipFileManager = getZipFileManager();

        ConsoleHelper.writeMessage("Содержимое архива:");

        List<FileProperties> files = zipFileManager.getFilesList();
        for (FileProperties file : files) {
            ConsoleHelper.writeMessage(file.toString());
        }

        ConsoleHelper.writeMessage("Содержимое архива прочитано.");
    }
}
