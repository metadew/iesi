package io.metadew.iesi.metadata.repository;

import io.metadew.iesi.SpringContext;
import io.metadew.iesi.common.configuration.Configuration;
import io.metadew.iesi.common.configuration.metadata.repository.MetadataRepositoryConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(classes = { Configuration.class, SpringContext.class, MetadataRepositoryConfiguration.class })
public class RepositoryTestSetup {

    @Autowired
    private static MetadataRepositoryConfiguration metadataRepositoryConfiguration;

    public static DesignMetadataRepository getDesignMetadataRepository() {
        return metadataRepositoryConfiguration.getDesignMetadataRepository();
    }

    public static ConnectivityMetadataRepository getConnectivityMetadataRepository() {
        return metadataRepositoryConfiguration.getConnectivityMetadataRepository();
    }

}
