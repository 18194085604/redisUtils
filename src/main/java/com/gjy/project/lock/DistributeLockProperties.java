package com.gjy.project.lock;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("distribute.lock.name")
public class DistributeLockProperties {

    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
