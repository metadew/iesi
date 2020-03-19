package io.metadew.iesi.framework.execution;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.metadew.iesi.common.config.*;
import io.metadew.iesi.framework.configuration.FrameworkConfiguration;
import io.metadew.iesi.framework.definition.FrameworkInitializationFile;
import io.metadew.iesi.framework.definition.FrameworkPlugin;
import io.metadew.iesi.metadata.configuration.FrameworkPluginConfiguration;
import io.metadew.iesi.metadata.repository.configuration.MetadataRepositoryConfiguration;
import lombok.extern.log4j.Log4j2;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Properties;

import static org.apache.commons.io.FilenameUtils.separatorsToUnix;

@Log4j2
public class FrameworkControl {

    private Properties properties;
    private List<MetadataRepositoryConfiguration> metadataRepositoryConfigurations;
    private List<FrameworkPlugin> frameworkPlugins;
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
                     List<FrameworkPlugin> frameworkPlugins, String logonType) {
        this.properties = properties;
        this.metadataRepositoryConfigurations = metadataRepositoryConfigurations;
        this.frameworkPlugins = frameworkPlugins;
        this.logonType = logonType;
    }

    public void init(String logonType, FrameworkInitializationFile frameworkInitializationFile) {
        this.logonType = logonType;
        this.properties = new Properties();
        this.metadataRepositoryConfigurations = new ArrayList<>();
        this.frameworkPlugins = new ArrayList<>();
        properties.put(FrameworkConfiguration.getInstance().getFrameworkCode() + ".home", separatorsToUnix(FrameworkConfiguration.getInstance().getFrameworkHome().toString()));
        readSettingFiles(FrameworkConfiguration.getInstance().getFrameworkHome().resolve("conf").resolve(frameworkInitializationFile.getName()));
    }

    public void init(String assemblyHome) {
        this.properties = new Properties();
        addSetting("iesi.home", assemblyHome);
        addSetting("iesi.identifier", "iesi");
        addSetting("iesi.metadata.repository.instance.name", "");
        this.metadataRepositoryConfigurations = new ArrayList<>();
        this.frameworkPlugins = new ArrayList<>();
    }

    // Methods
    private void readSettingFiles(Path initializationFile) {
        try {
            File file = initializationFile.toFile();
            BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
            String readLine;

            while ((readLine = bufferedReader.readLine()) != null) {
                String innerpart = readLine.trim();
                String[] parts = innerpart.split(",");

                String key = parts[0];
                String type = parts[1];
                String value = this.resolveConfiguration(parts[2]);
                Path path = Paths.get(value);
                ConfigFile configFile;
                if (key.equalsIgnoreCase("linux")) {
                    configFile = new LinuxConfigFile(path);
                } else if (key.equalsIgnoreCase("windows")) {
                    configFile = new WindowsConfigFile(path);
                } else if (key.equalsIgnoreCase("keyvalue")) {
                    configFile = new KeyValueConfigFile(path);
                } else {
                    continue;
                }

                if (type.trim().equalsIgnoreCase("repository")) {
                    log.info("Configuring repository according to " + configFile.getFilePath());
                    MetadataRepositoryConfiguration metadataRepositoryConfiguration = new MetadataRepositoryConfiguration(configFile);
                    this.getMetadataRepositoryConfigurations().add(metadataRepositoryConfiguration);
                } else if (type.trim().equalsIgnoreCase("plugin")) {
                    log.trace("Configuring plugin according to " + configFile.getFilePath());
                    this.getFrameworkPlugins().add(FrameworkPluginConfiguration.getInstance().from(configFile));
                    log.trace("Configured " + FrameworkPluginConfiguration.getInstance().from(configFile));
                } else {
                    properties.putAll(configFile.getProperties());
                }
            }
            bufferedReader.close();
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
            LinuxConfigFile linuxConfigFile = new LinuxConfigFile(Paths.get(filePath));
            configFile = objectMapper.convertValue(linuxConfigFile, ConfigFile.class);
        } else if (type.equalsIgnoreCase("windows")) {
            WindowsConfigFile windowsConfigFile = new WindowsConfigFile(Paths.get(filePath));
            configFile = objectMapper.convertValue(windowsConfigFile, ConfigFile.class);
        } else if (type.equalsIgnoreCase("keyvalue")) {
            KeyValueConfigFile keyValueConfigFile = new KeyValueConfigFile(Paths.get(filePath));
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


    public List<FrameworkPlugin> getFrameworkPlugins() {
        return frameworkPlugins;
    }

}