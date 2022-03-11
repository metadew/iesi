package io.metadew.iesi.server.rest.component.dto;

import io.metadew.iesi.metadata.configuration.security.SecurityGroupConfiguration;
import io.metadew.iesi.metadata.definition.component.Component;
import io.metadew.iesi.metadata.definition.component.ComponentAttribute;
import io.metadew.iesi.metadata.definition.component.ComponentParameter;
import io.metadew.iesi.metadata.definition.component.ComponentVersion;
import io.metadew.iesi.metadata.definition.component.key.ComponentAttributeKey;
import io.metadew.iesi.metadata.definition.component.key.ComponentKey;
import io.metadew.iesi.metadata.definition.component.key.ComponentParameterKey;
import io.metadew.iesi.metadata.definition.component.key.ComponentVersionKey;
import io.metadew.iesi.metadata.definition.environment.key.EnvironmentKey;
import io.metadew.iesi.metadata.definition.security.SecurityGroup;
import io.metadew.iesi.metadata.tools.IdentifierTools;
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

import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = Application.class, properties = {"spring.main.allow-bean-definition-overriding=true"})
@ContextConfiguration(classes = TestConfiguration.class)
@ActiveProfiles("test")
@DirtiesContext
class ComponentDtoTest {

    @Autowired
    private IComponentDtoService componentDtoService;
    @Autowired
    private SecurityGroupConfiguration securityGroupConfiguration;

    @Test
    void convertToEntityTest() {
        SecurityGroup securityGroup = SecurityGroupConfiguration.getInstance().getByName("PUBLIC")
                .orElseThrow(RuntimeException::new);

        Component component = new Component(new ComponentKey(IdentifierTools.getComponentIdentifier("name"), 1L),
                securityGroup.getMetadataKey(),
                securityGroup.getName(),
                "type",
                "name",
                "description",
                new ComponentVersion(new ComponentVersionKey(IdentifierTools.getComponentIdentifier("name"), 1L), "descriptions"),
                Stream.of(new ComponentParameter(new ComponentParameterKey(new ComponentKey(IdentifierTools.getComponentIdentifier("name"), 1L), "name1"), "value1"))
                        .collect(Collectors.toList()),
                Stream.of(new ComponentAttribute(new ComponentAttributeKey(new ComponentKey(IdentifierTools.getComponentIdentifier("name"), 1L), new EnvironmentKey("tst"), "name1"), "value1"),
                                new ComponentAttribute(new ComponentAttributeKey(new ComponentKey(IdentifierTools.getComponentIdentifier("name"), 1L), new EnvironmentKey("tst"), "name2"), "value2"))
                        .collect(Collectors.toList()));


        ComponentDto componentDto = new ComponentDto("type", securityGroup.getName(), "name", "description",
                new ComponentVersionDto(1L, "descriptions"),
                Stream.of(new ComponentParameterDto("name1", "value1"))
                        .collect(Collectors.toSet()),
                Stream.of(new ComponentAttributeDto("tst", "name1", "value1"),
                                new ComponentAttributeDto("tst", "name2", "value2"))
                        .collect(Collectors.toSet()));
        assertThat(componentDtoService.convertToEntity(componentDto)).isEqualTo(component);
    }
}