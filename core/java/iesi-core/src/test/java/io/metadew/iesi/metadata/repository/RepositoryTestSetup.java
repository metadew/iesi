package io.metadew.iesi.metadata.repository;

import io.metadew.iesi.common.configuration.Configuration;
import io.metadew.iesi.common.configuration.metadata.repository.MetadataRepositoryConfiguration;

public class RepositoryTestSetup {

    private static final String DB_NAME = "test;DB_CLOSE_DELAY=-1";
    private static final String DB_USER = "";
    private static final String DB_PASSWORD = "";

    public static DesignMetadataRepository getDesignMetadataRepository() {
        Configuration.getInstance();
        return MetadataRepositoryConfiguration.getInstance().getDesignMetadataRepository();
    }

    public static ConnectivityMetadataRepository getConnectivityMetadataRepository() {
        Configuration.getInstance();
        return MetadataRepositoryConfiguration.getInstance().getConnectivityMetadataRepository();
    }

}
