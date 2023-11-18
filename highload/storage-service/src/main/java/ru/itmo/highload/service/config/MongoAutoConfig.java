package ru.itmo.highload.service.config;

import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile("mongo")
@ComponentScan(basePackages = "ru.itmo.highload.service.kv.mongo")
public class MongoAutoConfig extends MongoAutoConfiguration {
}
