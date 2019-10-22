package io.metadew.iesi.server.rest.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;

@Configuration
@ConfigurationProperties(prefix = "security.jwt")
public class SecurityPropertiesServer {

    private Resource keyStore;
    private String keyStorePassword;
    private String keyPairAlias;
    private String keyPairPassword;
    private String heyStoreType;

    public Resource getKeyStore() {
        return keyStore;
    }

    public void setKeyStore(Resource keyStore) {
        this.keyStore = keyStore;
    }

    public String getKeyStorePassword() {
        return keyStorePassword;
    }

    public void setKeyStorePassword(String keyStorePassword) {
        this.keyStorePassword = keyStorePassword;
    }

    public String getKeyPairAlias() {
        return keyPairAlias;
    }

    public void setKeyPairAlias(String keyPairAlias) {
        this.keyPairAlias = keyPairAlias;
    }

    public String getKeyPairPassword() {
        return keyPairPassword;
    }

    public void setKeyPairPassword(String keyPairPassword) {
        this.keyPairPassword = keyPairPassword;
    }

    public String getHeyStoreType() {
        return heyStoreType;
    }

    public void setHeyStoreType(String heyStoreType) {
        this.heyStoreType = heyStoreType;
    }
}
