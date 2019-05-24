package io.metadew.iesi.metadata.repository.coordinator.configuration;

import io.metadew.iesi.common.config.ConfigFile;
import io.metadew.iesi.framework.configuration.FrameworkSettingConfiguration;
import io.metadew.iesi.framework.crypto.FrameworkCrypto;
import io.metadew.iesi.metadata.repository.coordinator.RepositoryCoordinator;

public abstract class RepositoryConfiguration {
	
    RepositoryConfiguration(ConfigFile configFile, FrameworkSettingConfiguration frameworkSettingConfiguration, FrameworkCrypto frameworkCrypto) {
    	fromConfigFile(configFile, frameworkSettingConfiguration, frameworkCrypto);
    }

    abstract void fromConfigFile(ConfigFile configFile, FrameworkSettingConfiguration frameworkSettingConfiguration,FrameworkCrypto frameworkCrypto);

    public abstract RepositoryCoordinator toRepository();

}
