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

    public static SparseIndex dumpMemTable(MemTable memTable, String ssTableFilePath) {
        try (FileOutputStream fileOutputStream = new FileOutputStream(ssTableFilePath);
             GZIPOutputStream gzipOutputStream = new GZIPOutputStream(fileOutputStream);
             ObjectOutputStream objectOutputStream = new ObjectOutputStream(gzipOutputStream)) {
            objectOutputStream.writeObject(memTable);
        } catch (IOException e) {
            log.error("Error dumping MemTable to file", e);
        }

        SparseIndex sparseIndex = new SparseIndex();
        sparseIndex.setIndex(memTable.firstKey(), 0);
        return sparseIndex;
    }

    public static Optional<String> findValueInSegment(String ssTableFilePath, int offsetBytes, String key) {
        try (FileInputStream fileInputStream = new FileInputStream(ssTableFilePath)) {
            long ignored = fileInputStream.skip(offsetBytes);

            try (GZIPInputStream gzipInputStream = new GZIPInputStream(fileInputStream);
                 ObjectInputStream objectInputStream = new ObjectInputStream(gzipInputStream)) {
                MemTable segmentTable = (MemTable) objectInputStream.readObject();
                return Optional.ofNullable(segmentTable.get(key));
            }

        } catch (IOException | ClassNotFoundException e) {
            log.error("Error reading segment from file", e);
            return Optional.empty();
        }
    }
}
