package io.metadew.iesi.common.configuration.metadata.ldap;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.metadew.iesi.common.configuration.Configuration;
import io.metadew.iesi.common.configuration.metadata.MetadataConfiguration;
import lombok.extern.log4j.Log4j2;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Log4j2
public class MetadataLdapConfiguration {

    private static MetadataLdapConfiguration INSTANCE;
    private static final String actionsKey = "ldap-configuration";

    private Map<String, String> map;

    public synchronized static MetadataLdapConfiguration getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new MetadataLdapConfiguration();
        }
        return INSTANCE;
    }

    private MetadataLdapConfiguration() {
        map = new HashMap<>();
        if (containsConfiguration()) {
            Map<String, Object> frameworkSettingConfigurations = (Map<String, Object>) ((Map<String, Object>) Configuration.getInstance().getProperties()
                    .get(MetadataConfiguration.configurationKey))
                    .get(actionsKey);
            ObjectMapper objectMapper = new ObjectMapper();
            for (Map.Entry<String, Object> entry : frameworkSettingConfigurations.entrySet()) {
                map.put(entry.getKey(), objectMapper.convertValue(entry.getValue(), String.class));
            }
        } else {
            //TODO: Add proper comments
            log.warn("no component type configurations found on system variable, classpath or filesystem");

        }
    }

    public String getProperty(String propertyName) {
        return map.get(propertyName);
    }

    public Map<String, String> getComponentTypes() {
        return map;
    }

    private boolean containsConfiguration() {
        return Configuration.getInstance().getProperties().containsKey(MetadataConfiguration.configurationKey) &&
                (Configuration.getInstance().getProperties().get(MetadataConfiguration.configurationKey) instanceof Map) &&
                ((Map<String, Object>) Configuration.getInstance().getProperties().get(MetadataConfiguration.configurationKey)).containsKey(actionsKey) &&
                ((Map<String, Object>) Configuration.getInstance().getProperties().get(MetadataConfiguration.configurationKey)).get(actionsKey) instanceof Map;
    }
}
