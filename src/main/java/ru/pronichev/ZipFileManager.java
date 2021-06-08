package ru.pronichev;

import ru.pronichev.exception.IORunException;
import ru.pronichev.exception.PathIsNotFoundException;
import ru.pronichev.exception.WrongZipFileException;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

public class ZipFileManager {
    private final Path zipFile;

    public ZipFileManager(Path zipFile) {
        this.zipFile = zipFile;
    }

    public void createZip(Path source) {
        try {
            Path zipDirectory = zipFile.getParent();
            if (Files.notExists(zipDirectory)) {
                Files.createDirectories(zipDirectory);
            }

            try (var zipOutputStream = new ZipOutputStream(Files.newOutputStream(zipFile))) {

                if (Files.isDirectory(source)) {
                    FileManager fileManager = new FileManager(source);
                    List<Path> fileNames = fileManager.getFileList();
                    for (Path fileName : fileNames) {
                        addNewZipEntry(zipOutputStream, source, fileName);
                    }
                } else if (Files.isRegularFile(source)) {
                    addNewZipEntry(zipOutputStream, source.getParent(), source.getFileName());
                } else {
                    throw new PathIsNotFoundException();
                }
            }
        } catch (IOException e) {
            throw new IORunException(e);
        }
    }

    public void extractAll(Path outputFolder) {
        try {
            if (!Files.isRegularFile(zipFile)) {
                throw new WrongZipFileException();
            }

            try (var zipInputStream = new ZipInputStream(Files.newInputStream(zipFile))) {

                if (Files.notExists(outputFolder)) {
                    Files.createDirectories(outputFolder);
                }

                var zipEntry = zipInputStream.getNextEntry();

                while (zipEntry != null) {
                    String fileName = zipEntry.getName();
                    Path fileFullName = outputFolder.resolve(fileName);

                    Path parent = fileFullName.getParent();
                    if (Files.notExists(parent)) {
                        Files.createDirectories(parent);
                    }

                    try (var outputStream = Files.newOutputStream(fileFullName)) {
                        copyData(zipInputStream, outputStream);
                    }
                    zipEntry = zipInputStream.getNextEntry();
                }
            }

        } catch (IOException e) {
            throw new IORunException(e);
        }
    }

    public void removeFile(Path path) {
        removeFiles(Collections.singletonList(path));
    }

    public void removeFiles(List<Path> pathList) {
        try {
            if (!Files.isRegularFile(zipFile)) {
                throw new WrongZipFileException();
            }

            Path tempZipFile = Files.createTempFile(null, null);

            try (var zipOutputStream = new ZipOutputStream(Files.newOutputStream(tempZipFile))) {
                try (var zipInputStream = new ZipInputStream(Files.newInputStream(zipFile))) {

                    var zipEntry = zipInputStream.getNextEntry();
                    while (zipEntry != null) {

                        var archivedFile = Paths.get(zipEntry.getName());

                        if (pathList.contains(archivedFile)) {
                            ConsoleHelper.writeMessage(String.format("Файл '%s' удален из архива.", archivedFile));
                        } else {
                            String fileName = zipEntry.getName();
                            zipOutputStream.putNextEntry(new ZipEntry(fileName));

                            copyData(zipInputStream, zipOutputStream);

                            zipOutputStream.closeEntry();
                            zipInputStream.closeEntry();
                        }
                        zipEntry = zipInputStream.getNextEntry();
                    }
                }
            }

            Files.move(tempZipFile, zipFile, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new IORunException(e);
        }
    }

    public void addFile(Path absolutePath) {
        addFiles(Collections.singletonList(absolutePath));
    }

    public void addFiles(List<Path> absolutePathList) {
        try {
            if (!Files.isRegularFile(zipFile)) {
                throw new WrongZipFileException();
            }
            Path tempZipFile = Files.createTempFile(null, null);
            List<Path> archiveFiles = new ArrayList<>();

            try (var zipOutputStream = new ZipOutputStream(Files.newOutputStream(tempZipFile))) {
                try (var zipInputStream = new ZipInputStream(Files.newInputStream(zipFile))) {

                    var zipEntry = zipInputStream.getNextEntry();
                    while (zipEntry != null) {
                        String fileName = zipEntry.getName();
                        archiveFiles.add(Paths.get(fileName));

                        zipOutputStream.putNextEntry(new ZipEntry(fileName));
                        copyData(zipInputStream, zipOutputStream);

                        zipInputStream.closeEntry();
                        zipOutputStream.closeEntry();

                        zipEntry = zipInputStream.getNextEntry();
                    }
                }

                for (Path file : absolutePathList) {
                    if (Files.isRegularFile(file)) {
                        if (archiveFiles.contains(file.getFileName())) {
                            ConsoleHelper.writeMessage(String.format("Файл '%s' уже существует в архиве.", file));
                        } else {
                            addNewZipEntry(zipOutputStream, file.getParent(), file.getFileName());
                            ConsoleHelper.writeMessage(String.format("Файл '%s' добавлен в архиве.", file));
                        }
                    } else {
                        throw new PathIsNotFoundException();
                    }
                }
            }


            Files.move(tempZipFile, zipFile, StandardCopyOption.REPLACE_EXISTING);

        } catch (IOException e) {
            throw new IORunException(e);
        }
    }

    public List<FileProperties> getFilesList() {
        try {
            if (!Files.isRegularFile(zipFile)) {
                throw new WrongZipFileException();
            }

            List<FileProperties> files = new ArrayList<>();

            try (var zipInputStream = new ZipInputStream(Files.newInputStream(zipFile))) {
                var zipEntry = zipInputStream.getNextEntry();

                while (zipEntry != null) {
                    var byteArrayOutputStream = new ByteArrayOutputStream();
                    copyData(zipInputStream, byteArrayOutputStream);

                    FileProperties file = new FileProperties(zipEntry.getName(), zipEntry.getSize(), zipEntry.getCompressedSize(), zipEntry.getMethod());
                    files.add(file);
                    zipEntry = zipInputStream.getNextEntry();
                }
            }

            return files;

        } catch (IOException e) {
            throw new IORunException(e);
        }
    }

    private void addNewZipEntry(ZipOutputStream zipOutputStream, Path filePath, Path fileName) throws IOException {
        var fullPath = filePath.resolve(fileName);
        try (var inputStream = Files.newInputStream(fullPath)) {
            var entry = new ZipEntry(fileName.toString());

            zipOutputStream.putNextEntry(entry);

            copyData(inputStream, zipOutputStream);

            zipOutputStream.closeEntry();
        }
    }

    private void copyData(InputStream in, OutputStream out) throws IOException {
        var buffer = new byte[8 * 1024];
        int len;
        while ((len = in.read(buffer)) > 0) {
            out.write(buffer, 0, len);
        }
    }
}