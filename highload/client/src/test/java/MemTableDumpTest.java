import org.junit.jupiter.api.Test;
import ru.itmo.highload.client.kv.KeyValueDto;
import ru.itmo.highload.client.kv.KeyValueService;
import ru.itmo.highload.client.kv.KeyValueViewModel;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class MemTableDumpTest {
    @Test
    void test1() {
        int reqCount = 10000;
        KeyValueService keyValueService = new KeyValueService();
        for (int i = 0; i <= reqCount; i++) {
            String key = "key-" + i;
            String value = "value-" + i;
            KeyValueDto keyValueDto = new KeyValueDto(key, value);
            KeyValueViewModel ignored = keyValueService.set(keyValueDto);
        }

        KeyValueViewModel result = keyValueService.get("key-" + reqCount);
        assertEquals("value-" + reqCount, result.value());
    }
}
