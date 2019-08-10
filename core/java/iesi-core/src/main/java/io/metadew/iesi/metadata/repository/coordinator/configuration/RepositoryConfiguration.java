package io.metadew.iesi.metadata.repository.coordinator.configuration;

import io.metadew.iesi.common.config.ConfigFile;
import io.metadew.iesi.framework.configuration.FrameworkSettingConfiguration;
import io.metadew.iesi.framework.crypto.FrameworkCrypto;
import io.metadew.iesi.metadata.repository.coordinator.RepositoryCoordinator;

public abstract class RepositoryConfiguration {
	
    RepositoryConfiguration(ConfigFile configFile) {
    	fromConfigFile(configFile);
    }

    abstract void fromConfigFile(ConfigFile configFile);

    public abstract RepositoryCoordinator toRepository();
    
    public String getSettingValue(ConfigFile configFile, String settingPath) {
    	String output = null;
        if (FrameworkSettingConfiguration.getInstance().getSettingPath(settingPath).isPresent() &&
                configFile.getProperty(FrameworkSettingConfiguration.getInstance().getSettingPath(settingPath).get()).isPresent()) {
        	output =FrameworkCrypto.getInstance().decryptIfNeeded(configFile.getProperty(FrameworkSettingConfiguration.getInstance().getSettingPath(settingPath).get()).get());
        }
        return output;
    }

}
