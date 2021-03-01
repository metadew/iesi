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
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Log4j2
@Data
public class ComponentParser {
    private static ComponentParser INSTANCE;


    private static final Logger LOGGER = LogManager.getLogger();
    private Map<String, SecurityScheme> securitySchemeMap;
    private Long versionNumber;
    private String connectionName;


    private ComponentParser() {}


    public synchronized static ComponentParser getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new ComponentParser();
        }
        return INSTANCE;
    }

    public List<Component> parse(OpenAPI openAPI) {
        List<Component> components = new ArrayList<>();

        securitySchemeMap = openAPI.getComponents().getSecuritySchemes();
        versionNumber = Long.parseLong(openAPI.getInfo().getVersion());
        connectionName = openAPI.getInfo().getTitle();

        Paths paths = openAPI.getPaths();
        for (String pathName : paths.keySet()) {
            PathItem path = paths.get(pathName);
            Map<PathItem.HttpMethod, Operation> operations = path.readOperationsMap();

            for (Map.Entry<PathItem.HttpMethod, Operation> entry : operations.entrySet()) {
                components.addAll(initComponents(entry.getValue(), entry.getKey(), pathName));
            }

        }

       return components;
    }

    public List<Component> initComponents(Operation operation, PathItem.HttpMethod operationName, String pathName) {
        List<Component> components = new ArrayList<>();
        List<String> securities = getSecurities(operation);
        List<String> requestContents = getRequestContents(operation.getRequestBody());
        List<String> responseContents = getResponseContents(operation.getResponses());
        List<List<String>> nameCombinations = new ArrayList<>(Arrays.asList(securities, requestContents, responseContents));
        List<HashMap<String, String>> names = new ArrayList<>();

        if (!(securities.isEmpty() && requestContents.isEmpty() && responseContents.isEmpty())) {
            names = generateNames(nameCombinations, new ArrayList<>(), 0, new LinkedHashMap<>());
        }



        if (!names.isEmpty()) {
            for (HashMap<String, String> partNames : names) {
                components.add(createComponent(partNames, operation, operationName, pathName));
            }
        } else {
            components.add(createComponent(new HashMap<>(), operation, operationName, pathName));
        }
        return components;
    }


    public List<String> getSecurities(Operation operation) {
        List<String> securities = new ArrayList<>();
        List<Parameter> parameters = operation.getParameters();
        List<SecurityRequirement> securityRequirements = operation.getSecurity();

        if (parameters != null) {
            securities = parameters.stream().map(parameter -> {
                if (securitySchemeMap.containsKey(parameter.getName())) {
                    return parameter.getName();
                }
                return null;
            }).filter(Objects::nonNull).collect(Collectors.toList());
        }

        if (securityRequirements != null) {
            securities.addAll(securityRequirements.stream().map(securityRequirement -> {
                return (String) securityRequirement.keySet().toArray()[0];
            }).collect(Collectors.toList()));
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
            for (Map.Entry<String, ApiResponse> entry : apiResponses.entrySet()) {
                String statusCode = entry.getKey();
                Content content = entry.getValue().getContent();

                if (content != null && (isGreenStatus(statusCode) || statusCode.equals("default"))) {
                    return new ArrayList<>(content.keySet());
                }
            }
        }
        return new ArrayList<>();
    }

    public List<HashMap<String, String>> generateNames(List<List<String>> lists, List<HashMap<String, String>> result, int depth, LinkedHashMap<String, String> current) {
        if (depth == lists.size()) {
            result.add((HashMap<String, String>) current.clone());
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

    public LinkedHashMap<String, String> createName(String value,  LinkedHashMap<String, String> current, int depth) {
        switch (depth) {
            case 0:
                current.put("security", value);
                break;
            case 1:
                current.put("request", value);
                break;
            case 2:
                current.put("response", value);
                break;
        }
        return current;
    }


    public  Component createComponent(HashMap<String, String> partNames, Operation operation,PathItem.HttpMethod operationName, String pathName) {
        String componentName = buildName(operation.getOperationId(), partNames);
        String componentType = "http.request";
        String componentDescription = operation.getDescription();
        ComponentVersion componentVersion = new ComponentVersion(new ComponentVersionKey(componentName, versionNumber),componentDescription);

        List<ComponentParameter> infos = getInfos(componentName,pathName, operationName);
        List<ComponentParameter> queryParams = getQueryParams(componentName, operation.getParameters());
        List<ComponentParameter> headers = getHeaders(componentName, partNames, operation);
        List<ComponentParameter> params = Stream.of(infos, queryParams, headers).flatMap(Collection::stream).collect(Collectors.toList());
        return new Component(new ComponentKey(componentName, versionNumber), componentType, componentName, componentDescription, componentVersion, params, new ArrayList<>());

    }

    public  List<ComponentParameter> getInfos(String componentName, String pathName, PathItem.HttpMethod operationName) {
        List<ComponentParameter> componentParameters = new ArrayList<>();
        componentParameters.add(new ComponentParameter(new ComponentParameterKey(componentName, versionNumber, "endpoint"), pathName));
        componentParameters.add(new ComponentParameter(new ComponentParameterKey(componentName, versionNumber, "type"), operationName.name()));
        componentParameters.add(new ComponentParameter(new ComponentParameterKey(componentName, versionNumber, "connection"), connectionName));
        return componentParameters;
    }

    public  List<ComponentParameter> getQueryParams(String componentName, List<Parameter> parameters) {
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


    public  List<ComponentParameter> getHeaders(String componentName, HashMap<String, String> partNames, Operation operation) {
        int position = 0;
        List<ComponentParameter> parameters = new ArrayList<>();

        for (Map.Entry entry : partNames.entrySet()) {
            String value = (String) entry.getValue();
            String key = (String) entry.getKey();
            if (value != null) {
                switch (key) {
                    case "security":
                        SecurityScheme securityType = securitySchemeMap.get(value);
                        if (securityType == null) {
                            LOGGER.warn("The securityScheme provided doesn't exists");
                            break;
                        }
                        if (securityType.getType() == SecurityScheme.Type.OAUTH2) {
                            parameters.add(new ComponentParameter(new ComponentParameterKey(componentName, versionNumber, String.format("header.%s", ++position)), String.format("Authorization, Bearer #%s#", value)));
                        } else if (securityType.getType() == SecurityScheme.Type.APIKEY) {
                            parameters.add(new ComponentParameter(new ComponentParameterKey(componentName, versionNumber, String.format("header.%s", ++position)), String.format("X-API-KEY, #%s#", value)));
                        }
                        break;
                    case "request":
                        parameters.add(new ComponentParameter(new ComponentParameterKey(componentName, versionNumber, String.format("header.%s", ++position)), String.format("Content-Type, %s", value)));
                        break;
                    case "response":
                        parameters.add(new ComponentParameter(new ComponentParameterKey(componentName, versionNumber, String.format("header.%s", ++position)), String.format("Accept, %s", value)));
                        break;
                }
            }
        }

        for (Parameter parameter : operation.getParameters()) {
            String parameterName = parameter.getName();
            if (parameter.getIn().equals("header") && !partNames.containsValue(parameterName)) {
                parameters.add(new ComponentParameter(new ComponentParameterKey(componentName, versionNumber, String.format("header.%s", ++position)), String.format("%s, #%s#",parameterName,parameterName)));
            }
        }


        return parameters;
    }

    public boolean isGreenStatus(String statusCode) {
        Pattern pattern = Pattern.compile("2[0-9][0-9]");
        return pattern.matcher(statusCode).matches();
    }

    public  String buildName(String operationId, HashMap<String, String> partNames) {
        List<String> formatedPartNames = partNames.keySet().stream().map(key -> {
            if (partNames.get(key) == null) {
                return "_";
            }
            if (key.equals("request") || key.equals("response")) {
                return serializeContentName(partNames.get(key));
            }
            return partNames.get(key);
        }).collect(Collectors.toList());


        return operationId.concat("." + String.join(".", formatedPartNames));
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
