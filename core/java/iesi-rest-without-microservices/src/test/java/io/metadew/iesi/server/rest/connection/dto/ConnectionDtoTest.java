package io.metadew.iesi.server.rest.connection.dto;

import io.metadew.iesi.metadata.configuration.security.SecurityGroupConfiguration;
import io.metadew.iesi.metadata.definition.connection.Connection;
import io.metadew.iesi.metadata.definition.connection.ConnectionParameter;
import io.metadew.iesi.metadata.definition.connection.key.ConnectionKey;
import io.metadew.iesi.metadata.definition.connection.key.ConnectionParameterKey;
import io.metadew.iesi.metadata.definition.security.SecurityGroup;
import io.metadew.iesi.metadata.definition.security.SecurityGroupKey;
import io.metadew.iesi.server.rest.Application;
import io.metadew.iesi.server.rest.configuration.TestConfiguration;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.HashSet;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = Application.class, properties = {"spring.main.allow-bean-definition-overriding=true"})
@ContextConfiguration(classes = TestConfiguration.class)
@ActiveProfiles("test")
@DirtiesContext
class ConnectionDtoTest {

    @Autowired
    private IConnectionDtoService connectionDtoService;
    @Autowired
    private SecurityGroupConfiguration securityGroupConfiguration;

    @Test
    void convertToEntityTest() {
        SecurityGroup securityGroup = SecurityGroupConfiguration.getInstance().getByName("PUBLIC")
                .orElseThrow(RuntimeException::new);
        Connection connection = new Connection(new ConnectionKey("name", "tst"),
                securityGroup.getMetadataKey(),
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