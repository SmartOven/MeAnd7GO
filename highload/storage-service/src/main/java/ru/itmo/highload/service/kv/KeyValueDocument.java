package ru.itmo.highload.service.kv;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collation = "keyValue")
public class KeyValueDocument {
    @Id
    private String id;
    private String key;
    private String value;
}
