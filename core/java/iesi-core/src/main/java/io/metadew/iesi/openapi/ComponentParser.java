package io.metadew.iesi.openapi;

import io.metadew.iesi.metadata.definition.component.Component;
import io.metadew.iesi.metadata.definition.component.ComponentParameter;
import io.metadew.iesi.metadata.definition.component.ComponentVersion;
import io.metadew.iesi.metadata.definition.component.key.ComponentKey;
import io.metadew.iesi.metadata.definition.component.key.ComponentParameterKey;
import io.metadew.iesi.metadata.definition.component.key.ComponentVersionKey;
import io.metadew.iesi.metadata.tools.IdentifierTools;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.PathItem;
import io.swagger.v3.oas.models.Paths;
import io.swagger.v3.oas.models.media.Content;
import io.swagger.v3.oas.models.parameters.Parameter;
import io.swagger.v3.oas.models.parameters.RequestBody;
import io.swagger.v3.oas.models.responses.ApiResponse;
import io.swagger.v3.oas.models.responses.ApiResponses;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import lombok.Data;
import lombok.extern.log4j.Log4j2;


import java.util.*;
import java.util.Map.Entry;
import java.util.regex.Pattern;
import java.util.stream.Collectors;


@Log4j2
@Data
public class ComponentParser {
    private static ComponentParser instance;
    private static final String REQUEST = "request";
    private static final String SECURITY = "security";
    private static final String RESPONSE = "response";
    private static final String COMPONENT_TYPE = "http.request";
    private static final String HEADER = "header.%s";


    private ComponentParser() {
    }


    public static synchronized ComponentParser getInstance() {
        if (instance == null) {
            instance = new ComponentParser();
        }
        return instance;
    }

    public List<Component> parse(OpenAPI openAPI) {
        List<Component> components = new ArrayList<>();
        Paths paths = openAPI.getPaths();
        Map<String, SecurityScheme> securitySchemeMap = openAPI.getComponents().getSecuritySchemes();
        String connectionName = openAPI.getInfo().getTitle();
        Long componentVersion = Long.parseLong(openAPI.getInfo().getVersion());

        for (Entry<String, PathItem> path : paths.entrySet()) {
            PathItem pathItem = path.getValue();
            Map<PathItem.HttpMethod, Operation> operations = pathItem.readOperationsMap();


            for (Entry<PathItem.HttpMethod, Operation> operationEntry : operations.entrySet()) {
                components.addAll(createComponentsForOperation(
                        connectionName, componentVersion, path.getKey(), operationEntry.getKey(), operationEntry.getValue(),
                        securitySchemeMap
                ));
            }

        }

        return components;
    }

    public List<Component> createComponentsForOperation(String connectionName, Long componentVersion, String pathName,
                                                        PathItem.HttpMethod httpMethod, Operation operation,
                                                        Map<String, SecurityScheme> securitySchemeMap) {
        List<Component> components = new ArrayList<>();
        List<Map<String, String>> names = extractNames(operation, securitySchemeMap);
        String componentDescription = operation.getDescription();


        if (!names.isEmpty()) {
            for (Map<String, String> partNames : names) {
                Component baseComponent = createBaseComponent(buildName(operation.getOperationId(), partNames),
                        componentVersion, componentDescription);
                addComponentParameters(
                        connectionName, pathName, httpMethod, operation, partNames, baseComponent,
                        securitySchemeMap);
                components.add(baseComponent);
            }
        } else {
            Component baseComponent = createBaseComponent(operation.getOperationId(),
                    componentVersion, componentDescription);
            addComponentParameters(
                    connectionName, pathName, httpMethod, operation, new HashMap<>(), baseComponent,
                    securitySchemeMap);
            components.add(baseComponent);
        }
        return components;
    }


    private Component createBaseComponent(String componentName, Long componentVersion, String description) {
        ComponentKey componentKey = new ComponentKey(
                IdentifierTools.getComponentIdentifier(componentName),
                componentVersion);
        return new Component(componentKey,
                COMPONENT_TYPE,
                componentName,
                description,
                new ComponentVersion(new ComponentVersionKey(componentKey), description),
                new ArrayList<>(),
                new ArrayList<>());
    }

    private List<Map<String, String>> extractNames(Operation operation, Map<String, SecurityScheme> securitySchemeMap) {
        List<String> securities = getSecurities(operation, securitySchemeMap);
        List<String> requestContents = getRequestContents(operation.getRequestBody());
        List<String> responseContents = getResponseContents(operation.getResponses());
        List<List<String>> nameCombinations = new ArrayList<>(Arrays.asList(securities, requestContents, responseContents));
        List<Map<String, String>> names = new ArrayList<>();

        if (!(securities.isEmpty() && requestContents.isEmpty() && responseContents.isEmpty())) {
            names = generateNames(nameCombinations, new ArrayList<>(), 0, new LinkedHashMap<>());
        }
        return names;
    }


    public List<String> getSecurities(Operation operation, Map<String, SecurityScheme> securitySchemeMap) {
        List<String> securities = new ArrayList<>();
        List<Parameter> parameters = operation.getParameters();
        List<SecurityRequirement> securityRequirements = operation.getSecurity();

        if (parameters != null) {
            securities = parameters.stream()
                    .map(Parameter::getName)
                    .filter(securitySchemeMap::containsKey)
                    .collect(Collectors.toList());
        }

        if (securityRequirements != null) {
            securities.addAll(securityRequirements.stream()

                    .map(securityRequirement -> (String) securityRequirement.keySet().toArray()[0])
                    .collect(Collectors.toList()));
        }
        return securities;
    }


    public List<String> getRequestContents(RequestBody requestBody) {
        if (requestBody != null && requestBody.getContent() != null) {
            return new ArrayList<>(requestBody.getContent().keySet());
        }
        return new ArrayList<>();
    }

    public List<String> getResponseContents(ApiResponses apiResponses) {


        if (apiResponses != null) {
            for (Entry<String, ApiResponse> entry : apiResponses.entrySet()) {
                String statusCode = entry.getKey();
                Content content = entry.getValue().getContent();

                if (content != null && (isGreenStatus(statusCode) || statusCode.equals("default"))) {
                    return new ArrayList<>(content.keySet());
                }
            }
        }
        return new ArrayList<>();
    }

    public List<Map<String, String>> generateNames(List<List<String>> lists, List<Map<String, String>> result, int depth, Map<String, String> current) {
        if (depth == lists.size()) {
            result.add(new HashMap<>(current));
            return result;
        }
        if (lists.get(depth).isEmpty()) {

            generateNames(lists, result, depth + 1, createName(null, current, depth));
        }
        for (int i = 0; i < lists.get(depth).size(); i++) {
            String value = lists.get(depth).get(i);
            generateNames(lists, result, depth + 1, createName(value, current, depth));
        }

        return result;
    }


    public Map<String, String> createName(String value, Map<String, String> current, int depth) {
        switch (depth) {
            case 0:
                current.put(SECURITY, value);
                break;
            case 1:
                current.put(REQUEST, value);
                break;
            case 2:
                current.put(RESPONSE, value);
                break;
            default:
                break;
        }
        return current;
    }


    public void addComponentParameters(String connectionName, String pathName, PathItem.HttpMethod httpMethod,
                                       Operation operation, Map<String, String> partNames, Component component,
                                       Map<String, SecurityScheme> securitySchemeMap) {
        component.getParameters().addAll(getInfo(component.getMetadataKey(), pathName, httpMethod, connectionName));
        component.getParameters().addAll(getQueryParams(component.getMetadataKey(), operation.getParameters()));
        component.getParameters().addAll(getHeaders(component.getMetadataKey(), partNames, operation,
                securitySchemeMap));
    }

    public List<ComponentParameter> getInfo(ComponentKey componentKey, String pathName, PathItem.HttpMethod operationName, String connectionName) {
        List<ComponentParameter> componentParameters = new ArrayList<>();
        componentParameters.add(new ComponentParameter(new ComponentParameterKey(componentKey, "endpoint"),  pathName.replaceAll("[{}]", "#")));
        componentParameters.add(new ComponentParameter(new ComponentParameterKey(componentKey, "type"), operationName.name()));
        componentParameters.add(new ComponentParameter(new ComponentParameterKey(componentKey, "connection"), connectionName));
        return componentParameters;
    }

    public List<ComponentParameter> getQueryParams(ComponentKey componentKey, List<Parameter> parameters) {
        int counter = 1;
        List<ComponentParameter> queryParams = new ArrayList<>();

        if (parameters != null) {
            for (Parameter parameter : parameters) {
                if (parameter.getIn().equals("query")) {
                    String parameterName = parameter.getName();
                    queryParams.add(new ComponentParameter(new ComponentParameterKey(componentKey, String.format("queryParam.%s", counter)), String.format("%s, #%s#", parameterName, parameterName)));
                    counter += 1;
                }
            }
        }
        return queryParams;
    }


    public List<ComponentParameter> getHeaders(ComponentKey componentKey, Map<String, String> partNames,
                                               Operation operation, Map<String, SecurityScheme> securitySchemeMap) {
        int position = 0;
        List<ComponentParameter> parameters = new ArrayList<>();

        for (Entry<String, String> entry : partNames.entrySet()) {
            String value = entry.getValue();
            String key = entry.getKey();
            String componentParameterValue = null;
            if (value != null) {
                switch (key) {
                    case SECURITY:
                        SecurityScheme securityScheme = securitySchemeMap.get(value);
                        if (securityScheme == null) {
                            log.warn(String.format("The securityScheme %s provided doesn't exists in the documentation and will be ignored", value));
                        } else {
                            componentParameterValue = getSecurityHeaderValue(securityScheme, value);
                        }
                        break;
                    case REQUEST:
                        componentParameterValue = String.format("Content-Type, %s", value);
                        break;
                    case RESPONSE:
                        componentParameterValue = String.format("Accept, %s", value);
                        break;
                    default:
                        throw new UnexpectedError("Unexpected error");
                }
                parameters.add(
                        new ComponentParameter(new ComponentParameterKey(componentKey, String.format(HEADER, ++position)),
                                componentParameterValue)
                );
            }
        }

        for (Parameter parameter : operation.getParameters()) {
            String parameterName = parameter.getName();
            if (parameter.getIn().equals("header") && !partNames.containsValue(parameterName)) {
                parameters.add(new ComponentParameter(
                        new ComponentParameterKey(componentKey, String.format(HEADER, ++position)),
                        String.format("%s, #%s#", parameterName, parameterName)));
            }
        }

        return parameters;
    }

    public String getSecurityHeaderValue(SecurityScheme securityScheme, String securityName) {
        String scheme = securityScheme.getScheme();
        SecurityScheme.Type securityType = securityScheme.getType();
        String componentParameterValue;

        if (securityType.equals(SecurityScheme.Type.OAUTH2) ||
                (securityType.equals(SecurityScheme.Type.HTTP) && scheme.equals("bearer"))) {
            componentParameterValue = String.format("Authorization, Bearer #%s#", securityName);
        } else if (securityScheme.getType().equals(SecurityScheme.Type.HTTP) && scheme.equals("basic")) {
            componentParameterValue = String.format("Authorization, Basic #%s#", securityName);
        } else if (securityScheme.getType().equals(SecurityScheme.Type.APIKEY)) {
            componentParameterValue = String.format("X-API-KEY, #%s#", securityName);
        } else if (securityScheme.getType().equals(SecurityScheme.Type.OPENIDCONNECT)) {
            //https://connect2id.com/learn/openid-connect
            componentParameterValue = String.format("Host, #%s#", securityName);
        } else {
            throw new UnsuportedSecurityScheme(String.format("Provided a wrong/unsupported security schema type %s", securityType));
        }
        return componentParameterValue;
    }

    public boolean isGreenStatus(String statusCode) {
        Pattern pattern = Pattern.compile("2[0-9][0-9]");
        return pattern.matcher(statusCode).matches();
    }


    public String buildName(String operationId, Map<String, String> partNames) {
        List<String> formattedPartNames = partNames.keySet().stream().map(key -> {
            if (partNames.get(key) == null) {
                return "_";
            }
            if (key.equals(REQUEST) || key.equals(RESPONSE)) {
                return serializeContentName(partNames.get(key));
            }
            return partNames.get(key);
        }).collect(Collectors.toList());


        return operationId.concat("." + String.join(".", formattedPartNames));
    }


    public String serializeContentName(String contentName) {
        switch (contentName) {
            case "application/json":
                return "JSON";
            case "application/xml":
                return "XML";
            case "application/x-www-form-urlencoded":
                return "FORM";
            case "application/octet-stream":
                return "OCTETSTRM";
            default:
                return "??";
        }
    }
}
