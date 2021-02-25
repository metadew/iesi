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

import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ComponentParser {
    private static ComponentParser INSTANCE;

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

    private  List<Component> initComponents(Operation operation, PathItem.HttpMethod operationName, String pathName) {
        List<Component> components = new ArrayList<>();
        List<String> securities = new ArrayList<>(getSecurities(operation));
        List<String> requestContents = new ArrayList<>(getRequestContents(operation.getRequestBody()));
        List<String> responseContents = new ArrayList<>(getResponseContents(operation.getResponses()));
        List<List<String>> nameCombinations = new ArrayList<>(Arrays.asList(securities, requestContents, responseContents));

        List<HashMap<String, String>> names = new ArrayList<>();
        generateNames(nameCombinations, names, 0, new LinkedHashMap<>());

        if (!names.isEmpty()) {
            for (HashMap<String, String> partNames : names) {
                components.add(createComponent(partNames, operation, operationName, pathName));
            }
        } else {
            components.add(createComponent(new HashMap<>(), operation, operationName, pathName));
        }
        return components;
    }


    private  Set<String> getSecurities(Operation operation) {
        Set<String> securities = new HashSet<>();
        List<Parameter> parameters = operation.getParameters();
        List<SecurityRequirement> securityRequirements = operation.getSecurity();

        if (parameters != null) {
            securities = parameters.stream().map(parameter -> {
                if (securitySchemeMap.containsKey(parameter.getName())) {
                    return parameter.getName();
                }
                return null;
            }).filter(Objects::nonNull).collect(Collectors.toSet());
        }

        if (securityRequirements != null) {
            securities.addAll(securityRequirements.stream().map(securityRequirement -> (String) securityRequirement.keySet().toArray()[0]).collect(Collectors.toSet()));
        }
        return securities;
    }

    private  Set<String> getRequestContents(RequestBody requestBody) {
        if (requestBody != null && requestBody.getContent() != null) {
            return requestBody.getContent().keySet();
        }
        return new HashSet<>();
    }

    private  Set<String> getResponseContents(ApiResponses apiResponses) {
        if (apiResponses!= null) {
            for (Map.Entry<String, ApiResponse> entry : apiResponses.entrySet()) {
                String statusCode = entry.getKey();
                Content content = entry.getValue().getContent();

                if (content != null && (isGreenStatus(statusCode) || statusCode.equals("default"))) {
                    return content.keySet();
                }
            }
        }
        return new HashSet<>();
    }

    void generateNames(List<List<String>> lists, List<HashMap<String, String>> result, int depth, LinkedHashMap<String, String> current) {
        if (depth == lists.size()) {
            result.add((HashMap<String, String>) current.clone());
            return;
        }
        if (lists.get(depth).isEmpty()) {
            generateNames(lists, result, depth + 1,  createName(null, current, depth));
            return;

        }
        for (int i = 0; i < lists.get(depth).size(); i++) {
            String value = lists.get(depth).get(i);
            generateNames(lists, result, depth + 1,  createName(value, current, depth));
        }
    }

    private LinkedHashMap<String, String> createName(String value,  LinkedHashMap<String, String> current, int depth) {
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


    private  Component createComponent(HashMap<String, String> partNames, Operation operation,PathItem.HttpMethod operationName, String pathName) {
        String componentName = buildName(operation.getOperationId(), partNames);
        String componentType = "http.request";
        String componentDescription = operation.getDescription();
        ComponentVersion componentVersion = new ComponentVersion(new ComponentVersionKey(componentName, versionNumber),componentDescription);

        List<ComponentParameter> infos = getInfos(componentName,pathName, operationName);
        List<ComponentParameter> queryParams = getQueryParams(componentName, operation.getParameters());
        List<ComponentParameter> headers = getHeaders(componentName, partNames);
        List<ComponentParameter> params = Stream.of(infos, queryParams, headers).flatMap(Collection::stream).collect(Collectors.toList());
        Component component = new Component(new ComponentKey(componentName, versionNumber), componentType, componentName, componentDescription, componentVersion, params, new ArrayList<>());
        return component;
    }

    private  List<ComponentParameter> getInfos(String componentName, String pathName, PathItem.HttpMethod operationName) {
        return new ArrayList(){{
            add(new ComponentParameter(new ComponentParameterKey(componentName, versionNumber, "endpoint"), pathName));
            add(new ComponentParameter(new ComponentParameterKey(componentName, versionNumber, "type"), operationName.name()));
            add(new ComponentParameter(new ComponentParameterKey(componentName, versionNumber, "connection"), connectionName));
        }};
    }

    private  List<ComponentParameter> getQueryParams(String componentName, List<Parameter> parameters) {
        Integer counter = 1;
        List<ComponentParameter> queryParams = new ArrayList();


        if (parameters != null) {
            for (int i = 0; i < parameters.size(); i++ ) {
                Parameter parameter = parameters.get(i);
                if (parameter.getIn() != null && parameter.getIn().equals("query")) {
                    String parameterName = parameter.getName();
                    queryParams.add(new ComponentParameter(new ComponentParameterKey(componentName, versionNumber,String.format("queryParam.%s", counter) ), String.format("%s, #%s#", parameterName, parameterName)));
                    counter += 1;
                }
            }
        }
        return queryParams;
    }


    private  List<ComponentParameter> getHeaders(String componentName, HashMap<String, String> partNames) {
        int position = 0;
        List<ComponentParameter> parameters = new ArrayList();
        List<String> headerTypes = new ArrayList(partNames.keySet());

        for (int i = 0; i < headerTypes.size(); i++) {
            String key = headerTypes.get(i);
            String value = partNames.get(key);
            if (value != null) {
                switch (key) {
                    case "security":
                        SecurityScheme.Type securityType = securitySchemeMap.get(value).getType();
                        if (securityType == SecurityScheme.Type.OAUTH2) {
                            parameters.add(new ComponentParameter(new ComponentParameterKey(componentName,versionNumber, String.format("header.%s", ++position)), String.format("Authorization, Bearer #%s#",value)));
                        } else if (securityType == SecurityScheme.Type.APIKEY) {
                            parameters.add(new ComponentParameter(new ComponentParameterKey(componentName,versionNumber, String.format("header.%s",++position)), String.format("X-API-KEY, #%s#", value)));
                        }
                        break;
                    case "request":
                        parameters.add(new ComponentParameter(new ComponentParameterKey(componentName,versionNumber, String.format("header.%s", ++position)), String.format("Content-Type, %s", value)));
                        break;
                    case "response":
                        parameters.add(new ComponentParameter(new ComponentParameterKey(componentName,versionNumber, String.format("header.%s", ++position)), String.format("Accept, %s", value)));
                        break;
                }
            }

        }
        return parameters;
    }

    private  List<HashMap<String, String>>  generateName(Set<String> securities, Set<String> requestContents, Set<String> responseContents) {
        List<HashMap<String, String>> names = new ArrayList<>();


        if (!securities.isEmpty()) {
            for (String security : securities) {
                if (!requestContents.isEmpty()) {
                    for (String requestContent : requestContents) {
                        if (!responseContents.isEmpty()) {
                            for (String responseContent : responseContents) {
                                names.add(addPartName(security, requestContent, responseContent));
                            }
                        } else {
                            names.add(addPartName(security, requestContent, null));
                        }

                    }
                } else if (!responseContents.isEmpty()) {
                    for (String responseContent : responseContents) {
                        names.add(addPartName(security, null, responseContent));
                    }
                } else {
                    names.add(addPartName(security, null, null));
                }
            }
        } else if (!requestContents.isEmpty()) {
            for (String requestContent : requestContents) {
                if (!responseContents.isEmpty()) {
                    for (String responseContent : responseContents) {
                        names.add(addPartName(null, requestContent, responseContent));
                    }
                } else {
                    names.add(addPartName(null, requestContent, null));
                }

            }
        } else if (!responseContents.isEmpty()) {
            for (String responseContent : responseContents) {
                names.add(addPartName(null, null, responseContent));
            }
        }

        return names;
    }

    private  LinkedHashMap<String, String> addPartName(String security, String requestContent, String responseContent) {
        return new LinkedHashMap<String, String>() {{
            put("security", security);
            put("request", requestContent);
            put("response", responseContent);
        }};
    }

    private  Boolean isGreenStatus(String statusCode) {
        Pattern pattern = Pattern.compile("2[0-9][0-9]");
        return pattern.matcher(statusCode).matches();
    }

    private  String buildName(String operationId, HashMap<String, String> partNames) {
        List<String> formatedPartNames = partNames.keySet().stream().map(key -> {
            if (partNames.get(key) == null) {
                return "_";
            }
            if (key.equals("request") || key.equals("response")) {
                try {
                    return serializeContentName(partNames.get(key));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            return partNames.get(key);
        }).collect(Collectors.toList());


        return operationId.concat("." + listToString(formatedPartNames, "."));
    }


    private  String listToString(List<String> list, String delimiter) {
        return String.join(delimiter, list);
    }

    private  String serializeContentName(String contentName) throws Exception {
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
                throw new Exception("The content name doesn't exist");
        }
    }
}
