package io.metadew.iesi.connection.service;

import io.metadew.iesi.connection.database.TeradataDatabase;
import io.metadew.iesi.connection.database.connection.DatabaseConnectionHandlerImpl;
import io.metadew.iesi.connection.database.connection.teradata.TeradataDatabaseConnection;
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
import org.mockito.MockitoAnnotations;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.mockito.Mockito.mock;
import static org.powermock.api.mockito.PowerMockito.mockStatic;

@RunWith(PowerMockRunner.class)
@PrepareForTest( { DatabaseConnectionHandlerImpl.class })
@PowerMockIgnore({"javax.management.*","javax.script.*"})
public class DbTeradataConnectionServiceTest   {
    @Mock
    private DatabaseConnectionHandlerImpl databaseConnectionHandler;

    @Before
    public void setup(){
        MockitoAnnotations.initMocks(this);
    }
    @Test
    public void getDatabaseTest (){
        mockStatic(DatabaseConnectionHandlerImpl.class);
        DatabaseConnectionHandlerImpl mock = mock(DatabaseConnectionHandlerImpl.class);
        PowerMockito.when(DatabaseConnectionHandlerImpl.getInstance()).thenReturn(mock);

        Connection connection = new Connection(new ConnectionKey("value", "value"),
                "jdbc:teradata://",
                "description",
                Stream.of(new ConnectionParameter(new ConnectionParameterKey("value", "value", "host"), "value"),
                        new ConnectionParameter(new ConnectionParameterKey("value", "value", "port"), "0"),
                        new ConnectionParameter(new ConnectionParameterKey("value", "value", "database"), "value"),
                        new ConnectionParameter(new ConnectionParameterKey("value", "value", "user"), "value"),
                        new ConnectionParameter(new ConnectionParameterKey("value", "value", "password"), "value"))
                        .collect(Collectors.toList()));
     TeradataDatabase teradataDatabaseExpected = new TeradataDatabase(new TeradataDatabaseConnection("value", 0, "value", "value", "value"));
        Assert.assertEquals(teradataDatabaseExpected, DbTeradataConnectionService.getInstance().getDatabase(connection));
    }
}
