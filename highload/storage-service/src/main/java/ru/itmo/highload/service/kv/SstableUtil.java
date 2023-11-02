package ru.itmo.highload.service.kv;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Optional;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class SstableUtil {
    private static final Logger log = LogManager.getLogger();

    /**
     * Дампит MemTable в конец файла и записывает offset дампа в SparseIndex
     */
    public static void dumpMemTable(MemTable memTable, String ssTableFilePath, SparseIndex sparseIndex) {
        try (FileOutputStream fileOutputStream = new FileOutputStream(ssTableFilePath, true);
             GZIPOutputStream gzipOutputStream = new GZIPOutputStream(fileOutputStream);
             ObjectOutputStream objectOutputStream = new ObjectOutputStream(gzipOutputStream)) {

            long offset = fileOutputStream.getChannel().position() - 10;
            objectOutputStream.writeObject(memTable);
            fileOutputStream.getFD().sync();

            sparseIndex.setIndex(memTable.firstKey(), offset);

        } catch (IOException e) {
            log.error("Error dumping MemTable to file", e);
        }
    }

    public static Optional<String> findValueInSegment(String ssTableFilePath, SparseIndex sparseIndex, String key) {
        long offset = sparseIndex.getNearestIndexPair(key).getValue();
        MemTable segmentTable = readMemTable(ssTableFilePath, offset);
        if (segmentTable == null) {
            return Optional.empty();
        }
        return Optional.ofNullable(segmentTable.get(key));
    }

    /**
     * Читает MemTable из файла с заданным offset
     */
    public static MemTable readMemTable(String ssTableFilePath, long offset) {
        try (FileInputStream fileInputStream = new FileInputStream(ssTableFilePath)) {
            var ignored = fileInputStream.skip(offset);

            try (GZIPInputStream gzipInputStream = new GZIPInputStream(fileInputStream);
                 ObjectInputStream objectInputStream = new ObjectInputStream(gzipInputStream)) {
                return (MemTable) objectInputStream.readObject();
            }

        } catch (IOException | ClassNotFoundException e) {
            log.error("Error reading segment from file", e);
            return null;
        }
    }
}
