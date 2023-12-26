package ru.itmo.highload.service.kv.lsm.storage;

import java.io.Serializable;


public record MemTableVersioned(MemTable memTable, long lastModified) implements Serializable {
}
