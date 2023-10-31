package ru.itmo.highload.client.kv;

public record KeyValueDto(String key, String value) {
    @Override
    public String toString() {
        return "KeyValueDto{" +
                "key='" + key + '\'' +
                ", value='" + value + '\'' +
                '}';
    }
}
