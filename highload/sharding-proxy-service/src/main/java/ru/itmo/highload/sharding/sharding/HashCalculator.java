package ru.itmo.highload.sharding.sharding;

public interface HashCalculator {
    int calculate(String key);
}
