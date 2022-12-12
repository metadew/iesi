package io.metadew.iesi.connection.database.tools;

import io.metadew.iesi.TestConfiguration;
import io.metadew.iesi.common.configuration.framework.FrameworkConfiguration;
import io.metadew.iesi.connection.database.Database;
import io.metadew.iesi.connection.database.DatabaseHandler;
import io.metadew.iesi.connection.tools.sql.SqlResultService;
import io.metadew.iesi.datatypes.DataTypeHandler;
import io.metadew.iesi.datatypes.dataset.Dataset;
import io.metadew.iesi.datatypes.dataset.DatasetConfiguration;
import io.metadew.iesi.datatypes.dataset.DatasetKey;
import io.metadew.iesi.datatypes.dataset.implementation.DatasetImplementation;
import io.metadew.iesi.datatypes.dataset.implementation.DatasetImplementationConfiguration;
import io.metadew.iesi.datatypes.dataset.implementation.DatasetImplementationKey;
import io.metadew.iesi.datatypes.dataset.implementation.database.DatabaseDatasetImplementation;
import io.metadew.iesi.datatypes.dataset.implementation.database.DatabaseDatasetImplementationKeyValueConfiguration;
import io.metadew.iesi.datatypes.dataset.implementation.label.DatasetImplementationLabel;
import io.metadew.iesi.datatypes.dataset.implementation.label.DatasetImplementationLabelKey;
import io.metadew.iesi.metadata.configuration.connection.ConnectionParameterConfiguration;
import io.metadew.iesi.metadata.configuration.script.result.ScriptResultOutputConfiguration;
import io.metadew.iesi.metadata.definition.action.key.ActionKey;
import io.metadew.iesi.metadata.definition.connection.Connection;
import io.metadew.iesi.metadata.definition.connection.ConnectionParameter;
import io.metadew.iesi.metadata.definition.connection.key.ConnectionKey;
import io.metadew.iesi.metadata.definition.connection.key.ConnectionParameterKey;
import io.metadew.iesi.metadata.definition.security.SecurityGroupKey;
import io.metadew.iesi.script.execution.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;

import javax.sql.rowset.CachedRowSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@SpringBootTest(classes = { SqlResultService.class, FrameworkConfiguration.class, DataTypeHandler.class, ScriptResultOutputConfiguration.class, ConnectionParameterConfiguration.class, DataTypeHandler.class,
        DatasetConfiguration.class, DatasetImplementationConfiguration.class, DatabaseDatasetImplementationKeyValueConfiguration.class })
@ContextConfiguration(classes = TestConfiguration.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@ActiveProfiles("test")
public class SqlResultServiceTest {
    ExecutionControl executionControl;
    ExecutionRuntime executionRuntime;
    ScriptExecution scriptExecution;
    ActionExecution actionExecution;
    ActionKey actionKey;
    ActionControl actionControl;

    Database database;

    @Autowired
    DatabaseHandler databaseHandler;

    @Autowired
    SqlResultService sqlResultService;

    @Autowired
    DatasetConfiguration datasetConfiguration;

    @BeforeEach
    void prepare() {
        executionControl = mock(ExecutionControl.class);
        scriptExecution = mock(ScriptExecution.class);
        actionExecution = mock(ActionExecution.class);
        actionControl = mock(ActionControl.class);
        executionRuntime = new ExecutionRuntime(executionControl, UUID.randomUUID().toString(), null);

        ConnectionKey connectionKey = new ConnectionKey("connection", "tst");
        Connection connection = new Connection(
                "connection",
                new SecurityGroupKey(UUID.randomUUID()),
                "PUBLIC",
                "db.sqlite",
                "description",
                "tst",
                Stream.of(new ConnectionParameter(
                        new ConnectionParameterKey(connectionKey, "filePath"),
                        "/home/hkhattabi/metadew/iesi/core/java/iesi-core"
                ), new ConnectionParameter(
                        new ConnectionParameterKey(connectionKey, "fileName"),
                        "repository.db3"
                )).collect(Collectors.toList())
        );
        database = databaseHandler.getDatabase(connection);


        when(executionControl.getExecutionRuntime())
                .thenReturn(executionRuntime);
        when(actionExecution.getActionControl())
                .thenReturn(actionControl);


    }

    @Test
    void SQLDataTransfer() throws SQLException {
        String query = "SELECT * FROM IESI_DES_SCRIPT;";
        DatasetKey datasetKey = new DatasetKey(UUID.randomUUID());
        DatasetImplementationKey datasetImplementationKey = new DatasetImplementationKey(UUID.randomUUID());

        DatasetImplementation datasetImplementation = new DatabaseDatasetImplementation(
                datasetImplementationKey,
                datasetKey,
                "dataset",
                Stream.of(new DatasetImplementationLabel(
                        new DatasetImplementationLabelKey(UUID.randomUUID()),
                        datasetImplementationKey,
                        "database"
                ), new DatasetImplementationLabel(
                        new DatasetImplementationLabelKey(UUID.randomUUID()),
                        datasetImplementationKey,
                        "result"
                )).collect(Collectors.toSet()),
                new HashSet<>()

        );
        Dataset dataset = new Dataset(
                datasetKey,
                new SecurityGroupKey(UUID.randomUUID()),
                "PUBLIC",
                "dataset",
                Stream.of(datasetImplementation).collect(Collectors.toSet()));


        datasetConfiguration.insert(dataset);
        CachedRowSet cachedRowSet = databaseHandler.executeQuery(database, query);
        sqlResultService.convert(cachedRowSet);
    }
}
