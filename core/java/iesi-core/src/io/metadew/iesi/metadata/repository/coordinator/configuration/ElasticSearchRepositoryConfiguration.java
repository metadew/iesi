package io.metadew.iesi.metadata.repository.coordinator.configuration;

import io.metadew.iesi.common.config.ConfigFile;
import io.metadew.iesi.framework.configuration.FrameworkSettingConfiguration;
import io.metadew.iesi.framework.crypto.FrameworkCrypto;
import io.metadew.iesi.metadata.repository.coordinator.RepositoryCoordinator;

public class ElasticSearchRepositoryConfiguration extends RepositoryConfiguration {

    private String url;


    public ElasticSearchRepositoryConfiguration(ConfigFile configFile, FrameworkSettingConfiguration frameworkSettingConfiguration, FrameworkCrypto frameworkCrypto) {
        super(configFile, frameworkSettingConfiguration, frameworkCrypto);
    }

    @Override
    void fromConfigFile(ConfigFile configFile, FrameworkSettingConfiguration frameworkSettingConfiguration, FrameworkCrypto frameworkCrypto) {

        if (frameworkSettingConfiguration.getSettingPath("metadata.repository.elasticsearch.url").isPresent() &&
                configFile.getProperty(frameworkSettingConfiguration.getSettingPath("metadata.repository.elasticsearch.url").get()).isPresent()) {

            url = configFile.getProperty(frameworkSettingConfiguration.getSettingPath("metadata.repository.elasticsearch.url").get()).get();
        } else {
            throw new RuntimeException("Could not initialize elasticsearch configuration");
        }
    }

    @Override
    public RepositoryCoordinator toRepository() {
        return null;
    }

    public String getUrl() {
        return url;
    }
}
