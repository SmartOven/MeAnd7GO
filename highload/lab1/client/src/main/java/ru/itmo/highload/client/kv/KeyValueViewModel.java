package ru.itmo.highload.client.kv;

public record KeyValueViewModel(String key, String value) {
    @Override
    public String toString() {
        return "KeyValueViewModel{" +
                "key='" + key + '\'' +
                ", value='" + value + '\'' +
                '}';
    }
}
