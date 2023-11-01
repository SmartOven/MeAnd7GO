package ru.itmo.highload.service.kv;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.RandomAccessFile;
import java.util.Optional;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class SstableUtil {
    private static final Logger log = LogManager.getLogger();
    private static final int INTEGER_SIZE_BYTES = 81;

    public static SparseIndex dumpMemTable(MemTable memTable, String ssTableFilePath) {
        byte[] compressedData;
        try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
             GZIPOutputStream gzipOutputStream = new GZIPOutputStream(byteArrayOutputStream);
             ObjectOutputStream objectOutputStream = new ObjectOutputStream(gzipOutputStream)) {
            objectOutputStream.writeObject(memTable);
            compressedData = byteArrayOutputStream.toByteArray();
        } catch (IOException e) {
            log.error(e);
            return null;
        }

        try (DataOutputStream dataOutputStream = new DataOutputStream(new FileOutputStream(ssTableFilePath))) {
            dataOutputStream.writeInt(compressedData.length); // Записываем длину
            dataOutputStream.write(compressedData); // Записываем сжатые данные
        } catch (IOException e) {
            log.error(e);
            return null;
        }

        SparseIndex sparseIndex = new SparseIndex();
        sparseIndex.setIndex(memTable.firstKey(), INTEGER_SIZE_BYTES);
        return sparseIndex;
    }

    public static Optional<String> findValueInSegment(String ssTableFilePath, int offsetBytes, String key) {
        try (RandomAccessFile file = new RandomAccessFile(ssTableFilePath, "r")) {
            file.seek(offsetBytes);
            int pairSizeBytes = (Integer) readCompressedObject(file, INTEGER_SIZE_BYTES);
            file.seek(offsetBytes + INTEGER_SIZE_BYTES);
            MemTable segmentTable = (MemTable) readCompressedObject(file, pairSizeBytes);
            return Optional.ofNullable(segmentTable.get(key));
        } catch (IOException | ClassNotFoundException e) {
            log.error(e);
            return Optional.empty();
        }
    }

    private static Object readCompressedObject(RandomAccessFile file, int objectSize)
            throws IOException, ClassNotFoundException {
        byte[] data = new byte[objectSize];
        int bytesRead = file.read(data);

        if (bytesRead != objectSize) {
            throw new RuntimeException(String.format("Cant read %s bytes from file", objectSize));
        }

        try (ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(data);
             GZIPInputStream gzipInputStream = new GZIPInputStream(byteArrayInputStream);
             ObjectInputStream objectInputStream = new ObjectInputStream(gzipInputStream)) {
            return objectInputStream.readObject();
        }
    }
}
