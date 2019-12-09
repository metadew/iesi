package io.metadew.iesi.metadata.configuration;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.metadew.iesi.connection.database.Database;
import io.metadew.iesi.connection.database.H2Database;
import io.metadew.iesi.connection.database.connection.h2.H2MemoryDatabaseConnection;
import io.metadew.iesi.framework.configuration.FrameworkFolderConfiguration;
import io.metadew.iesi.framework.definition.FrameworkFolder;
import io.metadew.iesi.metadata.configuration.script.ScriptConfiguration;
import io.metadew.iesi.metadata.definition.DataObject;
import io.metadew.iesi.metadata.definition.MetadataObject;
import io.metadew.iesi.metadata.definition.MetadataTable;
import io.metadew.iesi.metadata.execution.MetadataControl;
import io.metadew.iesi.metadata.operation.DataObjectOperation;
import io.metadew.iesi.metadata.repository.*;
import io.metadew.iesi.metadata.repository.coordinator.RepositoryCoordinator;
import static org.junit.Assert.assertFalse;



import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.sql.*;
import java.util.*;

public class ScriptConfigurationTest {
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

    ConnectivityMetadataRepository connectivityMetadataRepository = new ConnectivityMetadataRepository(
            "design",
            "instanceName", repositoryCoordinator
    );
    ControlMetadataRepository controlMetadataRepository = new ControlMetadataRepository(
            "design",
            "instanceName", repositoryCoordinator
    );
    TraceMetadataRepository traceMetadataRepository = new TraceMetadataRepository(
            "design",
            "instanceName", repositoryCoordinator
    );
    ResultMetadataRepository resultMetadataRepository = new ResultMetadataRepository(
            "design",
            "instanceName", repositoryCoordinator
    );
    CatalogMetadataRepository catalogMetadataRepository = new CatalogMetadataRepository(
            "design",
            "instanceName", repositoryCoordinator
    );

    List<MetadataRepository> repositoriesList = Arrays.asList(connectivityMetadataRepository, controlMetadataRepository,
            traceMetadataRepository, resultMetadataRepository,
            catalogMetadataRepository);
    List<MetadataRepository> repositories = new ArrayList<>(repositoriesList);




    @Before
    public void setup() throws SQLException {

        databases.put("reader", database);
        databases.put("owner", database);
        databases.put("writer", database);
        RepositoryCoordinator repositoryCoordinator = new RepositoryCoordinator(databases);
        scriptConfiguration = new ScriptConfiguration();


        DataObjectOperation dataObjectOperation = new DataObjectOperation(
                "C:\\Users\\thomas.vandendijk\\Documents\\belfius\\iesi_code\\iesi\\core\\metadata\\def\\DesignObjects.json");
        dataObjectOperation.parseFile();
        ObjectMapper objectMapper = new ObjectMapper();

        for (DataObject dataObject : dataObjectOperation.getDataObjects()) {
            if (dataObject.getType().equalsIgnoreCase("metadataobject")) {
                metadataObjects.add(objectMapper.convertValue(dataObject.getData(), MetadataObject.class));
            }
        }

        dataObjectOperation = new DataObjectOperation("C:\\Users\\thomas.vandendijk\\Documents\\belfius\\iesi_code\\iesi\\core\\metadata\\def\\DesignTables.json");
        dataObjectOperation.parseFile();
        //
        for (DataObject dataObject : dataObjectOperation.getDataObjects()) {
            if (dataObject.getType().equalsIgnoreCase("metadatatable")) {
                MetadataTable metadataTable = objectMapper.convertValue(dataObject.getData(), MetadataTable.class);
                metadataTable.setName("" + metadataTable.getName());
                metadataTables.add(metadataTable);
            }
        }

        DesignMetadataRepository designMetadataRepository = new DesignMetadataRepository("design",
                "test", repositoryCoordinator);
        designMetadataRepository.setMetadataObjects(metadataObjects);
        designMetadataRepository.setMetadataTables(metadataTables);
        designMetadataRepository.createAllTables();

        repositories.add(designMetadataRepository);

        MetadataControl metadataControl = MetadataControl.getInstance();
        metadataControl.init(repositories);

        scriptConfiguration.setMetadataRepository(designMetadataRepository);
    }

    @Test
    public void scriptNotExists(){
        assertFalse(scriptConfiguration.exists("testScript", 1));
    }
}
