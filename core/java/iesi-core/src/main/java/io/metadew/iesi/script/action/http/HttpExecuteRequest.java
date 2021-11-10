package io.metadew.iesi.script.action.http;

import io.metadew.iesi.component.http.*;
import io.metadew.iesi.connection.http.ProxyConnection;
import io.metadew.iesi.connection.http.request.HttpRequest;
import io.metadew.iesi.connection.http.request.HttpRequestBuilderException;
import io.metadew.iesi.connection.http.request.HttpRequestService;
import io.metadew.iesi.connection.http.response.HttpResponse;
import io.metadew.iesi.connection.http.response.HttpResponseService;
import io.metadew.iesi.datatypes.DataType;
import io.metadew.iesi.datatypes._null.Null;
import io.metadew.iesi.datatypes.array.Array;
import io.metadew.iesi.datatypes.dataset.implementation.inmemory.InMemoryDatasetImplementation;
import io.metadew.iesi.datatypes.dataset.implementation.inmemory.InMemoryDatasetImplementationService;
import io.metadew.iesi.datatypes.text.Text;
import io.metadew.iesi.metadata.configuration.connection.ConnectionConfiguration;
import io.metadew.iesi.metadata.definition.connection.key.ConnectionKey;
import io.metadew.iesi.script.action.ActionTypeExecution;
import io.metadew.iesi.script.execution.ActionExecution;
import io.metadew.iesi.script.execution.ActionPerformanceLogger;
import io.metadew.iesi.script.execution.ExecutionControl;
import io.metadew.iesi.script.execution.ScriptExecution;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.Header;

import java.io.IOException;
import java.net.URISyntaxException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;


@Log4j2
@Getter
public class HttpExecuteRequest extends ActionTypeExecution {

    private static final String ACTION_TYPE = "http.executeRequest";
    private static final String REQUEST_KEY = "request";
    private static final String REQUEST_VERSION = "requestVersion";
    private static final String BODY_KEY = "body";
    private static final String PROXY_KEY = "proxy";
    private static final String SET_DATASET_KEY = "setDataset";
    private static final String EXPECTED_STATUS_CODES_KEY = "expectedStatusCodes";
    private static final String HEADERS_KEY = "headers";
    private static final String QUERY_PARAMETERS_KEY = "queryParameters";

    private HttpRequest httpRequest;
    private InMemoryDatasetImplementation outputDataset;
    private ProxyConnection proxyConnection;
    private List<String> expectedStatusCodes;

    private static final Pattern INFORMATION_STATUS_CODE = Pattern.compile("1\\d\\d");
    private static final Pattern SUCCESS_STATUS_CODE = Pattern.compile("2\\d\\d");
    private static final Pattern REDIRECT_STATUS_CODE = Pattern.compile("3\\d\\d");
    @SuppressWarnings("unused")
    private static final Pattern SERVER_ERROR_STATUS_CODE = Pattern.compile("4\\d\\d");
    @SuppressWarnings("unused")
    private static final Pattern CLIENT_ERROR_STATUS_CODE = Pattern.compile("5\\d\\d");

    public HttpExecuteRequest(ExecutionControl executionControl,
                              ScriptExecution scriptExecution, ActionExecution actionExecution) {
        super(executionControl, scriptExecution, actionExecution);
    }

    public void prepareAction() throws HttpRequestBuilderException, URISyntaxException {
        Long componentVersion = convertHttpRequestVersion(getParameterResolvedValue(REQUEST_VERSION));
        HttpComponent httpComponent;
        if (componentVersion == null) {
            httpComponent = HttpComponentService.getInstance().getAndTrace(convertHttpRequestName(getParameterResolvedValue(REQUEST_KEY)), getActionExecution(), REQUEST_KEY);
        } else {
            httpComponent = HttpComponentService.getInstance().getAndTrace(convertHttpRequestName(getParameterResolvedValue(REQUEST_KEY)), getActionExecution(), REQUEST_KEY, componentVersion);
        }


        Optional<String> body = convertHttpRequestBody(getParameterResolvedValue(BODY_KEY));

        List<HttpHeader> headers = combineParameters(httpComponent.getHeaders(), convertHeaderParameters(getParameterResolvedValue(HEADERS_KEY)));
        List<HttpQueryParameter> queryParameters = combineParameters(httpComponent.getQueryParameters(), convertHttpQueryParameters(getParameterResolvedValue(QUERY_PARAMETERS_KEY)));

        httpComponent.setQueryParameters(queryParameters);
        httpComponent.setHeaders(headers);

        if (body.isPresent()) {
            getActionExecution().getActionControl().logOutput("request.body", body.get());
            httpRequest = HttpComponentService.getInstance().buildHttpRequest(
                    httpComponent,
                    body.get());
        } else {
            httpRequest = HttpComponentService.getInstance().buildHttpRequest(httpComponent);
        }
        getActionExecution().getActionControl().logOutput("request.uri", httpRequest.getHttpRequest().getURI().toString());
        getActionExecution().getActionControl().logOutput("request.method", httpRequest.getHttpRequest().getMethod());

        for (int i = 0; i < httpRequest.getHttpRequest().getAllHeaders().length; i++) {
            Header header = httpRequest.getHttpRequest().getAllHeaders()[i];
            getActionExecution().getActionControl().logOutput("request.header." + i, header.toString());
        }

        expectedStatusCodes = convertExpectStatusCodes(getParameterResolvedValue(EXPECTED_STATUS_CODES_KEY));
        proxyConnection = convertProxyName(getParameterResolvedValue(PROXY_KEY));
        outputDataset = convertOutputDatasetReferenceName(getParameterResolvedValue(SET_DATASET_KEY));

    }

    protected boolean executeAction() throws NoSuchAlgorithmException, IOException, KeyManagementException, InterruptedException {
        HttpResponse httpResponse;
        if (getProxyConnection().isPresent()) {
            httpResponse = HttpRequestService.getInstance().send(httpRequest, proxyConnection);
        } else {
            httpResponse = HttpRequestService.getInstance().send(httpRequest);
        }
        outputResponse(httpResponse);
        ActionPerformanceLogger.getInstance().log(getActionExecution(), "response", httpResponse.getRequestTimestamp(), httpResponse.getResponseTimestamp());
        checkStatusCode(httpResponse);
        return true;
    }

    @Override
    protected String getKeyword() {
        return ACTION_TYPE;
    }

    private <T extends HttpParameter> List<T> combineParameters(List<T> componentLevelParameters, List<T> actionLevelParameters) {
        List<T> additionalParameters = componentLevelParameters.stream()
                .filter(componentLevelHeader -> actionLevelParameters.stream()
                        .noneMatch(actionLevelHeader -> actionLevelHeader.getName().equals(componentLevelHeader.getName())))
                .collect(Collectors.toList());
        actionLevelParameters.addAll(additionalParameters);
        return actionLevelParameters;
    }


    private List<HttpHeader> convertHeaderParameters(DataType dataType) {
        if (dataType == null || dataType instanceof Null) {
            return new ArrayList<>();
        } else if (dataType instanceof Text) {
            List<String> tokens = Arrays.stream(dataType.toString().split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)", -1)).collect(Collectors.toList());
            return tokens
                    .stream().map(this::buildHttpHeader).collect(Collectors.toList());
        } else if (dataType instanceof InMemoryDatasetImplementation) {
            return InMemoryDatasetImplementationService
                    .getInstance()
                    .getDataItems((InMemoryDatasetImplementation) dataType, getExecutionControl()
                            .getExecutionRuntime()).entrySet().stream()
                    .map(dataItem -> new HttpHeader(dataItem.getKey(), dataItem.getValue().toString()))
                    .collect(
                            Collectors.toList()
                    );
        } else {
            log.warn(MessageFormat.format(getActionExecution().getAction().getType().concat(" does not accept {0} as type for headers"),
                    dataType.getClass()));
            return new ArrayList<>();
        }
    }

    private List<HttpQueryParameter> convertHttpQueryParameters(DataType dataType) {
        if (dataType == null || dataType instanceof Null) {
            return new ArrayList<>();
        } else if (dataType instanceof Text) {
            return Arrays.stream(dataType.toString().split(","))
                    .map(this::buildHttpQueryParameter).collect(Collectors.toList());
        } else if (dataType instanceof InMemoryDatasetImplementation) {
            return InMemoryDatasetImplementationService
                    .getInstance()
                    .getDataItems((InMemoryDatasetImplementation) dataType, getExecutionControl()
                            .getExecutionRuntime()).entrySet().stream()
                    .map(dataItem -> new HttpQueryParameter(dataItem.getKey(), dataItem.getValue().toString()))
                    .collect(
                            Collectors.toList()
                    );
        } else {
            log.warn(MessageFormat.format(getActionExecution().getAction().getType().concat(" does not accept {0} as type for queryParameters"),
                    dataType.getClass()));
            return new ArrayList<>();
        }
    }

    private HttpHeader buildHttpHeader(String header) {
        List<String> keyValue;
        String key;
        String value;

        if (!header.contains("=")) {
            throw new KeyValuePairException(String.format("The parameter %s should contain key value pair separated by the equals character < key=value >.", header));
        }

        keyValue = Arrays.stream(header.split("=", 2)).collect(Collectors.toList());
        if (keyValue.size() > 2) {
            throw new KeyValuePairException(String.format("The parameter %s should contain one key value pair, please remove additional separator character.", header));
        }

        key = keyValue.get(0);
        value = keyValue.get(1);

        if (!(value.startsWith("\"") && value.endsWith("\""))) {
            throw new QuoteCharException(String.format("The value %s is not provided correctly, please use quotes", value));
        }

        return new HttpHeader(key, StringUtils.substringBetween(value, "\"", "\""));
    }

    private HttpQueryParameter buildHttpQueryParameter(String queryParameter) {
        String[] keyValues;

        if (!queryParameter.contains("=")) {
            throw new KeyValuePairException(String.format("The parameter %s should contain key value pair separated by the equals character < key=value >.", queryParameter));
        }

        keyValues = queryParameter.split("=");
        if (keyValues.length > 2) {
            throw new KeyValuePairException(String.format("The parameter %s should contain one key value pair, please remove additional separator character.", queryParameter));
        }

        return new HttpQueryParameter(keyValues[0], keyValues[1]);
    }

    private List<String> convertExpectStatusCodes(DataType expectedStatusCodes) {
        if (expectedStatusCodes == null || expectedStatusCodes instanceof Null) {
            return null;
        }
        if (expectedStatusCodes instanceof Text) {
            return Arrays.stream(expectedStatusCodes.toString().split(","))
                    .map(String::trim)
                    .collect(Collectors.toList());
        } else if (expectedStatusCodes instanceof Array) {
            return ((Array) expectedStatusCodes).getList().stream()
                    .map(this::convertExpectedStatusCode)
                    .collect(Collectors.toList());
        } else {
            log.warn(MessageFormat.format(getActionExecution().getAction().getType() + " does not accept {0} as type for expectedStatusCode",
                    expectedStatusCodes.getClass()));
            return null;
        }
    }

    private String convertExpectedStatusCode(DataType expectedStatusCode) {
        if (expectedStatusCode instanceof Text) {
            return expectedStatusCode.toString();
        } else {
            log.warn(String.format("%s does not accept %s as type for expectedStatusCode",
                    getActionExecution().getAction().getType(),
                    expectedStatusCode.getClass()));
            return expectedStatusCode.toString();
        }
    }

    private ProxyConnection convertProxyName(DataType connectionName) {
        if (connectionName == null || connectionName instanceof Null) {
            return null;
        } else if (connectionName instanceof Text) {
            return ConnectionConfiguration.getInstance()
                    .get(new ConnectionKey(((Text) connectionName).getString(), getExecutionControl().getEnvName()))
                    .map(ProxyConnection::from)
                    .orElseThrow(() -> new RuntimeException(MessageFormat.format("Cannot find connection {0}", ((Text) connectionName).getString())));
        } else {
            throw new RuntimeException(MessageFormat.format("{0} does not accept {1} as type for proxy connection name",
                    getActionExecution().getAction().getType(), connectionName.getClass()));
        }
    }

    private void outputResponse(HttpResponse httpResponse) throws IOException {
        Optional<InMemoryDatasetImplementation> outputDataset = getOutputDataset();
        if (outputDataset.isPresent()) {
            if (!InMemoryDatasetImplementationService.getInstance().isEmpty(outputDataset.get())) {
                log.warn(String.format("Output dataset %s already contains data items. Clearing old data items before writing output", outputDataset.get()));
                InMemoryDatasetImplementationService.getInstance().clean(outputDataset.get(), getExecutionControl().getExecutionRuntime());
            }
            HttpResponseService.getInstance().writeToDataset(httpResponse, getOutputDataset().get(), getExecutionControl().getExecutionRuntime());
        }
        HttpResponseService.getInstance().traceOutput(httpResponse, getActionExecution().getActionControl());
    }

    private InMemoryDatasetImplementation convertOutputDatasetReferenceName(DataType outputDatasetReferenceName) {
        if (outputDatasetReferenceName == null || outputDatasetReferenceName instanceof Null) {
            return null;
        } else if (outputDatasetReferenceName instanceof Text) {
            return getExecutionControl().getExecutionRuntime()
                    .getDataset(((Text) outputDatasetReferenceName).getString())
                    .orElseThrow(() -> new RuntimeException(MessageFormat.format("No dataset found with name ''{0}''", ((Text) outputDatasetReferenceName).getString())));
        } else if (outputDatasetReferenceName instanceof InMemoryDatasetImplementation) {
            return (InMemoryDatasetImplementation) outputDatasetReferenceName;
        } else {
            log.warn(MessageFormat.format(getActionExecution().getAction().getType() + " does not accept {0} as type for OutputDatasetReferenceName",
                    outputDatasetReferenceName.getClass()));
            throw new RuntimeException(MessageFormat.format("Output dataset does not allow type ''{0}''", outputDatasetReferenceName.getClass()));
        }
    }

    private boolean convertSetRuntimeVariables(DataType setRuntimeVariables) {
        if (setRuntimeVariables == null) {
            return false;
        }
        if (setRuntimeVariables instanceof Text) {
            return setRuntimeVariables.toString().equalsIgnoreCase("y");
        } else {
            log.warn(MessageFormat.format(getActionExecution().getAction().getType() + " does not accept {0} as type for setRuntimeVariablesActionParameterOperation",
                    setRuntimeVariables.getClass()));
            return false;
        }
    }

    private Optional<String> convertHttpRequestBody(DataType httpRequestBody) {
        if (httpRequestBody == null) {
            return Optional.empty();
        }
        if (httpRequestBody instanceof Text) {
            return Optional.of(httpRequestBody.toString());
        } else {
            log.warn(MessageFormat.format(getActionExecution().getAction().getType() + " does not accept {0} as type for request body",
                    httpRequestBody.getClass()));
            return Optional.of(httpRequestBody.toString());
        }
    }

    private String convertHttpRequestName(DataType httpRequestName) {
        if (httpRequestName instanceof Text) {
            return ((Text) httpRequestName).getString();
        } else {
            log.warn(MessageFormat.format(getActionExecution().getAction().getType() + " does not accept {0} as type for request name",
                    httpRequestName.getClass()));
            return httpRequestName.toString();
        }
    }

    private Long convertHttpRequestVersion(DataType httpRequestVersion) {
        if (httpRequestVersion == null) {
            return null;
        }
        if (httpRequestVersion instanceof Text) {
            if (((Text) httpRequestVersion).getString().isEmpty()) {
                return null;
            }
            try {
                return Long.parseLong(((Text) httpRequestVersion).getString());
            } catch (NumberFormatException e) {
                throw new RuntimeException(String.format("Unable to parse the input value %s", ((Text) httpRequestVersion).getString()));
            }
        }
        throw new RuntimeException(MessageFormat.format(getActionExecution().getAction().getType() + " does not accept {0} as type for request name",
                httpRequestVersion.getClass()));
    }

    private void checkStatusCode(HttpResponse httpResponse) {
        if (getExpectedStatusCodes().isPresent()) {
            if (expectedStatusCodes.contains(String.valueOf(httpResponse.getStatusLine().getStatusCode()))) {
                getActionExecution().getActionControl().increaseSuccessCount();
            } else {
                getActionExecution().getActionControl().logOutput("action.error", MessageFormat.format("Status code of response {0} is not member of expected status codes {1}",
                        httpResponse.getStatusLine().getStatusCode(), expectedStatusCodes));
                log.warn(MessageFormat.format("Status code of response {0} is not member of expected status codes {1}",
                        httpResponse.getStatusLine().getStatusCode(), expectedStatusCodes));
                getActionExecution().getActionControl().increaseErrorCount();
            }
        } else {
            checkStatusCodeDefault(httpResponse);
        }
    }

    private void checkStatusCodeDefault(HttpResponse httpResponse) {
        if (SUCCESS_STATUS_CODE.matcher(Integer.toString(httpResponse.getStatusLine().getStatusCode())).find()) {
            getActionExecution().getActionControl().increaseSuccessCount();
        } else if (INFORMATION_STATUS_CODE.matcher(Integer.toString(httpResponse.getStatusLine().getStatusCode())).find()) {
            getActionExecution().getActionControl().increaseSuccessCount();
        } else if (REDIRECT_STATUS_CODE.matcher(Integer.toString(httpResponse.getStatusLine().getStatusCode())).find()) {
            getActionExecution().getActionControl().increaseSuccessCount();
        } else {
            getActionExecution().getActionControl().logOutput("action.error", MessageFormat.format("Status code of response {0} is not member of success status codes (1XX, 2XX, 3XX).",
                    httpResponse.getStatusLine().getStatusCode()));
            log.warn(MessageFormat.format("Status code of response {0} is not member of success status codes (1XX, 2XX, 3XX).",
                    httpResponse.getStatusLine().getStatusCode()));
            getActionExecution().getActionControl().increaseErrorCount();
        }
    }

    protected Optional<InMemoryDatasetImplementation> getOutputDataset() {
        return Optional.ofNullable(outputDataset);
    }

    protected Optional<List<String>> getExpectedStatusCodes() {
        return Optional.ofNullable(expectedStatusCodes);
    }

    protected Optional<ProxyConnection> getProxyConnection() {
        return Optional.ofNullable(proxyConnection);
    }

}