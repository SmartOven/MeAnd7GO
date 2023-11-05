package ru.itmo.highload.service.kv.util;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class Pair<Key, Value> implements Serializable {
    private final Key key;
    private Value value;
}
