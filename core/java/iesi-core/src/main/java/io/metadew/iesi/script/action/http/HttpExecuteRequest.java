package io.metadew.iesi.script.action.http;

import io.metadew.iesi.component.http.HttpComponent;
import io.metadew.iesi.component.http.HttpComponentService;
import io.metadew.iesi.component.http.HttpHeader;
import io.metadew.iesi.component.http.HttpQueryParameter;
import io.metadew.iesi.connection.http.ProxyConnection;
import io.metadew.iesi.connection.http.request.HttpRequest;
import io.metadew.iesi.connection.http.request.HttpRequestBuilderException;
import io.metadew.iesi.connection.http.request.HttpRequestService;
import io.metadew.iesi.connection.http.response.HttpResponse;
import io.metadew.iesi.connection.http.response.HttpResponseService;
import io.metadew.iesi.datatypes.DataType;
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
import org.apache.http.Header;

import java.io.IOException;
import java.net.URISyntaxException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.text.MessageFormat;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;


@Log4j2
@Getter
public class HttpExecuteRequest extends ActionTypeExecution {

    private static final String ACTION_TYPE = "http.executeRequest";
    private static final String REQUEST_KEY = "request";
    private static final String BODY_KEY = "body";
    private static final String PROXY_KEY = "proxy";
    private static final String SET_DATASET_KEY = "setDataset";
    private static final String EXPECTED_STATUS_CODES_KEY = "expectedStatusCodes";
    private static final String HEADERS_KEY = "headers";
    private static final String QUERYPARAMS_KEY = "queryParams";

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

    public void prepare() throws URISyntaxException, HttpRequestBuilderException {

//        // Reset Parameters
//        ActionParameterOperation requestNameActionParameterOperation = new ActionParameterOperation(getExecutionControl(), getActionExecution(), getActionExecution().getAction().getType(), REQUEST_KEY);
//        ActionParameterOperation requestBodyActionParameterOperation = new ActionParameterOperation(getExecutionControl(), getActionExecution(), getActionExecution().getAction().getType(), BODY_KEY);
//        ActionParameterOperation setDatasetActionParameterOperation = new ActionParameterOperation(getExecutionControl(), getActionExecution(), getActionExecution().getAction().getType(), SET_DATASET_KEY);
//        ActionParameterOperation expectedStatusCodesActionParameterOperation = new ActionParameterOperation(getExecutionControl(), getActionExecution(), getActionExecution().getAction().getType(), EXPECTED_STATUS_CODES_KEY);
//        ActionParameterOperation proxyActionParameterOperation = new ActionParameterOperation(getExecutionControl(), getActionExecution(), getActionExecution().getAction().getType(), PROXY_KEY);
//
//        // Get Parameters
//        for (ActionParameter actionParameter : getActionExecution().getAction().getParameters()) {
//            if (actionParameter.getMetadataKey().getParameterName().equalsIgnoreCase(REQUEST_KEY)) {
//                requestNameActionParameterOperation.setInputValue(actionParameter.getValue(), getExecutionControl().getExecutionRuntime());
//            } else if (actionParameter.getMetadataKey().getParameterName().equalsIgnoreCase(BODY_KEY)) {
//                requestBodyActionParameterOperation.setInputValue(actionParameter.getValue(), getExecutionControl().getExecutionRuntime());
//            } else if (actionParameter.getMetadataKey().getParameterName().equalsIgnoreCase(SET_DATASET_KEY)) {
//                setDatasetActionParameterOperation.setInputValue(actionParameter.getValue(), getExecutionControl().getExecutionRuntime());
//            } else if (actionParameter.getMetadataKey().getParameterName().equalsIgnoreCase(EXPECTED_STATUS_CODES_KEY)) {
//                expectedStatusCodesActionParameterOperation.setInputValue(actionParameter.getValue(), getExecutionControl().getExecutionRuntime());
//            } else if (actionParameter.getMetadataKey().getParameterName().equalsIgnoreCase(PROXY_KEY)) {
//                proxyActionParameterOperation.setInputValue(actionParameter.getValue(), getExecutionControl().getExecutionRuntime());
//            }
//        }
//
//        // Create parameter list
//        getActionParameterOperationMap().put(REQUEST_KEY, requestNameActionParameterOperation);
//        getActionParameterOperationMap().put(BODY_KEY, requestBodyActionParameterOperation);
//        getActionParameterOperationMap().put(SET_DATASET_KEY, setDatasetActionParameterOperation);
//        getActionParameterOperationMap().put(EXPECTED_STATUS_CODES_KEY, expectedStatusCodesActionParameterOperation);
//        getActionParameterOperationMap().put(PROXY_KEY, proxyActionParameterOperation);
        HttpComponent httpComponent =  HttpComponentService.getInstance().getAndTrace(convertHttpRequestName(getParameterResolvedValue(REQUEST_KEY)),getActionExecution(), REQUEST_KEY);

        Optional<String> body = convertHttpRequestBody(getParameterResolvedValue(BODY_KEY));
        List<HttpHeader> headers = convertHeaders(getParameterResolvedValue(HEADERS_KEY));
        List<HttpQueryParameter> queryParameters = convertQueryParams(getParameterResolvedValue(QUERYPARAMS_KEY));


        if (!headers.isEmpty()) {
            httpComponent.setHeaders(headers);
        }
        if (!queryParameters.isEmpty()) {
            httpComponent.setQueryParameters(queryParameters);
        }


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

    private List<HttpHeader> convertHeaders(DataType dataType) {
        Stream<Optional<HttpHeader>> stream;
        if (dataType == null) {
            return new ArrayList<>();
        } else if (dataType instanceof Text){
            stream = getParams(dataType.toString())
                    .map(text -> getHeader(buildParam(text)));
        } else if (dataType instanceof InMemoryDatasetImplementation) {
            stream = getDataItems(dataType).entrySet().stream()
                    .map(dataItem -> getHeader(buildParam(dataItem.getKey(), dataItem.getValue().toString())));
        }
        else {
            log.warn(MessageFormat.format(getActionExecution().getAction().getType().concat(" does not accept {0} as type for expectedStatusCode"),
                    dataType.getClass()));
            return new ArrayList<>();
        }

        return stream.filter(Optional::isPresent).map(Optional::get).collect(Collectors.toList());
    }

    private List<HttpQueryParameter> convertQueryParams(DataType dataType) {
        Stream<Optional<HttpQueryParameter>> stream;
        if (dataType == null) {
            return new ArrayList<>();
        } else if (dataType instanceof Text){
            stream = getParams(dataType.toString())
                    .map(text -> getQueryparam(buildParam(text)));
        } else if (dataType instanceof InMemoryDatasetImplementation) {
            stream = getDataItems(dataType).entrySet().stream()
                    .map(dataItem -> getQueryparam(buildParam(dataItem.getKey(), dataItem.getValue().toString())));
        }
        else {
            log.warn(MessageFormat.format(getActionExecution().getAction().getType().concat(" does not accept {0} as type for expectedStatusCode"),
                    dataType.getClass()));
            return new ArrayList<>();
        }
        return stream.filter(Optional::isPresent).map(Optional::get).collect(Collectors.toList());
    }

    private Stream<String> getParams(String paramsStr) {
        List<String> params = new ArrayList<>(Arrays.asList(paramsStr.split(",")));
        return params.stream();
    }

    private Map<String, DataType> getDataItems(DataType dataType) {
        return InMemoryDatasetImplementationService
                .getInstance()
                .getDataItems((InMemoryDatasetImplementation) dataType, getExecutionControl()
                        .getExecutionRuntime());
    }


    private Optional<HttpHeader> getHeader(AbstractMap.SimpleEntry<String, String> paramEntry) {
        if (paramEntry != null) {
            return Optional.of(new HttpHeader(paramEntry.getKey(), paramEntry.getValue()));
        }
        return Optional.empty();
    }

    private Optional<HttpQueryParameter> getQueryparam(AbstractMap.SimpleEntry<String, String> paramEntry) {
        if (paramEntry != null) {
            return Optional.of(new HttpQueryParameter(paramEntry.getKey(), paramEntry.getValue()));
        }
        return Optional.empty();
    }


    private AbstractMap.SimpleEntry<String, String> buildParam(String param) {
        List<String> keyValues;
        String key;
        String value;
        if (!param.contains("=")) {
            log.warn(String.format("The parameter %s should contain key value pair separated by the equals character < key=value >, ignored", param));
            return null;
        }

        keyValues = new ArrayList<>(Arrays.asList(param.split("=")));
        if (keyValues.size() > 2) {
            log.warn(String.format("The parameter %s should contain one key value pair, please remove additional separator character, ignored", param));
            return null;
        }

        key = keyValues.get(0);
        value = keyValues.get(1);
        return new AbstractMap.SimpleEntry<>(key, value);
    }

    private AbstractMap.SimpleEntry<String, String> buildParam(String key, String value) {
        return new AbstractMap.SimpleEntry<>(key, value);
    }

    private List<String> convertExpectStatusCodes(DataType expectedStatusCodes) {
        if (expectedStatusCodes == null) {
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
        if (connectionName == null) {
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
        if (outputDatasetReferenceName == null) {
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