package io.metadew.iesi.metadata_repository.repository.configuration;

import io.metadew.iesi.common.config.ConfigFile;
import io.metadew.iesi.framework.configuration.FrameworkSettingConfiguration;
import io.metadew.iesi.metadata_repository.repository.Repository;

public abstract class RepositoryConfiguration {

    RepositoryConfiguration(ConfigFile configFile, FrameworkSettingConfiguration frameworkSettingConfiguration) {
        fromConfigFile(configFile, frameworkSettingConfiguration);
    }

    abstract void fromConfigFile(ConfigFile configFile, FrameworkSettingConfiguration frameworkSettingConfiguration);
    public abstract Repository toRepository();

}
