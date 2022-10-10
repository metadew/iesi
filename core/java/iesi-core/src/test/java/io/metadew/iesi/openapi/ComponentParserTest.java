package io.metadew.iesi.openapi;

import io.metadew.iesi.TestConfiguration;
import io.metadew.iesi.metadata.configuration.security.SecurityGroupConfiguration;
import io.metadew.iesi.metadata.definition.component.Component;
import io.metadew.iesi.metadata.definition.component.ComponentParameter;
import io.metadew.iesi.metadata.definition.component.ComponentVersion;
import io.metadew.iesi.metadata.definition.component.key.ComponentKey;
import io.metadew.iesi.metadata.definition.component.key.ComponentParameterKey;
import io.metadew.iesi.metadata.definition.component.key.ComponentVersionKey;
import io.metadew.iesi.metadata.definition.security.SecurityGroup;
import io.metadew.iesi.metadata.definition.security.SecurityGroupKey;
import io.metadew.iesi.metadata.service.security.SecurityGroupService;
import io.metadew.iesi.metadata.tools.IdentifierTools;
import io.swagger.v3.oas.models.*;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {TestConfiguration.class, ComponentParser.class})
@ActiveProfiles("test")
class ComponentParserTest {

    private OpenAPI openAPI;
    private long componentVersion;

    @Autowired
    ComponentParser componentParser;

    @MockBean
    SecurityGroupService securityGroupService;

    @BeforeEach
    void setup() {
        SecurityScheme oAuthScheme = new SecurityScheme()
                .type(SecurityScheme.Type.OAUTH2);
        SecurityScheme apiKeyScheme = new SecurityScheme()
                .type(SecurityScheme.Type.APIKEY);
        Info info = new Info()
                .version("1")
                .title("Swagger Petstore - OpenAPI 3.0");
        Map<String, SecurityScheme> securitySchemeMap = new HashMap<>();

        securitySchemeMap.put("petstore_auth", oAuthScheme);
        securitySchemeMap.put("api_key", apiKeyScheme);

        this.openAPI = new OpenAPI()
                .info(info)
                .components(new Components()
                        .securitySchemes(securitySchemeMap));
        this.componentVersion = Long.parseLong(openAPI.getInfo().getVersion());

        when(securityGroupService.get("PUBLIC"))
                .thenReturn(Optional.of(new SecurityGroup(
                        new SecurityGroupKey(UUID.randomUUID()),
                        "PUBLIC",
                        new HashSet<>(),
                        new HashSet<>()
                )));
    }

    @Test
    void createComponent() {
        Component component;
        SecurityGroupKey securityGroupKey = new SecurityGroupKey(UUID.randomUUID());
        List<ComponentParameter> componentParameters = new ArrayList<>();
        String connectionName = "Swagger Petstore - OpenAPI 3.0";
        Operation operation = new Operation()
                .description("a description")
                .operationId("operationId");
        ComponentKey componentKey = new ComponentKey(
                IdentifierTools.getComponentIdentifier(operation.getOperationId()),
                componentVersion);

        componentParameters.add(new ComponentParameter(new ComponentParameterKey(componentKey, "endpoint"), "/pet"));
        componentParameters.add(new ComponentParameter(new ComponentParameterKey(componentKey, "type"), "POST"));
        componentParameters.add(new ComponentParameter(new ComponentParameterKey(componentKey, "connection"), connectionName));

        component = new Component(componentKey,
                securityGroupKey,
                "PUBLIC",
                "http.request",
                "operationId",
                "a description",
                new ComponentVersion(new ComponentVersionKey(componentKey), "a description"),
                componentParameters,
                new ArrayList<>());

        assertThat(componentParser.createComponent(securityGroupKey, componentVersion, connectionName, operation, "/pet", "POST")).isEqualTo(component);
    }

    @Test
    void createComponentWithNullDescription() {
        Component component;
        SecurityGroupKey securityGroupKey = new SecurityGroupKey(UUID.randomUUID());
        List<ComponentParameter> componentParameters = new ArrayList<>();
        String connectionName = "Swagger Petstore - OpenAPI 3.0";
        Operation operation = new Operation()
                .description(null)
                .operationId("operationId");

        ComponentKey componentKey = new ComponentKey(
                IdentifierTools.getComponentIdentifier(operation.getOperationId()),
                componentVersion);

        componentParameters.add(new ComponentParameter(new ComponentParameterKey(componentKey, "endpoint"), "/pet"));
        componentParameters.add(new ComponentParameter(new ComponentParameterKey(componentKey, "type"), "POST"));
        componentParameters.add(new ComponentParameter(new ComponentParameterKey(componentKey, "connection"), connectionName));

        component = new Component(componentKey,
                securityGroupKey,
                "PUBLIC",
                "http.request",
                "operationId",
                "",
                new ComponentVersion(new ComponentVersionKey(componentKey), ""),
                componentParameters,
                new ArrayList<>());

        assertThat(componentParser.createComponent(securityGroupKey, componentVersion, connectionName, operation, "/pet", "POST")).isEqualTo(component);
    }

    @Test
    void createComponentWithPathParameter() {
        Component component;
        SecurityGroupKey securityGroupKey = new SecurityGroupKey(UUID.randomUUID());
        List<ComponentParameter> componentParameters = new ArrayList<>();
        String connectionName = "Swagger Petstore - OpenAPI 3.0";
        Operation operation = new Operation()
                .description("a description")
                .operationId("operationId");
        ComponentKey componentKey = new ComponentKey(
                IdentifierTools.getComponentIdentifier(operation.getOperationId()),
                componentVersion);

        componentParameters.add(new ComponentParameter(new ComponentParameterKey(componentKey, "endpoint"), "/pet/#id#"));
        componentParameters.add(new ComponentParameter(new ComponentParameterKey(componentKey, "type"), "POST"));
        componentParameters.add(new ComponentParameter(new ComponentParameterKey(componentKey, "connection"), connectionName));

        component = new Component(componentKey,
                securityGroupKey,
                "PUBLIC",
                "http.request",
                "operationId",
                "a description",
                new ComponentVersion(new ComponentVersionKey(componentKey), "a description"),
                componentParameters,
                new ArrayList<>());

        assertThat(componentParser.createComponent(securityGroupKey, componentVersion, connectionName, operation, "/pet/{id}", "POST")).isEqualTo(component);

    }

    @Test
    void createComponentWithStringVersion() {
        List<String> messages = Collections.singletonList("The version should be a number");
        openAPI.getInfo().setVersion("SNAPSHOT-1.1");

        SwaggerParserException exception = assertThrows(SwaggerParserException.class, () -> componentParser.parse(openAPI));
        assertThat(exception.getMessages()).isEqualTo(messages);
    }

    @Test
    void createComponentWithLongVersion() {
        Paths paths = new Paths();
        PathItem pathItem = new PathItem();
        pathItem.setGet(new Operation()
                .description(null)
                .operationId("operationId"));
        paths.addPathItem("name", pathItem);

        openAPI.setPaths(paths);
        openAPI.getInfo().setVersion("1");

        List<Component> components = componentParser.parse(openAPI);
        assertThat(components.get(0).getVersion().getMetadataKey().getComponentKey().getVersionNumber()).isEqualTo(1);
    }

    @Test
    void createComponentWithSemanticVersion() {
        Paths paths = new Paths();
        PathItem pathItem = new PathItem();
        pathItem.setGet(new Operation()
                .description(null)
                .operationId("operationId"));
        paths.addPathItem("name", pathItem);

        openAPI.setPaths(paths);
        openAPI.getInfo().setVersion("11.2.3");

        List<Component> components = componentParser.parse(openAPI);
        assertThat(components.get(0).getVersion().getMetadataKey().getComponentKey().getVersionNumber()).isEqualTo(11);
    }

    @Test
    void testComponentWithSemanticVersion() {
        Paths paths = new Paths();
        PathItem pathItem = new PathItem();
        pathItem.setGet(new Operation()
                .description(null)
                .operationId("operationId"));
        paths.addPathItem("name", pathItem);

        openAPI.setPaths(paths);
        openAPI.getInfo().setVersion("1.2.3");

        List<Component> components = componentParser.parse(openAPI);
        assertThat(components.get(0).getVersion().getMetadataKey().getComponentKey().getVersionNumber()).isNotEqualTo(1.2);
    }
}
