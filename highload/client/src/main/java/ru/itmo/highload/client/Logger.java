package ru.itmo.highload.client;

public class Logger {
    private static Logger instance;

    public static Logger getInstance() {
        if (instance == null) {
            instance = new Logger();
        }
        return instance;
    }

    private Logger() {
    }

    public void info(String message) {
        System.out.println(message);
    }
}
