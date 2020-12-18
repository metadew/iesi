package io.metadew.iesi.server.rest.component.dto;

import io.metadew.iesi.metadata.definition.component.Component;
import io.metadew.iesi.metadata.definition.component.ComponentAttribute;
import io.metadew.iesi.metadata.definition.component.ComponentParameter;
import io.metadew.iesi.metadata.definition.component.ComponentVersion;
import io.metadew.iesi.metadata.definition.component.key.ComponentAttributeKey;
import io.metadew.iesi.metadata.definition.component.key.ComponentKey;
import io.metadew.iesi.metadata.definition.component.key.ComponentParameterKey;
import io.metadew.iesi.metadata.definition.component.key.ComponentVersionKey;
import io.metadew.iesi.metadata.definition.environment.key.EnvironmentKey;
import io.metadew.iesi.metadata.tools.IdentifierTools;
import org.junit.jupiter.api.Test;

import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.Assert.assertEquals;

class ComponentDtoTest {

    @Test
    void convertToEntityTest() {
        Component component = new Component(new ComponentKey(IdentifierTools.getComponentIdentifier("name"), 1L),
                "type",
                "name",
                "description",
                new ComponentVersion(new ComponentVersionKey(IdentifierTools.getComponentIdentifier("name"), 1L), "descriptions"),
                Stream.of(new ComponentParameter(new ComponentParameterKey(new ComponentKey(IdentifierTools.getComponentIdentifier("name"), 1L), "name1"), "value1"),
                        new ComponentParameter(new ComponentParameterKey(new ComponentKey(IdentifierTools.getComponentIdentifier("name"), 1L), "name1"), "value1"))
                        .collect(Collectors.toList()),
                Stream.of(new ComponentAttribute(new ComponentAttributeKey(new ComponentKey(IdentifierTools.getComponentIdentifier("name"), 1L), new EnvironmentKey("tst"), "name1"), "value1"),
                        new ComponentAttribute(new ComponentAttributeKey(new ComponentKey(IdentifierTools.getComponentIdentifier("name"), 1L), new EnvironmentKey("tst"), "name2"), "value2"))
                        .collect(Collectors.toList()));
        ComponentDto componentDto = new ComponentDto("type", "name", "description",
                new ComponentVersionDto(1L, "descriptions"),
                Stream.of(new ComponentParameterDto( "name1", "value1"),
                        new ComponentParameterDto("name1", "value1"))
                        .collect(Collectors.toList()),
                Stream.of(new ComponentAttributeDto("tst", "name1", "value1"),
                        new ComponentAttributeDto("tst", "name2", "value2"))
                        .collect(Collectors.toList()));
        assertEquals(component, componentDto.convertToEntity());
    }

}