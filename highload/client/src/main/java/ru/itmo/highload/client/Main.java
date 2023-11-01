package ru.itmo.highload.client;


import org.apache.commons.logging.Log;
import org.springframework.http.HttpLogging;
import ru.itmo.highload.client.kv.KeyValueDto;
import ru.itmo.highload.client.kv.KeyValueViewModel;
import ru.itmo.highload.client.kv.KeyValueService;

public class Main {
    private static final Log log = HttpLogging.forLogName(Main.class);

    public static void main(String[] args) {
        do1();
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

    private static void do1() {
        int reqCount = 100000;
        KeyValueService keyValueService = new KeyValueService();
        for (int i = 0; i <= reqCount; i++) {
            String key = "key-" + i;
            String value = "value-" + i;
            KeyValueDto keyValueDto = new KeyValueDto(key, value);
            KeyValueViewModel ignored = keyValueService.set(keyValueDto);
        }

        KeyValueViewModel result = keyValueService.get("key-" + reqCount);
        System.out.println(result);
    }
}