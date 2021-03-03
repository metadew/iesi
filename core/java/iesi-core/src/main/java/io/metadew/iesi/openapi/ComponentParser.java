package io.metadew.iesi.openapi;

import io.metadew.iesi.metadata.definition.component.Component;
import io.metadew.iesi.metadata.definition.component.ComponentParameter;
import io.metadew.iesi.metadata.definition.component.ComponentVersion;
import io.metadew.iesi.metadata.definition.component.key.ComponentKey;
import io.metadew.iesi.metadata.definition.component.key.ComponentParameterKey;
import io.metadew.iesi.metadata.definition.component.key.ComponentVersionKey;
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
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;
import java.util.Map.Entry;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Log4j2
@Data
public class ComponentParser {
    private static  ComponentParser instance;
    private static final String REQUEST = "request";
    private static final String RESPONSE = "response";
    private static final String HEADER = "header.%s";




    private ComponentParser() {}


    public static synchronized ComponentParser getInstance() {
        if (instance == null) {
            instance = new ComponentParser();
        }
        return instance;
    }

    public List<Component> parse(OpenAPI openAPI) {
        List<Component> components = new ArrayList<>();


        Paths paths = openAPI.getPaths();
        for (Entry<String, PathItem> path : paths.entrySet()) {
            PathItem pathItem = path.getValue();
            Map<PathItem.HttpMethod, Operation> operations = pathItem.readOperationsMap();

            for (Entry<PathItem.HttpMethod, Operation> operation : operations.entrySet()) {
                components.addAll(initComponents(operation.getValue(), operation.getKey(), path.getKey(), openAPI));
            }

        }

       return components;
    }

    public List<Component> initComponents(Operation operation, PathItem.HttpMethod operationName, String pathName, OpenAPI openAPI) {
        List<Component> components = new ArrayList<>();
        List<String> securities = getSecurities(operation, openAPI);
        List<String> requestContents = getRequestContents(operation.getRequestBody());
        List<String> responseContents = getResponseContents(operation.getResponses());
        List<List<String>> nameCombinations = new ArrayList<>(Arrays.asList(securities, requestContents, responseContents));
        List<Map<String, String>> names = new ArrayList<>();

        if (!(securities.isEmpty() && requestContents.isEmpty() && responseContents.isEmpty())) {
            names = generateNames(nameCombinations, new ArrayList<>(), 0, new LinkedHashMap<>());
        }

        if (!names.isEmpty()) {
            for (Map<String, String> partNames : names) {
                components.add(createComponent(partNames, operation, operationName, pathName, openAPI));
            }
        } else {
            components.add(createComponent(new HashMap<>(), operation, operationName, pathName, openAPI));
        }
        return components;
    }


    public List<String> getSecurities(Operation operation, OpenAPI openAPI) {
        List<String> securities = new ArrayList<>();
        List<Parameter> parameters = operation.getParameters();
        List<SecurityRequirement> securityRequirements = operation.getSecurity();
        Map<String, SecurityScheme> securitySchemeMap = openAPI.getComponents().getSecuritySchemes();

        if (parameters != null) {
            securities = parameters.stream().map(parameter -> {
                if (securitySchemeMap.containsKey(parameter.getName())) {
                    return parameter.getName();
                }
                return null;
            }).filter(Objects::nonNull).collect(Collectors.toList());
        }

        if (securityRequirements != null) {
            securities.addAll(securityRequirements.stream()
                    .map(securityRequirement -> (String) securityRequirement.keySet().toArray()[0]).collect(Collectors.toList()));
        }
        return securities;
    }

    public  List<String> getRequestContents(RequestBody requestBody) {
        if (requestBody != null && requestBody.getContent() != null) {
            return new ArrayList<>(requestBody.getContent().keySet());
        }
        return new ArrayList<>();
    }

    public List<String> getResponseContents(ApiResponses apiResponses) {

        if (apiResponses!= null) {
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
            generateNames(lists, result, depth + 1,  createName(null, current, depth));
        }
        for (int i = 0; i < lists.get(depth).size(); i++) {
            String value = lists.get(depth).get(i);
            generateNames(lists, result, depth + 1,  createName(value, current, depth));
        }

        return result;
    }

    public Map<String, String> createName(String value,  Map<String, String> current, int depth) {
        switch (depth) {
            case 0:
                current.put("security", value);
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


    public  Component createComponent(Map<String, String> partNames, Operation operation,PathItem.HttpMethod operationName, String pathName, OpenAPI openAPI) {
        long version = Long.parseLong(openAPI.getInfo().getVersion());
        String connectionName = openAPI.getInfo().getTitle();
        String componentName = buildName(operation.getOperationId(), partNames);
        String componentType = "http.request";
        String componentDescription = operation.getDescription();
        ComponentVersion componentVersion = new ComponentVersion(new ComponentVersionKey(componentName, version),componentDescription);

        List<ComponentParameter> info = getInfo(componentName,pathName, operationName, version, connectionName);
        List<ComponentParameter> queryParams = getQueryParams(componentName, operation.getParameters(), version);
        List<ComponentParameter> headers = getHeaders(componentName, partNames, operation, openAPI);
        List<ComponentParameter> params = Stream.of(info, queryParams, headers).flatMap(Collection::stream).collect(Collectors.toList());
        return new Component(new ComponentKey(componentName, version), componentType, componentName, componentDescription, componentVersion, params, new ArrayList<>());

    }

    public  List<ComponentParameter> getInfo(String componentName, String pathName, PathItem.HttpMethod operationName, Long version, String connectionName) {
        List<ComponentParameter> componentParameters = new ArrayList<>();
        componentParameters.add(new ComponentParameter(new ComponentParameterKey(componentName, version, "endpoint"), translatePathName(pathName)));
        componentParameters.add(new ComponentParameter(new ComponentParameterKey(componentName, version, "type"), operationName.name()));
        componentParameters.add(new ComponentParameter(new ComponentParameterKey(componentName, version, "connection"), connectionName));
        return componentParameters;
    }

    public  List<ComponentParameter> getQueryParams(String componentName, List<Parameter> parameters, Long versionNumber) {
        int counter = 1;
        List<ComponentParameter> queryParams = new ArrayList<>();

        if (parameters != null) {
            for (Parameter parameter : parameters) {
                if (parameter.getIn().equals("query")) {
                    String parameterName = parameter.getName();
                    queryParams.add(new ComponentParameter(new ComponentParameterKey(componentName, versionNumber, String.format("queryParam.%s", counter)), String.format("%s, #%s#", parameterName, parameterName)));
                    counter += 1;
                }
            }
        }
        return queryParams;
    }


    public  List<ComponentParameter> getHeaders(String componentName, Map<String, String> partNames, Operation operation, OpenAPI openAPI) {
        int position = 0;
        long versionNumber = Long.parseLong(openAPI.getInfo().getVersion());
        List<ComponentParameter> parameters = new ArrayList<>();
        Map<String, SecurityScheme> securitySchemeMap = openAPI.getComponents().getSecuritySchemes();

        for (Entry<String, String> entry : partNames.entrySet()) {
            String value = entry.getValue();
            String key = entry.getKey();
            if (value != null) {
                switch (key) {
                    case "security":
                        addSecurityHeader(securitySchemeMap.get(value), parameters, componentName, openAPI, ++position, value);
                        break;
                    case REQUEST:
                        parameters.add(new ComponentParameter(new ComponentParameterKey(componentName, versionNumber, String.format(HEADER, ++position)), String.format("Content-Type, %s", value)));
                        break;
                    case RESPONSE:
                        parameters.add(new ComponentParameter(new ComponentParameterKey(componentName, versionNumber, String.format(HEADER, ++position)), String.format("Accept, %s", value)));
                        break;
                    default:
                        break;
                }
            }
        }

        for (Parameter parameter : operation.getParameters()) {
            String parameterName = parameter.getName();
            if (parameter.getIn().equals("header") && !partNames.containsValue(parameterName)) {
                parameters.add(new ComponentParameter(new ComponentParameterKey(componentName, versionNumber, String.format(HEADER, ++position)), String.format("%s, #%s#",parameterName,parameterName)));
            }
        }

        return parameters;
    }

    public void addSecurityHeader(SecurityScheme securityScheme, List<ComponentParameter> componentParameters,String componentName, OpenAPI openAPI, int position, String value) {
        long versionNumber = Long.parseLong(openAPI.getInfo().getVersion());
        String scheme = securityScheme.getScheme();
        SecurityScheme.Type securityType = securityScheme.getType();
        

        if (securityType == null) {
            log.warn("The securityScheme provided doesn't exists");
            return;
        }
        if (securityType.equals(SecurityScheme.Type.OAUTH2) || (securityType.equals(SecurityScheme.Type.HTTP) && scheme.equals("bearer"))) {
            componentParameters.add(new ComponentParameter(new ComponentParameterKey(componentName, versionNumber, String.format(HEADER, position)), String.format("Authorization, Bearer #%s#", value)));
        }
        else if (securityScheme.getType().equals(SecurityScheme.Type.HTTP) && scheme.equals("basic" )) {
            componentParameters.add(new ComponentParameter(new ComponentParameterKey(componentName, versionNumber, String.format(HEADER, position)), String.format("Authorization, Basic #%s#", value)));
        }
        else if (securityScheme.getType().equals(SecurityScheme.Type.APIKEY)) {
            componentParameters.add(new ComponentParameter(new ComponentParameterKey(componentName, versionNumber, String.format(HEADER, position)), String.format("X-API-KEY, #%s#", value)));
        } else if (securityScheme.getType().equals(SecurityScheme.Type.OPENIDCONNECT)) {
            componentParameters.add(new ComponentParameter(new ComponentParameterKey(componentName, versionNumber, String.format(HEADER, position)), String.format("X-API-KEY, #%s#", value)));
        }

    }

    public String translatePathName(String pathName) {
        return pathName.replaceAll("[{}]", "#");
    }

    public boolean isGreenStatus(String statusCode) {
        Pattern pattern = Pattern.compile("2[0-9][0-9]");
        return pattern.matcher(statusCode).matches();
    }

    public  String buildName(String operationId, Map<String, String> partNames) {
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

    public  String serializeContentName(String contentName) {
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
