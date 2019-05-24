package io.metadew.iesi.common.config;

import java.util.Optional;
import java.util.Properties;

public class ConfigFile {

    private Properties properties;

    private String filePath;

    public ConfigFile() {

        this.properties = new Properties();
    }

    public Properties getProperties() {
        return properties;
    }

    public void setProperties(Properties properties) {
        this.properties = properties;
    }

    public Optional<String> getProperty(String key) {
        if (properties.getProperty(key).isEmpty()) {
            return Optional.empty();
        } else {
            return Optional.ofNullable(this.properties.getProperty(key));
        }
    }

    public void setProperty(String key, String value) {
        this.properties.setProperty(key, value);
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

}