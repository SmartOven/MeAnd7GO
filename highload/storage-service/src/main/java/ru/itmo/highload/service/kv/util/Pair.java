package ru.itmo.highload.service.kv.util;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class Pair<Key, Value> {
    private final Key key;
    private Value value;
}
