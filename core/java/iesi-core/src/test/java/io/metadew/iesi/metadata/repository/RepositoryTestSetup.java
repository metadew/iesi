package io.metadew.iesi.metadata.repository;

import io.metadew.iesi.common.configuration.Configuration;
import io.metadew.iesi.common.configuration.metadata.repository.MetadataRepositoryConfiguration;

public class RepositoryTestSetup {


    public static DesignMetadataRepository getDesignMetadataRepository() {
        Configuration.getInstance();
        return MetadataRepositoryConfiguration.getInstance().getDesignMetadataRepository();
    }

    public static ConnectivityMetadataRepository getConnectivityMetadataRepository() {
        Configuration.getInstance();
        return MetadataRepositoryConfiguration.getInstance().getConnectivityMetadataRepository();
    }

    public static ExecutionServerMetadataRepository getExecutionServerMetadataRepository() {
        Configuration.getInstance();
        return MetadataRepositoryConfiguration.getInstance().getExecutionServerMetadataRepository();
    }

    public static TraceMetadataRepository getTraceMetadataRepository() {
        Configuration.getInstance();
        return MetadataRepositoryConfiguration.getInstance().getTraceMetadataRepository();
    }

    public static ResultMetadataRepository getResultMetadataRepository() {
        Configuration.getInstance();
        return MetadataRepositoryConfiguration.getInstance().getResultMetadataRepository();
    }
}
