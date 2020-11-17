package io.metadew.iesi.server.rest.connection.dto;

import io.metadew.iesi.metadata.definition.connection.Connection;
import io.metadew.iesi.metadata.definition.connection.ConnectionParameter;
import io.metadew.iesi.metadata.definition.connection.key.ConnectionKey;
import io.metadew.iesi.metadata.definition.connection.key.ConnectionParameterKey;
import org.junit.jupiter.api.Test;

import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.Assert.assertEquals;

class ConnectionDtoTest {

    @Test
    void convertToEntityTest() {
        Connection connection = new Connection(new ConnectionKey("name", "tst"),
                "type",
                "description",
                Stream.of(new ConnectionParameter(new ConnectionParameterKey(new ConnectionKey("name", "tst"), "name1"), "value1"),
                        new ConnectionParameter(new ConnectionParameterKey(new ConnectionKey("name", "tst"), "name2"), "value2"))
                        .collect(Collectors.toList()));
        ConnectionDto connectionDto = new ConnectionDto("name", "type","description", "tst",
                Stream.of(new ConnectionParameterDto( "name1", "value1"),
                        new ConnectionParameterDto("name2", "value2"))
                        .collect(Collectors.toList()));
        assertEquals(connection, connectionDto.convertToEntity());
    }

}