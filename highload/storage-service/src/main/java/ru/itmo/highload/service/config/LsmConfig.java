package ru.itmo.highload.service.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(basePackages = "ru.itmo.highload.service.kv.lsm")
public class LsmConfig {
}
