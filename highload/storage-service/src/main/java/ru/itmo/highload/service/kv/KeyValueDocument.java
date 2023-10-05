package ru.itmo.highload.service.kv;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collation = "keyValue")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class KeyValueDocument {
    @Id
    private String id;
    private String key;
    private String value;
}
