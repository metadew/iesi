package io.metadew.iesi.metadata.repository.coordinator.configuration;

import io.metadew.iesi.common.config.ConfigFile;
import io.metadew.iesi.framework.configuration.FrameworkSettingConfiguration;
import io.metadew.iesi.framework.crypto.FrameworkCrypto;
import io.metadew.iesi.metadata.repository.coordinator.RepositoryCoordinator;

import java.util.Optional;

public class FileStoreRepositoryConfiguration extends RepositoryConfiguration {

    private String path;


    public FileStoreRepositoryConfiguration(ConfigFile configFile, FrameworkSettingConfiguration frameworkSettingConfiguration, FrameworkCrypto frameworkCrypto) {
        super(configFile, frameworkSettingConfiguration, frameworkCrypto);
    }

    @Override
    void fromConfigFile(ConfigFile configFile, FrameworkSettingConfiguration frameworkSettingConfiguration, FrameworkCrypto frameworkCrypto) {
    	path = getSettingValue(frameworkSettingConfiguration, frameworkCrypto, configFile, "metadata.repository.filestore.path");
    }

    @Override
    public RepositoryCoordinator toRepository() {
        return null;
    }

    public Optional<String> getPath() {
        return Optional.ofNullable(path);
    }
}
