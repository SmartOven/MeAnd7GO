package ru.itmo.highload.service.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
@Getter
public class ShardingConfig {
    @Value("${lsm.sharding.method:consistent}")
    private String shardingMethod;

    @Value("${lsm.sharding.method.scaleConst:3.814697267401356840227431077616e-6}")
    private double scaleConst;
}
