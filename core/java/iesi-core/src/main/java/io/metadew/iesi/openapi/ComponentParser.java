package io.metadew.iesi.openapi;

import io.metadew.iesi.metadata.definition.Metadata;
import io.metadew.iesi.metadata.definition.component.Component;
import io.metadew.iesi.metadata.definition.component.ComponentParameter;
import io.metadew.iesi.metadata.definition.component.ComponentVersion;
import io.metadew.iesi.metadata.definition.component.key.ComponentKey;
import io.metadew.iesi.metadata.definition.component.key.ComponentParameterKey;
import io.metadew.iesi.metadata.definition.component.key.ComponentVersionKey;
import io.metadew.iesi.metadata.definition.security.SecurityGroupKey;
import io.metadew.iesi.metadata.service.security.SecurityGroupService;
import io.metadew.iesi.metadata.tools.IdentifierTools;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.PathItem;
import io.swagger.v3.oas.models.Paths;
import lombok.Data;
import lombok.extern.log4j.Log4j2;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

@org.springframework.stereotype.Component
@Log4j2
@Data
public class ComponentParser implements Parser<Component> {

    private final SecurityGroupService securityGroupService;

    public ComponentParser(SecurityGroupService securityGroupService) {
        this.securityGroupService = securityGroupService;
    }

    public List<Component> parse(OpenAPI openAPI) {
        List<Component> components = new ArrayList<>();
        SecurityGroupKey securityGroupKey = securityGroupService.get("PUBLIC")
                .map(Metadata::getMetadataKey)
                .orElseThrow(() -> new RuntimeException("could not find security group PUBLIC"));
        Paths paths = openAPI.getPaths();
        String connectionName = openAPI.getInfo().getTitle();
        Long componentVersion;
        try {
            componentVersion = Long.parseLong(openAPI.getInfo().getVersion().split("\\.")[0]);
        } catch (NumberFormatException numberFormatException) {
            throw new SwaggerParserException(Collections.singletonList("The version should be a number"));
        }


        for (Entry<String, PathItem> path : paths.entrySet()) {
            PathItem pathItem = path.getValue();
            Map<PathItem.HttpMethod, Operation> operations = pathItem.readOperationsMap();


            for (Entry<PathItem.HttpMethod, Operation> operationEntry : operations.entrySet()) {

                components.add(createComponent(
                        securityGroupKey,
                        componentVersion,
                        connectionName,
                        operationEntry.getValue(),
                        path.getKey(),
                        operationEntry.getKey().toString()
                ));
            }
        }

        return components;
    }

    public Component createComponent(SecurityGroupKey securityGroupKey, Long componentVersion, String connectionName, Operation operation, String pathName, String method) {
        List<ComponentParameter> componentParameters = new ArrayList<>();
        ComponentKey componentKey = new ComponentKey(
                IdentifierTools.getComponentIdentifier(operation.getOperationId()),
                componentVersion);

        componentParameters.add(new ComponentParameter(new ComponentParameterKey(componentKey, "endpoint"), pathName.replaceAll("[{}]", "#")));
        componentParameters.add(new ComponentParameter(new ComponentParameterKey(componentKey, "type"), method));
        componentParameters.add(new ComponentParameter(new ComponentParameterKey(componentKey, "connection"), connectionName));

        if (operation.getDescription() == null) {
            operation.setDescription("");
        }

        return new Component(
                componentKey,
                securityGroupKey,
                "PUBLIC",
                "http.request",
                operation.getOperationId(),
                operation.getDescription(),
                new ComponentVersion(new ComponentVersionKey(componentKey), operation.getDescription()),
                componentParameters,
                new ArrayList<>());

    }
}
