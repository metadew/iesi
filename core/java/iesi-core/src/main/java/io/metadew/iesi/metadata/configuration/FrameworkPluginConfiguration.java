package io.metadew.iesi.metadata.configuration;

import io.metadew.iesi.common.config.ConfigFile;
import io.metadew.iesi.framework.configuration.FrameworkFolderConfiguration;
import io.metadew.iesi.framework.configuration.FrameworkSettingConfiguration;
import io.metadew.iesi.framework.definition.FrameworkPlugin;

import java.io.File;

public class FrameworkPluginConfiguration {

    private static FrameworkPluginConfiguration INSTANCE;

    public synchronized static FrameworkPluginConfiguration getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new FrameworkPluginConfiguration();
        }
        return INSTANCE;
    }

    private FrameworkPluginConfiguration() {}

    public FrameworkPlugin from(ConfigFile configFile) {
        String name = configFile
                .getProperty(FrameworkSettingConfiguration.getInstance()
                        .getSettingPath("plugin.name").get()).get()
                .toLowerCase();
        return new FrameworkPlugin(name,
                FrameworkFolderConfiguration.getInstance().getFolderAbsolutePath("plugins") + File.separator + name);
    }

}