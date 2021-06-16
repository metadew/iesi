package io.metadew.iesi.server.rest.connection.dto;

import io.metadew.iesi.metadata.definition.connection.Connection;
import io.metadew.iesi.metadata.definition.connection.ConnectionParameter;
import io.metadew.iesi.metadata.definition.connection.key.ConnectionKey;
import io.metadew.iesi.metadata.definition.connection.key.ConnectionParameterKey;
import org.junit.jupiter.api.Test;

import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

class ConnectionDtoTest {

    @Test
    void convertToEntityTest() {
        Connection connection = new Connection(new ConnectionKey("name", "tst"),
                "type",
                "description",
                Stream.of(
                        new ConnectionParameter(new ConnectionParameterKey(new ConnectionKey("name", "tst"), "name1"), "value1")
                )
                        .collect(Collectors.toList()));
        ConnectionDto connectionDto = new ConnectionDto(
                "name",
                "type",
                "description",
                Stream.of(
                        new ConnectionEnvironmentDto(
                                "tst",
                                Stream.of(new ConnectionParameterDto("name1", "value1"))
                                        .collect(Collectors.toSet())
                        )
                ).collect(Collectors.toSet())
        );
        assertThat(connectionDto.convertToEntity().get(0)).isEqualTo(connection);
    }

}