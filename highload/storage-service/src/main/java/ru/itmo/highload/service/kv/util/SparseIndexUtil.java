package ru.itmo.highload.service.kv.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.Objects;

import ru.itmo.highload.service.kv.SparseIndex;

public class SparseIndexUtil {
    public static void createDump(SparseIndex sparseIndex, Path filePath) {
        SparseIndexSerializable sparseIndexSerializable = sparseIndex.toSerializable();
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filePath.toFile()))) {
            oos.writeObject(sparseIndexSerializable);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void deleteDump(Path filePath) {
        var ignored = filePath.toFile().delete();
    }

    public static SortedPairList<String, SparseIndex> loadSparseIndexes(Path indexesDirPath) {
        SortedPairList<String, SparseIndex> sparseIndexes = new SortedPairList<>(Comparator.comparing(Pair::getKey));
        for (File file : Objects.requireNonNull(indexesDirPath.toFile().listFiles())) {
            if (!file.isFile()) {
                continue;
            }
            try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
                SparseIndexSerializable sparseIndexSerializable = (SparseIndexSerializable) ois.readObject();
                SparseIndex sparseIndex = SparseIndex.ofSerializable(sparseIndexSerializable);
                sparseIndexes.insertSorted(new Pair<>(file.getName(), sparseIndex));
            } catch (IOException | ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        }
        return sparseIndexes;
    }
}
