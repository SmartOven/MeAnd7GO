package ru.itmo.highload.sharding;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.data.mongo.MongoDataAutoConfiguration;
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration;
import org.springframework.context.annotation.Import;
import ru.itmo.highload.sharding.config.ShardingConfig;

@SpringBootApplication(exclude = {
        MongoAutoConfiguration.class,
        MongoDataAutoConfiguration.class
})
@Import(ShardingConfig.class)
public class ShardingProxyApplication {

    public static void main(String[] args) {
        SpringApplication.run(ShardingProxyApplication.class, args);
    }

}
