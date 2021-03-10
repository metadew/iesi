package io.metadew.iesi.openapi;

import io.metadew.iesi.metadata.definition.component.ComponentParameter;
import io.metadew.iesi.metadata.definition.component.key.ComponentKey;
import io.metadew.iesi.metadata.definition.component.key.ComponentParameterKey;
import io.metadew.iesi.metadata.tools.IdentifierTools;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.PathItem;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.media.Content;
import io.swagger.v3.oas.models.media.MediaType;
import io.swagger.v3.oas.models.parameters.Parameter;
import io.swagger.v3.oas.models.parameters.RequestBody;
import io.swagger.v3.oas.models.responses.ApiResponse;
import io.swagger.v3.oas.models.responses.ApiResponses;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

class ComponentParserTest {


    private OpenAPI openAPI;
    private long versionNumber;

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
        this.versionNumber = Long.parseLong(openAPI.getInfo().getVersion());

    }

    @Test
    void getNoSecurities() {
        Operation operation = new Operation()
                .parameters(null)
                .security(null);

        //TESTS
        assertThat(ComponentParser.getInstance().getSecurities(operation, openAPI.getComponents().getSecuritySchemes()))

                .isEqualTo(new ArrayList<>());
    }

    @Test
    void getSecuritiesNotDefinedInParameters() {
        Operation operation = new Operation()
                .parameters(null)
                .addSecurityItem(new SecurityRequirement()
                        .addList("petstore_auth"))
                .addSecurityItem(new SecurityRequirement()
                        .addList("user_auth"));
        //TESTS
        assertThat(ComponentParser.getInstance().getSecurities(operation, openAPI.getComponents().getSecuritySchemes()))

                .isEqualTo(new ArrayList<>(Arrays.asList("petstore_auth", "user_auth")));
    }

    @Test
    void getSecuritiesDefinedInParametersAndSecurities() {
        Operation operation = new Operation()
                .addParametersItem(new Parameter().name("api_key").in("header"))
                .addSecurityItem(new SecurityRequirement()
                        .addList("petstore_auth"))
                .addSecurityItem(new SecurityRequirement()
                        .addList("user_auth"));

        //TEST

        assertThat(ComponentParser.getInstance().getSecurities(operation, openAPI.getComponents().getSecuritySchemes()))
                .isEqualTo(new ArrayList<>(Arrays.asList("api_key", "petstore_auth", "user_auth")));
    }

    @Test
    void getRequestContent() {
        RequestBody requestBody = new RequestBody()
                .content(new Content()
                        .addMediaType("application/json", new MediaType())
                        .addMediaType("application/xml", new MediaType()));

        //TESTS
        assertThat(ComponentParser.getInstance().getRequestContents(requestBody))
                .isEqualTo(Stream.of("application/json", "application/xml").collect(Collectors.toList()));
    }

    @Test
    void getEmptyRequestContent() {
        RequestBody requestBody = new RequestBody().content(new Content());

        //TESTS
        assertThat(ComponentParser.getInstance().getRequestContents(requestBody)).isEqualTo(new ArrayList<>());
    }


    @Test
    void getResponsesWith200Included() {
        ApiResponses apiResponses = new ApiResponses();
        ApiResponse response200 = new ApiResponse()
                .content(new Content()
                        .addMediaType("application/json", new MediaType()));

        apiResponses.put("200", response200);
        apiResponses.put("400", new ApiResponse());
        apiResponses.put("404", new ApiResponse());


        //TEST
        assertThat(ComponentParser.getInstance().getResponseContents(apiResponses))
                .isEqualTo(new ArrayList<>(Collections.singletonList("application/json")));

    }

    @Test
    void getResponsesWithNo200Included() {
        ApiResponses apiResponses = new ApiResponses();

        apiResponses.put("400", new ApiResponse());
        apiResponses.put("404", new ApiResponse());


        //TEST
        assertThat(ComponentParser.getInstance().getResponseContents(apiResponses))
                .isEqualTo(new ArrayList<>());
    }

    @Test
    void getResponsesWithNo200AndContentIncluded() {
        ApiResponses apiResponses = new ApiResponses();
        ApiResponse response404 = new ApiResponse()
                .content(new Content()
                        .addMediaType("application/json", new MediaType()));

        apiResponses.put("400", new ApiResponse());
        apiResponses.put("404", response404);

        //TEST
        assertThat(ComponentParser.getInstance().getResponseContents(apiResponses))
                .isEqualTo(new ArrayList<>());
    }

    @Test
    void getDefaultResponseWithContent() {
        ApiResponses apiResponses = new ApiResponses();
        ApiResponse defaultResponse = new ApiResponse()
                .content(new Content()
                        .addMediaType("application/json", new MediaType())
                        .addMediaType("application/xml", new MediaType())
                );

        apiResponses.put("default", defaultResponse);

        //TEST
        assertThat(ComponentParser.getInstance().getResponseContents(apiResponses))
                .isEqualTo(new ArrayList<>(defaultResponse.getContent().keySet()));
    }

    @Test
    void getDefaultResponseWithoutContent() {
        ApiResponses apiResponses = new ApiResponses();
        apiResponses.put("default", new ApiResponse());

        //TESTS
        assertThat(ComponentParser.getInstance().getResponseContents(apiResponses))
                .isEqualTo(new ArrayList<>());
    }

    @Test
    void generateNamesWithAll() {
        List<String> securities = Collections.singletonList("petstore_auth");
        List<String> requestContents = Arrays.asList("application/x-www-form-urlencoded", "application/json");
        List<String> responseContents = Collections.singletonList("application/xml");
        List<List<String>> nameCombinations = new ArrayList<>(Arrays.asList(securities, requestContents, responseContents));
        HashMap<String, String> hashMap = new HashMap<>();
        List<HashMap<String, String>> names = new ArrayList<>();

        hashMap.put("security", "petstore_auth");
        hashMap.put("request", "application/x-www-form-urlencoded");
        hashMap.put("response", "application/xml");
        names.add(hashMap);
        hashMap = new HashMap<>();
        hashMap.put("security", "petstore_auth");
        hashMap.put("request", "application/json");
        hashMap.put("response", "application/xml");
        names.add(hashMap);

        //TESTS
        assertThat(ComponentParser.getInstance().generateNames(nameCombinations, new ArrayList<>(), 0, new LinkedHashMap<>()))
                .isEqualTo(names);
    }

    @Test
    void generateNamesWitoutSecurity() {
        List<String> securities = new ArrayList<>();
        List<String> requestContents = Arrays.asList("application/x-www-form-urlencoded", "application/json");
        List<String> responseContents = Collections.singletonList("application/xml");
        List<List<String>> nameCombinations = new ArrayList<>(Arrays.asList(securities, requestContents, responseContents));
        HashMap<String, String> hashMap = new HashMap<>();
        List<HashMap<String, String>> names = new ArrayList<>();

        hashMap.put("security", null);
        hashMap.put("request", "application/x-www-form-urlencoded");
        hashMap.put("response", "application/xml");
        names.add(hashMap);
        hashMap = new HashMap<>();
        hashMap.put("security", null);
        hashMap.put("request", "application/json");
        hashMap.put("response", "application/xml");
        names.add(hashMap);

        //TESTS
        assertThat(ComponentParser.getInstance().generateNames(nameCombinations, new ArrayList<>(), 0, new LinkedHashMap<>()))
                .isEqualTo(names);
    }

    @Test
    void generateNamesWitoutRequest() {
        List<String> securities = Collections.singletonList("petstore_auth");
        List<String> requestContents = new ArrayList<>();
        List<String> responseContents = Collections.singletonList("application/xml");
        List<List<String>> nameCombinations = new ArrayList<>(Arrays.asList(securities, requestContents, responseContents));
        HashMap<String, String> hashMap = new HashMap<>();
        List<HashMap<String, String>> names = new ArrayList<>();

        hashMap.put("security", "petstore_auth");
        hashMap.put("request", null);
        hashMap.put("response", "application/xml");
        names.add(hashMap);

        //TESTS
        assertThat(ComponentParser.getInstance().generateNames(nameCombinations, new ArrayList<>(), 0, new LinkedHashMap<>()))
                .isEqualTo(names);
    }

    @Test
    void generateNamesWhitoutResponse() {
        List<String> securities = Collections.singletonList("petstore_auth");
        List<String> requestContents = Arrays.asList("application/x-www-form-urlencoded", "application/json");
        List<String> responseContents = new ArrayList<>();
        List<List<String>> nameCombinations = new ArrayList<>(Arrays.asList(securities, requestContents, responseContents));
        HashMap<String, String> hashMap = new HashMap<>();
        List<HashMap<String, String>> names = new ArrayList<>();

        hashMap.put("security", "petstore_auth");
        hashMap.put("request", "application/x-www-form-urlencoded");
        hashMap.put("response", null);
        names.add(hashMap);
        hashMap = new HashMap<>();
        hashMap.put("security", "petstore_auth");
        hashMap.put("request", "application/json");
        hashMap.put("response", null);
        names.add(hashMap);

        //TESTS
        assertThat(ComponentParser.getInstance().generateNames(nameCombinations, new ArrayList<>(), 0, new LinkedHashMap<>()))
                .isEqualTo(names);
    }

    @Test
    void getInfo() {
        String componentName = "AUTH.JSON.JSON";
        String componentID = IdentifierTools.getComponentIdentifier(componentName);
        String pathName = "/pet";
        ComponentKey componentKey = new ComponentKey(
                componentID,
                versionNumber);
        List<ComponentParameter> componentParameters = new ArrayList<>();
        PathItem.HttpMethod get = PathItem.HttpMethod.GET;

        componentParameters.add(new ComponentParameter(new ComponentParameterKey(componentID, versionNumber, "endpoint"), pathName));
        componentParameters.add(new ComponentParameter(new ComponentParameterKey(componentID, versionNumber, "type"), get.name()));
        componentParameters.add(new ComponentParameter(new ComponentParameterKey(componentID, versionNumber, "connection"), openAPI.getInfo().getTitle()));

        //TESTS
        assertThat(ComponentParser.getInstance().getInfo(componentKey, pathName, get, openAPI.getInfo().getTitle()))

                .isEqualTo(componentParameters);
    }

    @Test
    void getInfoWithPathParam() {
        String componentName = "AUTH.JSON.JSON";
        String componentID = IdentifierTools.getComponentIdentifier(componentName);
        String pathName = "/pet/{id}";
        ComponentKey componentKey = new ComponentKey(
                componentID,
                versionNumber);
        List<ComponentParameter> componentParameters = new ArrayList<>();
        PathItem.HttpMethod get = PathItem.HttpMethod.GET;

        componentParameters.add(new ComponentParameter(new ComponentParameterKey(componentID, versionNumber, "endpoint"), "/pet/#id#"));
        componentParameters.add(new ComponentParameter(new ComponentParameterKey(componentID, versionNumber, "type"), get.name()));
        componentParameters.add(new ComponentParameter(new ComponentParameterKey(componentID, versionNumber, "connection"), openAPI.getInfo().getTitle()));

        //TESTS
        assertThat(ComponentParser.getInstance().getInfo(componentKey, pathName, get, openAPI.getInfo().getTitle()))
                .isEqualTo(componentParameters);
    }


    @Test
    void getQueryParams() {
        String componentName = "AUTH.JSON.JSON";
        String componentID = IdentifierTools.getComponentIdentifier(componentName);
        List<ComponentParameter> queryParams = new ArrayList<>();
        Parameter parameter = new Parameter().in("query").name("findByStatus");
        Parameter parameter1 = new Parameter().in("header");
        ComponentKey componentKey = new ComponentKey(
                componentID,
                versionNumber);


        queryParams.add(new ComponentParameter(
                new ComponentParameterKey(componentID, versionNumber, "queryParam.1"),
                "findByStatus, #findByStatus#"));


        assertThat(ComponentParser.getInstance().getQueryParams(componentKey, Arrays.asList(parameter, parameter1)))
                .isEqualTo(queryParams);
    }

    @Test
    void getHeaders() {
        String componentName = "AUTH.JSON.JSON";
        String componentID = IdentifierTools.getComponentIdentifier(componentName);
        HashMap<String, String> partNames = new LinkedHashMap<>();
        List<ComponentParameter> parameters = new ArrayList<>();
        Parameter allowHeader = new Parameter().in("header").name("Allow");
        Operation operation = new Operation().parameters(Collections.singletonList(allowHeader));

        ComponentKey componentKey = new ComponentKey(
                componentID,
                versionNumber);


        partNames.put("security", "petstore_auth");
        partNames.put("request", "application/x-www-form-urlencoded");
        partNames.put("response", "application/json");

        parameters.add(
                new ComponentParameter(
                        new ComponentParameterKey(componentID, versionNumber, "header.1"),
                        "Authorization, Bearer #petstore_auth#"
                )
        );
        parameters.add(
                new ComponentParameter(
                        new ComponentParameterKey(componentID, versionNumber, "header.2"),
                        "Content-Type, application/x-www-form-urlencoded"
                )
        );
        parameters.add(
                new ComponentParameter(
                        new ComponentParameterKey(componentID, versionNumber, "header.3"),
                        "Accept, application/json"
                )
        );

        parameters.add(
                new ComponentParameter(
                        new ComponentParameterKey(componentID, versionNumber, "header.4"),
                        "Allow, #Allow#"
                )
        );

        //TESTS
        assertThat(ComponentParser.getInstance().getHeaders(
                componentKey, partNames, operation, openAPI.getComponents().getSecuritySchemes()
        )).isEqualTo(parameters);

    }

    @Test
    void getHeadersWithNullValues() {
        String componentName = "AUTH.JSON.JSON";
        String componentID = IdentifierTools.getComponentIdentifier(componentName);
        HashMap<String, String> partNames = new LinkedHashMap<>();
        List<ComponentParameter> parameters = new ArrayList<>();
        Operation operation = mock(Operation.class);
        ComponentKey componentKey = new ComponentKey(
                componentID,
                versionNumber);

        partNames.put("security", null);
        partNames.put("request", "application/x-www-form-urlencoded");
        partNames.put("response", null);

        parameters.add(
                new ComponentParameter(
                        new ComponentParameterKey(componentID, versionNumber, "header.1"),
                        "Content-Type, application/x-www-form-urlencoded"
                )
        );


        //TESTS
        assertThat(ComponentParser.getInstance().getHeaders(
                componentKey, partNames, operation, openAPI.getComponents().getSecuritySchemes()
        ))
                .isEqualTo(parameters);

    }

    @Test
    void buildName() {
        String operationId = "updatePet";
        HashMap<String, String> partNames = new LinkedHashMap<>();

        partNames.put("security", "petstore_auth");
        partNames.put("request", "application/x-www-form-urlencoded");
        partNames.put("response", "application/json");

        //TESTS
        assertThat(ComponentParser.getInstance().buildName(operationId, partNames))
                .isEqualTo("updatePet.petstore_auth.FORM.JSON");
    }

    @Test
    void buildNameWithNullValues() {
        String operationId = "updatePet";
        HashMap<String, String> partNames = new LinkedHashMap<>();

        partNames.put("security", null);
        partNames.put("request", null);
        partNames.put("response", "application/json");

        //TESTS
        assertThat(ComponentParser.getInstance().buildName(operationId, partNames))
                .isEqualTo("updatePet._._.JSON");
    }

    @Test
    void serializeContentNames() {
        String contentName = "application/json";

        //TESTS
        assertThat(ComponentParser.getInstance().serializeContentName(contentName))
                .isEqualTo("JSON");
    }

    @Test
    void serializeUnknownContentNames() {
        String contentName = "unknown/unknown";

        //TESTS
        assertThat(ComponentParser.getInstance().serializeContentName(contentName))
                .isEqualTo("??");
    }

}
