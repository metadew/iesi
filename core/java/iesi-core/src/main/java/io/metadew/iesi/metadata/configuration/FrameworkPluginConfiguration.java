package io.metadew.iesi.metadata.configuration;

import io.metadew.iesi.common.config.ConfigFile;
import io.metadew.iesi.framework.configuration.FrameworkConfiguration;
import io.metadew.iesi.framework.configuration.FrameworkFolderConfiguration;
import io.metadew.iesi.framework.configuration.FrameworkSettingConfiguration;
import io.metadew.iesi.framework.definition.FrameworkPlugin;

import java.io.File;

public class FrameworkPluginConfiguration {

    private ConfigFile configFile;
    private FrameworkPlugin frameworkPlugin;

    // Constructors
    public FrameworkPluginConfiguration(ConfigFile configFile) {
        this.setFrameworkPlugin(new FrameworkPlugin());
        this.getFrameworkPlugin()
                .setName(configFile.getProperty(
                        FrameworkSettingConfiguration.getInstance().getSettingPath("plugin.name").get()).get()
                        .toLowerCase());
        StringBuilder path = new StringBuilder();
        path.append(FrameworkFolderConfiguration.getInstance().getFolderAbsolutePath("plugins"));
        path.append(File.separator);
        path.append(this.getFrameworkPlugin().getName());
        this.getFrameworkPlugin().setPath(path.toString());
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