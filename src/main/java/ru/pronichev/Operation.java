package ru.pronichev;

public enum Operation {
    CREATE("упаковать файлы в архив"),
    ADD("добавить файл в архив"),
    REMOVE("удалить файл из архива"),
    EXTRACT("распаковать архив"),
    CONTENT("просмотреть содержимое архива"),
    EXIT("выход");

    private final String value;

    Operation(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return value;
    }
}