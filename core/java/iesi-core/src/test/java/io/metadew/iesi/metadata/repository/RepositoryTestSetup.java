package io.metadew.iesi.metadata.repository;

import io.metadew.iesi.common.configuration.Configuration;
import io.metadew.iesi.common.configuration.metadata.repository.MetadataRepositoryConfiguration;

public class RepositoryTestSetup {


    public static DesignMetadataRepository getDesignMetadataRepository() {
        // Configuration.getInstance();
        return MetadataRepositoryConfiguration.getInstance().getDesignMetadataRepository();
    }

    public static ConnectivityMetadataRepository getConnectivityMetadataRepository() {
        // Configuration.getInstance();
        return MetadataRepositoryConfiguration.getInstance().getConnectivityMetadataRepository();
    }

}
