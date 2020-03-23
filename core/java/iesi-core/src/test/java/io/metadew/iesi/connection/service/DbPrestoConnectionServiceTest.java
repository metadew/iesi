package io.metadew.iesi.connection.service;

import io.metadew.iesi.connection.database.PrestoDatabase;
import io.metadew.iesi.connection.database.connection.DatabaseConnectionHandlerImpl;
import io.metadew.iesi.connection.database.connection.presto.PrestoDatabaseConnection;
import io.metadew.iesi.connection.database.connection.teradata.TeradataDatabaseConnection;
import io.metadew.iesi.connection.operation.DbPrestoConnectionService;
import io.metadew.iesi.connection.operation.DbTeradataConnectionService;
import io.metadew.iesi.metadata.definition.connection.Connection;
import io.metadew.iesi.metadata.definition.connection.ConnectionParameter;
import io.metadew.iesi.metadata.definition.connection.key.ConnectionKey;
import io.metadew.iesi.metadata.definition.connection.key.ConnectionParameterKey;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.stream.Collectors;
import java.util.stream.Stream;

@RunWith(PowerMockRunner.class)
@PrepareForTest( { DatabaseConnectionHandlerImpl.class })
@PowerMockIgnore({"javax.management.*","javax.script.*"})
public class DbPrestoConnectionServiceTest   {
    @Mock
    private DatabaseConnectionHandlerImpl databaseConnectionHandler;

    @Before
    public void setup(){
        MockitoAnnotations.initMocks(this);
        PowerMockito.mockStatic(DatabaseConnectionHandlerImpl.class);
    }
    @Test
    public void getDatabaseTest (){
        Mockito.when(DatabaseConnectionHandlerImpl.getInstance()).thenReturn(databaseConnectionHandler);
//        PowerMockito.when(databaseConnectionHandler.getConnection(null)).thenReturn(null);

        Connection connection = new Connection(new ConnectionKey("value", "value"),
                "jdbc:presto://",
                "description",
                Stream.of(new ConnectionParameter(new ConnectionParameterKey("value", "value", "host"), "value"),
                        new ConnectionParameter(new ConnectionParameterKey("value", "value", "port"), "0"),
                        new ConnectionParameter(new ConnectionParameterKey("value", "value", "catalog"), "value"),
                        new ConnectionParameter(new ConnectionParameterKey("value", "value", "schema"), "value"),
                        new ConnectionParameter(new ConnectionParameterKey("value", "value", "user"), "value"),
                        new ConnectionParameter(new ConnectionParameterKey("value", "value", "password"), "value"))
                        .collect(Collectors.toList()));
        PrestoDatabase prestoDatabaseExpected = new PrestoDatabase(new PrestoDatabaseConnection("hostName",
                0,
                "catalogName",
                "schemaName",
                "userName",
                "userPassword"),"schema");
        Assert.assertEquals(prestoDatabaseExpected, DbPrestoConnectionService.getInstance().getDatabase(connection));
    }
}