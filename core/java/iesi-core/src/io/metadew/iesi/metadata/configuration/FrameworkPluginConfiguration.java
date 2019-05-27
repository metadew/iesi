package io.metadew.iesi.metadata.configuration;

import io.metadew.iesi.common.config.ConfigFile;
import io.metadew.iesi.framework.configuration.FrameworkConfiguration;
import io.metadew.iesi.framework.definition.FrameworkPlugin;

import java.io.File;

public class FrameworkPluginConfiguration {

    private FrameworkConfiguration frameworkConfiguration;
    private ConfigFile configFile;
    private FrameworkPlugin frameworkPlugin;

    // Constructors
    public FrameworkPluginConfiguration(FrameworkConfiguration frameworkConfiguration, ConfigFile configFile) {
        this.setFrameworkConfiguration(frameworkConfiguration);
        this.setFrameworkPlugin(new FrameworkPlugin());
        this.getFrameworkPlugin()
                .setName(configFile.getProperty(
                        this.getFrameworkConfiguration().getSettingConfiguration().getSettingPath("plugin.name").get()).get()
                        .toLowerCase());
        StringBuilder path = new StringBuilder();
        path.append(this.getFrameworkConfiguration().getFolderConfiguration().getFolderAbsolutePath("plugins"));
        path.append(File.separator);
        path.append(this.getFrameworkPlugin().getName());
        this.getFrameworkPlugin().setPath(path.toString());
    }

    // Getters and Setters
    public FrameworkConfiguration getFrameworkConfiguration() {
        return frameworkConfiguration;
    }

    public void setFrameworkConfiguration(FrameworkConfiguration frameworkConfiguration) {
        this.frameworkConfiguration = frameworkConfiguration;
    }

    public ConfigFile getConfigFile() {
        return configFile;
    }

    public void setConfigFile(ConfigFile configFile) {
        this.configFile = configFile;
    }

    public FrameworkPlugin getFrameworkPlugin() {
        return frameworkPlugin;
    }

    public void setFrameworkPlugin(FrameworkPlugin frameworkPlugin) {
        this.frameworkPlugin = frameworkPlugin;
    }

}