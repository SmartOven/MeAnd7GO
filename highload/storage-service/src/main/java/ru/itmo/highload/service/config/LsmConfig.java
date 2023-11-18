package ru.itmo.highload.service.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile("lsm")
@ComponentScan(basePackages = "ru.itmo.highload.service.kv.lsm")
public class LsmConfig {
}
