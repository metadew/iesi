package io.metadew.iesi.server.rest.environment.dto;

import io.metadew.iesi.metadata.definition.connection.Connection;
import io.metadew.iesi.metadata.definition.connection.ConnectionParameter;
import io.metadew.iesi.metadata.definition.connection.key.ConnectionKey;
import io.metadew.iesi.metadata.definition.connection.key.ConnectionParameterKey;
import io.metadew.iesi.metadata.definition.environment.Environment;
import io.metadew.iesi.server.rest.resource.connection.dto.ConnectionDto;
import io.metadew.iesi.server.rest.resource.connection.dto.ConnectionParameterDto;
import org.junit.Test;

import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class EnvironmentDtoTest {

    @Test
    public void convertToEntityTest() {
        Environment connection = new Environment(new En
                Key("name", "tst"),
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