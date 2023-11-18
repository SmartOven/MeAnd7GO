package ru.itmo.highload.service.kv.lsm;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Stream;

import ru.itmo.highload.service.kv.util.Pair;

public class MemTableWal {
    private static final long MIN_WAL_FILE_LENGTH = 11L;
    private final Path filePath;
    private final File file;

    public MemTableWal(Path filePath) {
        this.filePath = filePath;
        this.file = filePath.toFile();
        if (!this.file.getParentFile().exists()){
            boolean ignored = this.file.getParentFile().mkdirs();
        }
        if (!file.exists()) {
            try {
                var ignored = file.createNewFile();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    /**
     * Собирает MemTable из журнала
     */
    public MemTable loadFromFile() {
        if (file.length() < MIN_WAL_FILE_LENGTH) {
            return new MemTable();
        }

        MemTable memTable = new MemTable();
        try (Stream<String> stream = Files.lines(filePath)) {
            stream.map(this::stringToPair)
                    .forEach(pair -> memTable.put(pair.getKey(), pair.getValue()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return memTable;
    }

    /**
     * Добавляет новую запись в журнал
     */
    public void writeLine(Pair<String, String> pair) {
        try (PrintWriter printWriter = new PrintWriter(new FileWriter(file, true))) {
            printWriter.println(pairToString(pair));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Удаляет файл, а затем создает пустой
     */
    public void clearWal() {
        try {
            var ignored = file.delete() && file.createNewFile();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private String pairToString(Pair<String, String> pair) {
        return String.format("%s\t:\t%s", pair.getKey(), pair.getValue());
    }

    private Pair<String, String> stringToPair(String str) {
        String[] parts = str.split("\t:\t");
        if (parts.length != 2) {
            throw new RuntimeException("Pair-string is corrupted");
        }
        return new Pair<>(parts[0], parts[1]);
    }
}
