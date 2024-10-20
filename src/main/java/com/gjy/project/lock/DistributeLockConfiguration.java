package com.gjy.project.lock;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@EnableConfigurationProperties(DistributeLockProperties.class)
@Configuration
public class DistributeLockConfiguration {

    @Bean
    public RedissonClient redisson() {
        Config config = new Config();
        config.useSingleServer().setAddress("redis://127.0.0.1:6379");
        return Redisson.create(config);
    }

    @Bean
    public DistributeLock redisDistributeLock(RedissonClient redisson, DistributeLockProperties properties) {
        return new RedisDistributeLock(redisson, properties);
    }
}
