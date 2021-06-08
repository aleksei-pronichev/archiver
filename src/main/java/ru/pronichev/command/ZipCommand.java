package ru.pronichev.command;

import ru.pronichev.ConsoleHelper;
import ru.pronichev.ZipFileManager;

import java.nio.file.Paths;

public interface ZipCommand extends Command {
    default ZipFileManager getZipFileManager() {
        ConsoleHelper.writeMessage("Введите полный путь файла архива:");
        return new ZipFileManager(
                Paths.get(ConsoleHelper.readString())
        );
    }
}