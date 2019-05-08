package io.metadew.iesi.metadata.repository.coordinator.configuration;

import io.metadew.iesi.common.config.ConfigFile;
import io.metadew.iesi.framework.configuration.FrameworkSettingConfiguration;
import io.metadew.iesi.metadata.repository.coordinator.RepositoryCoordinator;

import java.util.Optional;

public class FileStoreRepositoryConfiguration extends RepositoryConfiguration {

    private String path;


    public FileStoreRepositoryConfiguration(ConfigFile configFile, FrameworkSettingConfiguration frameworkSettingConfiguration) {
        super(configFile, frameworkSettingConfiguration);
    }

    @Override
    void fromConfigFile(ConfigFile configFile, FrameworkSettingConfiguration frameworkSettingConfiguration) {
        if (frameworkSettingConfiguration.getSettingPath("metadata.repository.filestore.path").isPresent() &&
                configFile.getProperty(frameworkSettingConfiguration.getSettingPath("metadata.repository.filestore.path").get()).isPresent()) {

            path = configFile.getProperty(frameworkSettingConfiguration.getSettingPath("metadata.repository.filestore.path").get()).get();
        } else {
            throw new RuntimeException("Could not initialize file store configuration");
        }
    }

    @Override
    public RepositoryCoordinator toRepository() {
        return null;
    }

    public Optional<String> getPath() {
        return Optional.ofNullable(path);
    }
}
