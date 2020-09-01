package io.metadew.iesi.server.rest.environment.dto;

import io.metadew.iesi.metadata.definition.environment.Environment;
import io.metadew.iesi.metadata.definition.environment.EnvironmentParameter;
import io.metadew.iesi.metadata.definition.environment.key.EnvironmentKey;
import io.metadew.iesi.metadata.definition.environment.key.EnvironmentParameterKey;
import org.junit.Test;

import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.Assert.assertEquals;

public class EnvironmentDtoTest {

    @Test
    public void convertToEntityTest() {
        Environment environment = new Environment(new EnvironmentKey("tst"),
                "description",
                Stream.of(new EnvironmentParameter(new EnvironmentParameterKey(new EnvironmentKey("tst"), "name1"), "value1"),
                        new EnvironmentParameter(new EnvironmentParameterKey(new EnvironmentKey("tst"), "name2"), "value2"))
                        .collect(Collectors.toList()));
        EnvironmentDto environmentDto = new EnvironmentDto("tst","description",
                Stream.of(new EnvironmentParameterDto( "name1", "value1"),
                        new EnvironmentParameterDto("name2", "value2"))
                        .collect(Collectors.toList()));
        assertEquals(environment, environmentDto.convertToEntity());
    }

}