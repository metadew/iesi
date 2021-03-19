package io.metadew.iesi.openapi;

import io.metadew.iesi.metadata.definition.component.Component;
import io.metadew.iesi.metadata.definition.component.ComponentParameter;
import io.metadew.iesi.metadata.definition.component.ComponentVersion;
import io.metadew.iesi.metadata.definition.component.key.ComponentKey;
import io.metadew.iesi.metadata.definition.component.key.ComponentParameterKey;
import io.metadew.iesi.metadata.definition.component.key.ComponentVersionKey;
import io.metadew.iesi.metadata.tools.IdentifierTools;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;


class ComponentParserTest {

    private OpenAPI openAPI;
    private long componentVersion;

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
    }
  
    @Test
    void createComponent() {
        Component component;
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
                "http.request",
                "operationId",
                "a description",
                new ComponentVersion(new ComponentVersionKey(componentKey), "a description"),
                componentParameters,
                new ArrayList<>());

        assertThat(ComponentParser.getInstance().createComponent(componentVersion, connectionName, operation, "/pet", "POST")).isEqualTo(component);
    }

    @Test
    void createComponentWithNullDescription() {
        Component component;
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
                "http.request",
                "operationId",
                "",
                new ComponentVersion(new ComponentVersionKey(componentKey), ""),
                componentParameters,
                new ArrayList<>());

        assertThat(ComponentParser.getInstance().createComponent(componentVersion, connectionName, operation, "/pet", "POST")).isEqualTo(component);
    }

    @Test
    void createComponentWithPathParameter() {
        Component component;
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
                "http.request",
                "operationId",
                "a description",
                new ComponentVersion(new ComponentVersionKey(componentKey), "a description"),
                componentParameters,
                new ArrayList<>());

        assertThat(ComponentParser.getInstance().createComponent(componentVersion, connectionName, operation, "/pet/{id}", "POST")).isEqualTo(component);

    }
}
