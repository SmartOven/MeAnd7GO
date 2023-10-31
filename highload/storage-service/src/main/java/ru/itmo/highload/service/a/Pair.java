package ru.itmo.highload.service.a;

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
