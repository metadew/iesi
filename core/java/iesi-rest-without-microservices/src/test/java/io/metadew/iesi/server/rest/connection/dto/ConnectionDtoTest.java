package io.metadew.iesi.server.rest.connection.dto;

import io.metadew.iesi.metadata.definition.connection.Connection;
import io.metadew.iesi.metadata.definition.connection.ConnectionParameter;
import io.metadew.iesi.metadata.definition.connection.key.ConnectionKey;
import io.metadew.iesi.metadata.definition.connection.key.ConnectionParameterKey;
import io.metadew.iesi.metadata.definition.security.SecurityGroupKey;
import org.junit.jupiter.api.Test;

import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

class ConnectionDtoTest {

    private IConnectionDtoService connectionDtoService;
    public ConnectionDtoTest(IConnectionDtoService connectionDtoService) {
        this.connectionDtoService = connectionDtoService;
    }

    @Test
    void convertToEntityTest() {
        SecurityGroupKey securityGroupKey = new SecurityGroupKey(UUID.randomUUID());
        Connection connection = new Connection(new ConnectionKey("name", "tst"),
                securityGroupKey,
                "PUBLIC",
                "type",
                "description",
                Stream.of(
                        new ConnectionParameter(new ConnectionParameterKey(new ConnectionKey("name", "tst"), "name1"), "value1")
                )
                        .collect(Collectors.toList()));
        ConnectionDto connectionDto = new ConnectionDto(
                "name",
                "PUBLIC",
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
        assertThat(connectionDtoService.convertToEntity(connectionDto).get(0)).isEqualTo(connection);
    }

}