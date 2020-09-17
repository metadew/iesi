package io.metadew.iesi.gcp.common.framework;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.metadew.iesi.gcp.common.configuration.Configuration;
import lombok.extern.log4j.Log4j2;

import java.text.MessageFormat;
import java.util.*;

@Log4j2
public class FrameworkConfiguration {

    private static FrameworkConfiguration INSTANCE;
    private static final String configurationKey = "framework";
    private static final String folderConfigurationKey = "folders";
    private static final String settingConfigurationKey = "settings";

    private Map<String, FrameworkSetting> frameworkSettings;
    private Map<String, FrameworkFolder> frameworkFolders;

    public synchronized static FrameworkConfiguration getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new FrameworkConfiguration();
        }
        return INSTANCE;
    }

    private FrameworkConfiguration() {
        frameworkSettings = new HashMap<>();
        frameworkFolders = new HashMap<>();
        if (!Configuration.getInstance().getProperties().containsKey(configurationKey) || !(Configuration.getInstance().getProperties().get(configurationKey) instanceof Map)) {
            log.warn("no framework configuration found on system variable, classpath or filesystem");
        } else {
            loadSettingConfigurations();
            loadFolderConfigurations();
        }
    }

    @SuppressWarnings("unchecked")
    private void loadSettingConfigurations() {
        if (!((Map<String, Object>) Configuration.getInstance().getProperties().get(configurationKey)).containsKey(settingConfigurationKey)) {
            log.warn("no framework setting configuration found on system variable, classpath or filesystem");
        } else {
            Map<String, Object> frameworkSettingConfigurations = (Map<String, Object>) ((Map<String, Object>) Configuration.getInstance().getProperties()
                    .get(configurationKey))
                    .get(settingConfigurationKey);
            ObjectMapper objectMapper = new ObjectMapper();
            for (Map.Entry<String, Object> entry : frameworkSettingConfigurations.entrySet()) {
                frameworkSettings.put(entry.getKey(), objectMapper.convertValue(entry.getValue(), FrameworkSetting.class));
            }
        }
    }

    @SuppressWarnings("unchecked")
    private void loadFolderConfigurations() {
        if (!((Map<String, Object>) Configuration.getInstance().getProperties().get(configurationKey)).containsKey(folderConfigurationKey)) {
            log.warn("no framework folder configuration found on system variable, classpath or filesystem");
        } else {
            Map<String, Object> frameworkSettingConfigurations = (Map<String, Object>) ((Map<String, Object>) Configuration.getInstance().getProperties()
                    .get(configurationKey))
                    .get(folderConfigurationKey);
            ObjectMapper objectMapper = new ObjectMapper();
            for (Map.Entry<String, Object> entry : frameworkSettingConfigurations.entrySet()) {
                frameworkFolders.put(entry.getKey(), objectMapper.convertValue(entry.getValue(), FrameworkFolder.class));
            }
        }
    }

    public Map<String, FrameworkFolder> getFrameworkFolders() {
        return frameworkFolders;

    }

    public Optional<FrameworkFolder> getFrameworkFolder(String frameworkFolder) {
        return Optional.ofNullable(frameworkFolders.get(frameworkFolder));
    }

    public FrameworkFolder getMandatoryFrameworkFolder(String frameworkFolder) {
        return Optional.ofNullable(frameworkFolders.get(frameworkFolder))
                .orElseThrow(() -> new RuntimeException(MessageFormat.format("No FrameworkFolder ''{0}'' found", frameworkFolder)));
    }
}