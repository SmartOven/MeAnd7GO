package ru.itmo.highload.service.kv.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Path;
import java.util.Comparator;

import ru.itmo.highload.service.kv.lsm.SparseIndex;

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
        var listFiles = indexesDirPath.toFile().listFiles();
        if (listFiles == null)
            return sparseIndexes;
        for (File file : listFiles) {
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
