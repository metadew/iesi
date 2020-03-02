package io.metadew.iesi.connection.service;

import com.sun.media.sound.StandardMidiFileReader;
import io.metadew.iesi.connection.database.TeradataDatabase;
import io.metadew.iesi.connection.database.connection.TeradataDatabaseConnection;
import io.metadew.iesi.connection.operation.DbTeradataConnectionService;
import io.metadew.iesi.metadata.definition.connection.Connection;
import io.metadew.iesi.metadata.definition.connection.ConnectionParameter;
import io.metadew.iesi.metadata.definition.connection.key.ConnectionKey;
import io.metadew.iesi.metadata.definition.connection.key.ConnectionParameterKey;
import org.junit.jupiter.api.Test;

import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.Assert.assertEquals;

class DbTeradataConnectionServiceTest {

    @Test
    void getDatabaseTest() {
        // TODO: Mock get Connection in Database Connection
        Connection connection = new Connection(new ConnectionKey("test", "tst"),
                "db.teradata",
                "description",
                Stream.of(new ConnectionParameter(new ConnectionParameterKey("test", "tst", "host"), "value"),
                        new ConnectionParameter(new ConnectionParameterKey("test", "tst", "database"), "value"),
                        new ConnectionParameter(new ConnectionParameterKey("test", "tst", "user"), "value"),
                        new ConnectionParameter(new ConnectionParameterKey("test", "tst", "password"), "value"))
                        .collect(Collectors.toList()));

        TeradataDatabase teradataDatabaseExpected = new TeradataDatabase(new TeradataDatabaseConnection("value", 0, "value", "value", "value"));
        assertEquals(teradataDatabaseExpected, DbTeradataConnectionService.getInstance().getDatabase(connection));
    }

}
