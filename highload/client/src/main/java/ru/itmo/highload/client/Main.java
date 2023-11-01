package ru.itmo.highload.client;


import org.apache.commons.logging.Log;
import org.springframework.http.HttpLogging;
import ru.itmo.highload.client.kv.KeyValueDto;
import ru.itmo.highload.client.kv.KeyValueViewModel;
import ru.itmo.highload.client.kv.KeyValueService;

public class Main {
    private static final Log log = HttpLogging.forLogName(Main.class);

    public static void main(String[] args) {
        testMemTableDump();
//        KeyValueService keyValueService = new KeyValueService();
//        log.info("Starting...");
//        try {
//            if (args.length < 2) {
//                log.error("Not enough arguments");
//                return;
//            }
//
//            String command = args[0];
//            if (command.equalsIgnoreCase("set")) {
//                if (args.length < 3) {
//                    log.error("Not enough arguments for `set` command");
//                    return;
//                }
//                KeyValueDto keyValueDto = new KeyValueDto(args[1], args[2]);
//                KeyValueViewModel keyValueViewModel = keyValueService.set(keyValueDto);
//                System.out.println("set performed: " + keyValueViewModel);
//                return;
//            }
//
//            if (command.equalsIgnoreCase("get")) {
//                KeyValueViewModel keyValueViewModel = keyValueService.get(args[1]);
//                System.out.println("get performed: " + keyValueViewModel);
//                return;
//            }
//
//            log.error(String.format("There is no command %s", command));
//        } catch (RuntimeException e) {
//            log.error(e.getMessage());
//        }
    }

    private static void testMemTableDump() {
        int i = 1;

        KeyValueService keyValueService = new KeyValueService();
        while (keyValueService.getMemUsage() < 51.0) {
            String s = String.valueOf(i).repeat(1000);
            String key = "key-" + s;
            String value = "value-" + s;
            KeyValueDto keyValueDto = new KeyValueDto(key, value);
            KeyValueViewModel ignored = keyValueService.set(keyValueDto);
            i++;
        }

        for (int j = i; j < 2 * i; j++) {
            String s = String.valueOf(j).repeat(1000);
            String key = "key-" + s;
            String value = "value-" + s;
            KeyValueDto keyValueDto = new KeyValueDto(key, value);
            KeyValueViewModel ignored = keyValueService.set(keyValueDto);
        }

        KeyValueViewModel result = keyValueService.get("key-" + String.valueOf(1).repeat(1000));
        System.out.println(result);
    }
}