package io.metadew.iesi.framework.execution;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.metadew.iesi.common.config.*;
import io.metadew.iesi.framework.configuration.FrameworkConfiguration;
import io.metadew.iesi.framework.definition.FrameworkInitializationFile;
import io.metadew.iesi.metadata.configuration.FrameworkPluginConfiguration;
import io.metadew.iesi.metadata.repository.configuration.MetadataRepositoryConfiguration;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Properties;

public class FrameworkControl {

    private Properties properties;
    private List<MetadataRepositoryConfiguration> metadataRepositoryConfigurations;
    private List<FrameworkPluginConfiguration> frameworkPluginConfigurationList;
    private String logonType;


    private static FrameworkControl INSTANCE;

    public synchronized static FrameworkControl getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new FrameworkControl();
        }
        return INSTANCE;
    }

    private FrameworkControl() {
    }

    public void init(Properties properties, List<MetadataRepositoryConfiguration> metadataRepositoryConfigurations,
                     List<FrameworkPluginConfiguration> frameworkPluginConfigurationList, String logonType) {
        this.properties = properties;
        this.metadataRepositoryConfigurations = metadataRepositoryConfigurations;
        this.frameworkPluginConfigurationList = frameworkPluginConfigurationList;
        this.logonType = logonType;
    }

    public void init(String logonType, FrameworkInitializationFile frameworkInitializationFile) {
        this.logonType = logonType;
        this.properties = new Properties();
        this.metadataRepositoryConfigurations = new ArrayList<>();
        this.frameworkPluginConfigurationList = new ArrayList<>();
        properties.put(FrameworkConfiguration.getInstance().getFrameworkCode() + ".home", FrameworkConfiguration.getInstance().getFrameworkHome());
        readSettingFiles(frameworkInitializationFile.getName());
    }

    public void init(String assemblyHome) {
        this.properties = new Properties();
        addSetting("iesi.home", assemblyHome);
        addSetting("iesi.identifier", "iesi");
        addSetting("iesi.metadata.repository.instance.name", "");
        this.metadataRepositoryConfigurations = new ArrayList<>();
        this.frameworkPluginConfigurationList = new ArrayList<>();
    }

    // Methods
    private void readSettingFiles(String initializationFile) {
        try {
            File file = new File(this.resolveConfiguration("#" + FrameworkConfiguration.getInstance().getFrameworkCode()
                    + ".home#/conf/" + initializationFile));
            BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
            String readLine;

            while ((readLine = bufferedReader.readLine()) != null) {
                String innerpart = readLine.trim();
                String[] parts = innerpart.split(",");

                String key = parts[0];
                String type = parts[1];
                String value = parts[2];
                value = this.resolveConfiguration(value);

                ConfigFile configFile;
                if (key.equalsIgnoreCase("linux")) {
                    configFile = new LinuxConfigFile(value);
                } else if (key.equalsIgnoreCase("windows")) {
                    configFile = new WindowsConfigFile(value);
                } else if (key.equalsIgnoreCase("keyvalue")) {
                    configFile = new KeyValueConfigFile(value);
                } else {
                    continue;
                }

                if (type.trim().equalsIgnoreCase("repository")) {
                    MetadataRepositoryConfiguration metadataRepositoryConfiguration = new MetadataRepositoryConfiguration(configFile);
                    this.getMetadataRepositoryConfigurations().add(metadataRepositoryConfiguration);
                } else if (type.trim().equalsIgnoreCase("plugin")) {
                    FrameworkPluginConfiguration frameworkPluginConfiguration = new FrameworkPluginConfiguration(configFile);
                    this.getFrameworkPluginConfigurationList().add(frameworkPluginConfiguration);
                } else {
                    properties.putAll(configFile.getProperties());
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    public void setSettingsList(String input) {
        KeyValueConfigList keyValueConfigList = new KeyValueConfigList(input);
        properties.putAll(keyValueConfigList.getProperties());
    }

    public String resolveConfiguration(String input) {
        int openPos;
        int closePos;
        String variable_char = "#";
        String midBit;
        Optional<String> replaceValue;
        String temp = input;
        while (temp.indexOf(variable_char) > 0 || temp.startsWith(variable_char)) {
            openPos = temp.indexOf(variable_char);
            closePos = temp.indexOf(variable_char, openPos + 1);
            midBit = temp.substring(openPos + 1, closePos);

            // Try to find a configuration value
            // If none is found, null is set by default
            replaceValue = this.getProperty(midBit);

            // Replacing the value if found
            if (replaceValue.isPresent()) {
                input = input.replaceAll(variable_char + midBit + variable_char, replaceValue.get());
            }
            temp = temp.substring(closePos + 1, temp.length());

        }
        return input;
    }

    public void addKeyValueConfigFile(String path) {
        KeyValueConfigFile keyValueConfigFile = new KeyValueConfigFile(path);
        properties.putAll(keyValueConfigFile.getProperties());
    }

    public void addSetting(String key, String value) {
        properties.put(key, value);
    }

    public Optional<String> getProperty(String input) {
        return Optional.ofNullable(properties.getProperty(input));
//        if (output == null) {
//            throw new RuntimeException("Unknown configuration value lookup requested: " + input);
//        }
//        return output;
    }

    public ConfigFile getConfigFile(String type, String filePath) {
        ObjectMapper objectMapper = new ObjectMapper();
        ConfigFile configFile = null;
        if (type.equalsIgnoreCase("linux")) {
            LinuxConfigFile linuxConfigFile = new LinuxConfigFile(filePath);
            configFile = objectMapper.convertValue(linuxConfigFile, ConfigFile.class);
        } else if (type.equalsIgnoreCase("windows")) {
            WindowsConfigFile windowsConfigFile = new WindowsConfigFile(filePath);
            configFile = objectMapper.convertValue(windowsConfigFile, ConfigFile.class);
        } else if (type.equalsIgnoreCase("keyvalue")) {
            KeyValueConfigFile keyValueConfigFile = new KeyValueConfigFile(filePath);
            configFile = objectMapper.convertValue(keyValueConfigFile, ConfigFile.class);
        }

        configFile.setFilePath(filePath);

        return configFile;
    }

    // Getters and Setters
    public Properties getProperties() {
        return properties;
    }

    private void setProperties(Properties properties) {
        this.properties = properties;
    }

    public List<MetadataRepositoryConfiguration> getMetadataRepositoryConfigurations() {
        return metadataRepositoryConfigurations;
    }

    public void setMetadataRepositoryConfigurations(
            List<MetadataRepositoryConfiguration> metadataRepositoryConfigurations) {
        this.metadataRepositoryConfigurations = metadataRepositoryConfigurations;
    }

    public String getLogonType() {
        return logonType;
    }

    public void setLogonType(String logonType) {
        this.logonType = logonType;
    }


    public List<FrameworkPluginConfiguration> getFrameworkPluginConfigurationList() {
        return frameworkPluginConfigurationList;
    }

    public void setFrameworkPluginConfigurationList(List<FrameworkPluginConfiguration> frameworkPluginConfigurationList) {
        this.frameworkPluginConfigurationList = frameworkPluginConfigurationList;
    }

}