package ru.itmo.highload.service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.data.mongo.MongoDataAutoConfiguration;
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;
import ru.itmo.highload.service.config.SpringConfig;
import ru.itmo.highload.service.web.KeyValueController;
import ru.itmo.highload.service.web.PingController;
import ru.itmo.highload.service.web.ReplicationController;

@SpringBootApplication(exclude = {
        MongoAutoConfiguration.class,
        MongoDataAutoConfiguration.class
})
@Import({SpringConfig.class})
@ComponentScan({"ru.itmo.highload.service.kv", "ru.itmo.highload.service.config"})
@ComponentScan(basePackageClasses = {KeyValueController.class,
        PingController.class,
        ReplicationController.class})
public class StorageServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(StorageServiceApplication.class, args);
    }

}
