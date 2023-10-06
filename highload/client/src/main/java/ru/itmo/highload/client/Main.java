package ru.itmo.highload.client;

import java.util.Scanner;

import org.apache.commons.logging.Log;
import org.springframework.http.HttpLogging;
import ru.itmo.highload.client.kv.KeyValueDto;
import ru.itmo.highload.client.kv.KeyValueViewModel;
import ru.itmo.highload.client.kv.KeyValueService;

public class Main {
    private static final Log log = HttpLogging.forLogName(Main.class);

    public static void main(String[] args) {
        KeyValueService keyValueService = new KeyValueService();
        log.info("Starting...");
        Scanner scanner = new Scanner(System.in);
        log.info("Ready to accept requests");
        while (true) {
            try {
                String input = scanner.nextLine().trim();
                if (input.equals("exit")) {
                    log.info("Exiting...");
                    break;
                }

                String[] parts = input.trim().split(" +");
                if (parts.length < 2) {
                    log.error("Not enough arguments");
                    continue;
                }

                String command = parts[0];
                if (command.equalsIgnoreCase("set")) {
                    if (parts.length < 3) {
                        log.error("Not enough arguments for `set` command");
                        continue;
                    }
                    KeyValueDto keyValueDto = new KeyValueDto(parts[1], parts[2]);
                    KeyValueViewModel keyValueViewModel = keyValueService.set(keyValueDto);
                    System.out.println("set performed: " + keyValueViewModel);
                    continue;
                }

                if (command.equalsIgnoreCase("get")) {
                    KeyValueViewModel keyValueViewModel = keyValueService.get(parts[1]);
                    System.out.println("get performed: " + keyValueViewModel);
                    continue;
                }

                log.error(String.format("There is no command %s", command));
            } catch (RuntimeException e) {
                log.error(e.getMessage());
            }
        }
    }
}