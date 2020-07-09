package io.metadew.iesi.metadata.repository;

import io.metadew.iesi.common.configuration.Configuration;
import io.metadew.iesi.common.configuration.metadata.repository.MetadataRepositoryConfiguration;

public class RepositoryTestSetup {

    private static final String DB_NAME = "test;DB_CLOSE_DELAY=-1";
    private static final String DB_USER = "";
    private static final String DB_PASSWORD = "";

<<<<<<< HEAD
    public static DesignMetadataRepository getDesignMetadataRepository() throws Exception {
        Configuration configuration = Configuration.getInstance();
//        DesignMetadataRepository designMetadataRepository = new DesignMetadataRepository("", getRepositoryCoordinator(), "", "",
//                getMetadataObjects(DESIGN_OBJECTS), getMetadataTables(DESIGN_TABLES));
//        designMetadataRepository.createAllTables();
=======
    public static DesignMetadataRepository getDesignMetadataRepository() {
        Configuration.getInstance();
>>>>>>> 8a45560172030c8b38e3f30c0ea2cc97f9ba0888
        return MetadataRepositoryConfiguration.getInstance().getDesignMetadataRepository();
    }

    public static ConnectivityMetadataRepository getConnectivityMetadataRepository() throws Exception {
        Configuration.getInstance();
        return MetadataRepositoryConfiguration.getInstance().getConnectivityMetadataRepository();
    }

}
