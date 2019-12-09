package io.metadew.iesi.metadata.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.metadew.iesi.connection.database.Database;
import io.metadew.iesi.connection.database.H2Database;
import io.metadew.iesi.connection.database.connection.h2.H2MemoryDatabaseConnection;
import io.metadew.iesi.metadata.configuration.script.ScriptConfiguration;
import io.metadew.iesi.metadata.definition.DataObject;
import io.metadew.iesi.metadata.definition.MetadataObject;
import io.metadew.iesi.metadata.definition.MetadataTable;
import io.metadew.iesi.metadata.execution.MetadataControl;
import io.metadew.iesi.metadata.operation.DataObjectOperation;
import io.metadew.iesi.metadata.repository.*;
import io.metadew.iesi.metadata.repository.coordinator.RepositoryCoordinator;

import java.io.File;
import java.util.*;

public class ConfigurationTestSetup {

    RepositoryCoordinator repositoryCoordinator;

    private static final String DB_DRIVER = "org.h2.Driver";
    private static final String DB_CONNECTION = "jdbc:h2:mem:test;DB_CLOSE_DELAY=-1";
    private static final String DB_USER = "";
    private static final String DB_PASSWORD = "";

    H2MemoryDatabaseConnection memoryDatabaseConnection =
            new H2MemoryDatabaseConnection("test", "", "");
    H2Database database = new H2Database(memoryDatabaseConnection);
    Map<String, Database> databases = new HashMap<>();
    ScriptConfiguration scriptConfiguration;
    List<MetadataObject> metadataObjects = new ArrayList<>();
    List<MetadataTable> metadataTables = new ArrayList<>();
    RepositoryCoordinator repositoryCoord = new RepositoryCoordinator(databases);

    DesignMetadataRepository designMetadataRepository = new DesignMetadataRepository(
            "design",
            "instanceName", repositoryCoord
    );

    ConnectivityMetadataRepository connectivityMetadataRepository = new ConnectivityMetadataRepository(
            "connect",
            "instanceName", repositoryCoord
    );
    ControlMetadataRepository controlMetadataRepository = new ControlMetadataRepository(
            "control",
            "instanceName", repositoryCoord
    );
    TraceMetadataRepository traceMetadataRepository = new TraceMetadataRepository(
            "trace",
            "instanceName", repositoryCoord
    );
    ResultMetadataRepository resultMetadataRepository = new ResultMetadataRepository(
            "result",
            "instanceName", repositoryCoord
    );
    CatalogMetadataRepository catalogMetadataRepository = new CatalogMetadataRepository(
            "catalog",
            "instanceName", repositoryCoord
    );

    List<MetadataRepository> repositoriesList = Arrays.asList(connectivityMetadataRepository, controlMetadataRepository,
            traceMetadataRepository, resultMetadataRepository,
            catalogMetadataRepository, designMetadataRepository);
    List<MetadataRepository> repositories = new ArrayList<>(repositoriesList);

    public void executeSetup(String dataObjectFilename, String metatableFilename){
        databases.put("reader", database);
        databases.put("owner", database);
        databases.put("writer", database);

        DataObjectOperation dataObjectOperation = new DataObjectOperation(
                "../../metadata/def/" + dataObjectFilename);
        dataObjectOperation.parseFile();
        ObjectMapper objectMapper = new ObjectMapper();

        for (DataObject dataObject : dataObjectOperation.getDataObjects()) {
            if (dataObject.getType().equalsIgnoreCase("metadataobject")) {
                metadataObjects.add(objectMapper.convertValue(dataObject.getData(), MetadataObject.class));
            }
        }

        dataObjectOperation = new DataObjectOperation("../../metadata/def/" + metatableFilename);
        dataObjectOperation.parseFile();
        //
        for (DataObject dataObject : dataObjectOperation.getDataObjects()) {
            if (dataObject.getType().equalsIgnoreCase("metadatatable")) {
                MetadataTable metadataTable = objectMapper.convertValue(dataObject.getData(), MetadataTable.class);
                metadataTable.setName("" + metadataTable.getName());
                metadataTables.add(metadataTable);
            }
        }

        MetadataControl metadataControl = MetadataControl.getInstance();
        metadataControl.init(this.getRepositories());
    }

    public RepositoryCoordinator getRepositoryCoordinator() {
        return repositoryCoord;
    }

    public Map<String, Database> getDatabases() {
        return databases;
    }

    public List<MetadataObject> getMetadataObjects() {
        return metadataObjects;
    }

    public List<MetadataTable> getMetadataTables() {
        return metadataTables;
    }

    public List<MetadataRepository> getRepositories() {
        return repositories;
    }

    public ConnectivityMetadataRepository getConnectivityMetadataRepository() {
        return connectivityMetadataRepository;
    }

    public ControlMetadataRepository getControlMetadataRepository() {
        return controlMetadataRepository;
    }

    public TraceMetadataRepository getTraceMetadataRepository() {
        return traceMetadataRepository;
    }

    public ResultMetadataRepository getResultMetadataRepository() {
        return resultMetadataRepository;
    }

    public CatalogMetadataRepository getCatalogMetadataRepository() {
        return catalogMetadataRepository;
    }

    public DesignMetadataRepository getDesignMetadataRepository() {
        return designMetadataRepository;
    }
}
