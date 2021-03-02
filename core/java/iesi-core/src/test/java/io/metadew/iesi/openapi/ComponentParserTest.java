package io.metadew.iesi.openapi;

import io.metadew.iesi.metadata.definition.component.ComponentParameter;
import io.metadew.iesi.metadata.definition.component.key.ComponentParameterKey;
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
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ComponentParserTest {


    private OpenAPI openAPI;

    @BeforeEach
    public void init() {

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

        openAPI = new OpenAPI()
                .info(info)
                .components(new Components()
                        .securitySchemes(securitySchemeMap));

    }

    @Test
    public void getNoSecurities() {
        // TODO: to be removed if securitySchemeMap etc. is no longer a class variable
        ComponentParser.getInstance().setSecuritySchemeMap(openAPI.getComponents().getSecuritySchemes());
        ComponentParser.getInstance().setVersionNumber(Long.parseLong(openAPI.getInfo().getVersion()));
        ComponentParser.getInstance().setConnectionName(openAPI.getInfo().getTitle());
        Operation operation = new Operation()
                .parameters(null)
                .security(null);

        //TESTS
        assertThat(ComponentParser.getInstance().getSecurities(operation))
                .isEqualTo(new ArrayList<>());
    }

    @Test
    public void getSecuritiesNotDefinedInParameters() {
        ComponentParser.getInstance().setSecuritySchemeMap(openAPI.getComponents().getSecuritySchemes());
        ComponentParser.getInstance().setVersionNumber(Long.parseLong(openAPI.getInfo().getVersion()));
        ComponentParser.getInstance().setConnectionName(openAPI.getInfo().getTitle());
        Operation operation = new Operation()
                .parameters(null)
                .addSecurityItem(new SecurityRequirement()
                        .addList("petstore_auth"))
                .addSecurityItem(new SecurityRequirement()
                        .addList("user_auth"));

        //MOCKS
        //TESTS
        assertThat(ComponentParser.getInstance().getSecurities(operation))
                .isEqualTo(new ArrayList<>(Arrays.asList("petstore_auth", "user_auth")));
    }

    @Test
    public void getSecuritiesDefinedInParametersAndSecurities() {
        // TODO: mocking of paramter is not needed, see example above
        Operation operation = mock(Operation.class);
        SecurityRequirement securityRequirement = mock(SecurityRequirement.class);
        Parameter parameter = mock(Parameter.class);

        when(securityRequirement.keySet()).thenReturn(new HashSet<>(Collections.singletonList("petstore_auth")));
        when(parameter.getName()).thenReturn("api_key");
        when(operation.getParameters()).thenReturn(Collections.singletonList(parameter));
        when(operation.getSecurity()).thenReturn(Collections.singletonList(securityRequirement));

        //TEST
        assertThat(ComponentParser.getInstance().getSecurities(operation)).isEqualTo(new ArrayList<>(Arrays.asList("api_key", "petstore_auth")));
    }

    @Test
    public void getRequestContent() {
        RequestBody requestBody = new RequestBody()
                .content(new Content()
                        .addMediaType("application/json", new MediaType())
                        .addMediaType("application/xml", new MediaType()));

        //TESTS
        assertThat(ComponentParser.getInstance().getRequestContents(requestBody))
                .isEqualTo(Stream.of("application/json", "application/xml").collect(Collectors.toList()));
    }

    @Test
    public void getEmptyRequestContent() {
        // TODO: mocking of request body is not needed, see example above
        RequestBody requestBody = mock(RequestBody.class);
        Content content = mock(Content.class);

        when(content.keySet()).thenReturn(new HashSet<>());
        when(requestBody.getContent()).thenReturn(content);

        //TESTS
        assertThat(ComponentParser.getInstance().getRequestContents(requestBody)).isEqualTo(new ArrayList<>());
    }

    // TODO: see if mocking is really neccessary for tests below

    @Test
    public void getResponsesWith200Included() {
        ApiResponses apiResponses = new ApiResponses();
        ApiResponse response200 = mock(ApiResponse.class);
        ApiResponse response400 = mock(ApiResponse.class);
        ApiResponse response404 = mock(ApiResponse.class);
        Content content = mock(Content.class);

        apiResponses.put("200", response200);
        apiResponses.put("400", response400);
        apiResponses.put("404", response404);

        when(content.keySet()).thenReturn(new HashSet<>(Arrays.asList("application/json")));
        when(response200.getContent()).thenReturn(content);

        //TEST
        assertThat(ComponentParser.getInstance().getResponseContents(apiResponses)).isEqualTo(new ArrayList<>(Arrays.asList("application/json")));

    }

    @Test
    public void getResponsesWithNo200Included() {
        ApiResponses apiResponses = new ApiResponses();
        ApiResponse response400 = mock(ApiResponse.class);
        ApiResponse response404 = mock(ApiResponse.class);

        apiResponses.put("400", response400);
        apiResponses.put("404", response404);


        //TEST
        assertThat(ComponentParser.getInstance().getResponseContents(apiResponses)).isEqualTo(new ArrayList<>());
    }

    @Test
    public void getResponsesWithNo200AndContentIncluded() {
        ApiResponses apiResponses = new ApiResponses();
        ApiResponse response400 = mock(ApiResponse.class);
        ApiResponse response404 = mock(ApiResponse.class);
        Content content = mock(Content.class);

        apiResponses.put("400", response400);
        apiResponses.put("404", response404);

        when(content.keySet()).thenReturn(new HashSet<>(Arrays.asList("application/json")));
        when(response404.getContent()).thenReturn(content);

        //TEST
        assertThat(ComponentParser.getInstance().getResponseContents(apiResponses)).isEqualTo(new ArrayList<>());
    }

    @Test
    public void getDefaultResponseWithContent() {
        ApiResponses apiResponses = new ApiResponses();
        ApiResponse defaultResponse = mock(ApiResponse.class);
        Content content = mock(Content.class);

        apiResponses.put("default", defaultResponse);

        when(content.keySet()).thenReturn(new HashSet<>(Arrays.asList("application/json", "application/xml")));
        when(defaultResponse.getContent()).thenReturn(content);

        //TEST
        assertThat(ComponentParser.getInstance().getResponseContents(apiResponses)).isEqualTo(new ArrayList<>(content.keySet()));
    }

    @Test
    public void getDefaultResponseWithoutContent() {
        ApiResponses apiResponses = new ApiResponses();
        ApiResponse defaultResponse = mock(ApiResponse.class);

        apiResponses.put("default", defaultResponse);

        //TESTS
        assertThat(ComponentParser.getInstance().getResponseContents(apiResponses)).isEqualTo(new ArrayList<>());
    }

    @Test
    public void generateNamesWithAll() {
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
        assertThat(ComponentParser.getInstance().generateNames(nameCombinations, new ArrayList<>(), 0, new LinkedHashMap<>())).isEqualTo(names);
    }

    @Test
    public void generateNamesWitoutSecurity() {
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
        assertThat(ComponentParser.getInstance().generateNames(nameCombinations, new ArrayList<>(), 0, new LinkedHashMap<>())).isEqualTo(names);
    }

    @Test
    public void generateNamesWitoutRequest() {
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
        assertThat(ComponentParser.getInstance().generateNames(nameCombinations, new ArrayList<>(), 0, new LinkedHashMap<>())).isEqualTo(names);
    }

    @Test
    public void generateNamesWhitoutResponse() {
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
        assertThat(ComponentParser.getInstance().generateNames(nameCombinations, new ArrayList<>(), 0, new LinkedHashMap<>())).isEqualTo(names);
    }

    @Test
    public void getInfos() {
        String componentName = "AUTH.JSON.JSON";
        String pathName = "/pet";
        List<ComponentParameter> componentParameters = new ArrayList<>();
        PathItem.HttpMethod get = PathItem.HttpMethod.GET;

        componentParameters.add(new ComponentParameter(new ComponentParameterKey(componentName, ComponentParser.getInstance().getVersionNumber(), "endpoint"), pathName));
        componentParameters.add(new ComponentParameter(new ComponentParameterKey(componentName, ComponentParser.getInstance().getVersionNumber(), "type"), get.name()));
        componentParameters.add(new ComponentParameter(new ComponentParameterKey(componentName, ComponentParser.getInstance().getVersionNumber(), "connection"), ComponentParser.getInstance().getConnectionName()));

        //TESTS
        assertThat(ComponentParser.getInstance().getInfos(componentName, pathName, get)).isEqualTo(componentParameters);
    }

    @Test
    public void getQueryParams() {
        List<ComponentParameter> queryParams = new ArrayList<>();
        Parameter parameter = mock(Parameter.class);
        Parameter parameter1 = mock(Parameter.class);
        String componentName = "AUTH.JSON.JSON";

        when(parameter.getIn()).thenReturn("query");
        when(parameter.getName()).thenReturn("findByStatus");
        when(parameter1.getIn()).thenReturn("header");

        queryParams.add(new ComponentParameter(
                new ComponentParameterKey(componentName, ComponentParser.getInstance().getVersionNumber(), "queryParam.1"),
                "findByStatus, #findByStatus#"));


        assertThat(ComponentParser.getInstance().getQueryParams(componentName, Arrays.asList(parameter, parameter1))).isEqualTo(queryParams);
    }

    @Test
    public void getHeaders() {
        String componentName = "AUTH.JSON.JSON";
        HashMap<String, String> partNames = new LinkedHashMap<>();
        List<ComponentParameter> parameters = new ArrayList<>();
        Operation operation = mock(Operation.class);
        Parameter allowHeader = mock(Parameter.class);

        partNames.put("security", "petstore_auth");
        partNames.put("request", "application/x-www-form-urlencoded");
        partNames.put("response", "application/json");

        parameters.add(
                new ComponentParameter(
                        new ComponentParameterKey(componentName, ComponentParser.getInstance().getVersionNumber(), "header.1"),
                        "Authorization, Bearer #petstore_auth#"
                )
        );
        parameters.add(
                new ComponentParameter(
                        new ComponentParameterKey(componentName, ComponentParser.getInstance().getVersionNumber(), "header.2"),
                        "Content-Type, application/x-www-form-urlencoded"
                )
        );
        parameters.add(
                new ComponentParameter(
                        new ComponentParameterKey(componentName, ComponentParser.getInstance().getVersionNumber(), "header.3"),
                        "Accept, application/json"
                )
        );

        parameters.add(
                new ComponentParameter(
                        new ComponentParameterKey(componentName, ComponentParser.getInstance().getVersionNumber(), "header.4"),
                        "Allow, #Allow#"
                )
        );

        //MOCKS
        when(allowHeader.getName()).thenReturn("Allow");
        when(allowHeader.getIn()).thenReturn("header");
        when(operation.getParameters()).thenReturn(Collections.singletonList(allowHeader));

        //TESTS
        assertThat(ComponentParser.getInstance().getHeaders(componentName, partNames, operation)).isEqualTo(parameters);

    }

    @Test
    public void getHeadersWithNullValues() {
        String componentName = "AUTH.JSON.JSON";
        HashMap<String, String> partNames = new LinkedHashMap<>();
        List<ComponentParameter> parameters = new ArrayList<>();
        Operation operation = mock(Operation.class);

        partNames.put("security", null);
        partNames.put("request", "application/x-www-form-urlencoded");
        partNames.put("response", null);

        parameters.add(
                new ComponentParameter(
                        new ComponentParameterKey(componentName, ComponentParser.getInstance().getVersionNumber(), "header.1"),
                        "Content-Type, application/x-www-form-urlencoded"
                )
        );


        //TESTS
        assertThat(ComponentParser.getInstance().getHeaders(componentName, partNames, operation)).isEqualTo(parameters);

    }

    @Test
    public void buildName() {
        String operationId = "updatePet";
        HashMap<String, String> partNames = new LinkedHashMap<>();

        partNames.put("security", "petstore_auth");
        partNames.put("request", "application/x-www-form-urlencoded");
        partNames.put("response", "application/json");

        //TESTS
        assertThat(ComponentParser.getInstance().buildName(operationId, partNames)).isEqualTo("updatePet.petstore_auth.FORM.JSON");
    }

    @Test
    public void buildNameWithNullValues() {
        String operationId = "updatePet";
        HashMap<String, String> partNames = new LinkedHashMap<>();

        partNames.put("security", null);
        partNames.put("request", null);
        partNames.put("response", "application/json");

        //TESTS
        assertThat(ComponentParser.getInstance().buildName(operationId, partNames)).isEqualTo("updatePet._._.JSON");
    }

    @Test
    public void serializeContentNames() {
        String contentName = "application/json";

        //TESTS
        assertThat(ComponentParser.getInstance().serializeContentName(contentName)).isEqualTo("JSON");
    }

    @Test
    public void serializeUnknownContentNames() {
        String contentName = "unknown/unknown";

        //TESTS
        assertThat(ComponentParser.getInstance().serializeContentName(contentName)).isEqualTo("??");
    }

}
