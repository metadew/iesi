package io.metadew.iesi.metadata.repository;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.metadew.iesi.connection.database.Database;
import io.metadew.iesi.connection.database.h2.H2Database;
import io.metadew.iesi.connection.database.h2.H2MemoryDatabaseConnection;
import io.metadew.iesi.common.configuration.Configuration;
import io.metadew.iesi.common.configuration.metadata.repository.MetadataRepositoryConfiguration;
import io.metadew.iesi.metadata.definition.DataObject;
import io.metadew.iesi.metadata.definition.MetadataObject;
import io.metadew.iesi.metadata.definition.MetadataTable;
import io.metadew.iesi.metadata.operation.DataObjectOperation;
import io.metadew.iesi.metadata.repository.coordinator.RepositoryCoordinator;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RepositoryTestSetup {

    private static final String DB_NAME = "test;DB_CLOSE_DELAY=-1";
    private static final String DB_USER = "";
    private static final String DB_PASSWORD = "";
    private static final String DESIGN_TABLES = "io.metadew.iesi.metadata/DesignTables.json";
    private static final String DESIGN_OBJECTS = "io.metadew.iesi.metadata/DesignObjects.json";
    private static final String CONNECTIVITY_TABLES = "io.metadew.iesi.metadata/ConnectivityTables.json";
    private static final String CONNECTIVITY_OBJECTS = "io.metadew.iesi.metadata/ConnectivityObjects.json";

    public static DesignMetadataRepository getDesignMetadataRepository() {
        Configuration.getInstance();
//        DesignMetadataRepository designMetadataRepository = new DesignMetadataRepository("", getRepositoryCoordinator(), "", "",
//                getMetadataObjects(DESIGN_OBJECTS), getMetadataTables(DESIGN_TABLES));
//        designMetadataRepository.createAllTables();
        return MetadataRepositoryConfiguration.getInstance().getDesignMetadataRepository();
    }

    public static ConnectivityMetadataRepository getConnectivityMetadataRepository() {
        Configuration.getInstance();
//        ConnectivityMetadataRepository connectivityMetadataRepository = new ConnectivityMetadataRepository("", getRepositoryCoordinator(), "", "",
//                getMetadataObjects(CONNECTIVITY_OBJECTS), getMetadataTables(CONNECTIVITY_TABLES));
//        connectivityMetadataRepository.createAllTables();
        return MetadataRepositoryConfiguration.getInstance().getConnectivityMetadataRepository();
    }

    private static List<MetadataTable> getMetadataTables(String tableDefinitions) {
        List<MetadataTable> metadataTables = new ArrayList<>();
        ObjectMapper objectMapper = new ObjectMapper();
        ClassLoader classLoader = RepositoryTestSetup.class.getClassLoader();
        File tablesFile = new File(classLoader.getResource(tableDefinitions).getFile());
        String objectsAbsolutePath = tablesFile.getAbsolutePath();
        DataObjectOperation dataObjectOperation = new DataObjectOperation(objectsAbsolutePath);
        dataObjectOperation.parseFile();
        //
//        for (DataObject dataObject : dataObjectOperation.getDataObjects()) {
//            if (dataObject.getType().equalsIgnoreCase("metadatatable")) {
//                MetadataTable metadataTable = objectMapper.convertValue(dataObject.getData(), MetadataTable.class);
//                metadataTable.setName(metadataTable.getName());
//                metadataTables.add(metadataTable);
//            }
//        }
        return metadataTables;
    }

    private static List<MetadataObject> getMetadataObjects(String objectDefinitions) {
        List<MetadataObject> metadataObjects = new ArrayList<>();
        ObjectMapper objectMapper = new ObjectMapper();
        ClassLoader classLoader = RepositoryTestSetup.class.getClassLoader();
        File objectsFile = new File(classLoader.getResource(objectDefinitions).getFile());
        String tablesAbsolutePath = objectsFile.getAbsolutePath();
        DataObjectOperation dataObjectOperation = new DataObjectOperation(tablesAbsolutePath);
        dataObjectOperation.parseFile();
        //
        for (DataObject dataObject : dataObjectOperation.getDataObjects()) {
            if (dataObject.getType().equalsIgnoreCase("metadataobject")) {
                metadataObjects.add(objectMapper.convertValue(dataObject.getData(), MetadataObject.class));
            }
        }
        return metadataObjects;
    }

    private static RepositoryCoordinator getRepositoryCoordinator() {
        Map<String, Database> databases = new HashMap<>();
        databases.put("owner", getDatabase());
        databases.put("writer", getDatabase());
        databases.put("reader", getDatabase());
        return new RepositoryCoordinator(databases);
    }

    private static Database getDatabase() {
        return new H2Database(new H2MemoryDatabaseConnection(DB_NAME, DB_USER, DB_PASSWORD, null, null));
    }

}
