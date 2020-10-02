package io.metadew.iesi.metadata.configuration.security;

import lombok.Data;

@Data
public class SecurityGroupConfiguration {

    private static SecurityGroupConfiguration INSTANCE;
    public synchronized static SecurityGroupConfiguration getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new SecurityGroupConfiguration();
        }
        return INSTANCE;
    }

    private SecurityGroupConfiguration() {
    }

}
