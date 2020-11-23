package io.metadew.iesi.script.action.http;

import io.metadew.iesi.component.http.HttpComponentService;
import io.metadew.iesi.connection.http.ProxyConnection;
import io.metadew.iesi.connection.http.request.HttpRequest;
import io.metadew.iesi.connection.http.request.HttpRequestBuilderException;
import io.metadew.iesi.connection.http.request.HttpRequestService;
import io.metadew.iesi.connection.http.response.HttpResponse;
import io.metadew.iesi.connection.http.response.HttpResponseService;
import io.metadew.iesi.datatypes.DataType;
import io.metadew.iesi.datatypes.array.Array;
import io.metadew.iesi.datatypes.dataset.keyvalue.KeyValueDataset;
import io.metadew.iesi.datatypes.dataset.keyvalue.KeyValueDatasetService;
import io.metadew.iesi.datatypes.text.Text;
import io.metadew.iesi.metadata.configuration.connection.ConnectionConfiguration;
import io.metadew.iesi.metadata.configuration.exception.MetadataDoesNotExistException;
import io.metadew.iesi.metadata.definition.action.ActionParameter;
import io.metadew.iesi.metadata.definition.connection.key.ConnectionKey;
import io.metadew.iesi.script.action.ActionTypeExecution;
import io.metadew.iesi.script.execution.ActionExecution;
import io.metadew.iesi.script.execution.ActionPerformanceLogger;
import io.metadew.iesi.script.execution.ExecutionControl;
import io.metadew.iesi.script.execution.ScriptExecution;
import io.metadew.iesi.script.operation.ActionParameterOperation;
import lombok.extern.log4j.Log4j2;
import org.apache.http.Header;

import java.io.IOException;
import java.net.URISyntaxException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.text.MessageFormat;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;
import java.util.stream.Collectors;


@Log4j2
public class HttpExecuteRequest extends ActionTypeExecution {

    private static final String REQUEST_KEY = "request";
    private static final String BODY_KEY = "body";
    private static final String PROXY_KEY = "proxy";
    private static final String SET_DATASET_KEY = "setDataset";
    private static final String EXPECTED_STATUS_CODES_KEY = "expectedStatusCodes";

    private HttpRequest httpRequest;
    private KeyValueDataset outputDataset;
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

    public void prepare() throws URISyntaxException, HttpRequestBuilderException, IOException, MetadataDoesNotExistException {
        // Reset Parameters
        ActionParameterOperation requestNameActionParameterOperation = new ActionParameterOperation(getExecutionControl(), getActionExecution(), getActionExecution().getAction().getType(), REQUEST_KEY);
        ActionParameterOperation requestBodyActionParameterOperation = new ActionParameterOperation(getExecutionControl(), getActionExecution(), getActionExecution().getAction().getType(), BODY_KEY);
        ActionParameterOperation setDatasetActionParameterOperation = new ActionParameterOperation(getExecutionControl(), getActionExecution(), getActionExecution().getAction().getType(), SET_DATASET_KEY);
        ActionParameterOperation expectedStatusCodesActionParameterOperation = new ActionParameterOperation(getExecutionControl(), getActionExecution(), getActionExecution().getAction().getType(), EXPECTED_STATUS_CODES_KEY);
        ActionParameterOperation proxyActionParameterOperation = new ActionParameterOperation(getExecutionControl(), getActionExecution(), getActionExecution().getAction().getType(), PROXY_KEY);

        // Get Parameters
        for (ActionParameter actionParameter : getActionExecution().getAction().getParameters()) {
            if (actionParameter.getMetadataKey().getParameterName().equalsIgnoreCase(REQUEST_KEY)) {
                requestNameActionParameterOperation.setInputValue(actionParameter.getValue(), getExecutionControl().getExecutionRuntime());
            } else if (actionParameter.getMetadataKey().getParameterName().equalsIgnoreCase(BODY_KEY)) {
                requestBodyActionParameterOperation.setInputValue(actionParameter.getValue(), getExecutionControl().getExecutionRuntime());
            } else if (actionParameter.getMetadataKey().getParameterName().equalsIgnoreCase(SET_DATASET_KEY)) {
                setDatasetActionParameterOperation.setInputValue(actionParameter.getValue(), getExecutionControl().getExecutionRuntime());
            } else if (actionParameter.getMetadataKey().getParameterName().equalsIgnoreCase(EXPECTED_STATUS_CODES_KEY)) {
                expectedStatusCodesActionParameterOperation.setInputValue(actionParameter.getValue(), getExecutionControl().getExecutionRuntime());
            } else if (actionParameter.getMetadataKey().getParameterName().equalsIgnoreCase(PROXY_KEY)) {
                proxyActionParameterOperation.setInputValue(actionParameter.getValue(), getExecutionControl().getExecutionRuntime());
            }
        }

        // Create parameter list
        getActionParameterOperationMap().put(REQUEST_KEY, requestNameActionParameterOperation);
        getActionParameterOperationMap().put(BODY_KEY, requestBodyActionParameterOperation);
        getActionParameterOperationMap().put(SET_DATASET_KEY, setDatasetActionParameterOperation);
        getActionParameterOperationMap().put(EXPECTED_STATUS_CODES_KEY, expectedStatusCodesActionParameterOperation);
        getActionParameterOperationMap().put(PROXY_KEY, proxyActionParameterOperation);

        Optional<String> body = convertHttpRequestBody(requestBodyActionParameterOperation.getValue());

        if (body.isPresent()) {
            getActionExecution().getActionControl().logOutput("request.body", body.get());
            httpRequest = HttpComponentService.getInstance().buildHttpRequest(
                    HttpComponentService.getInstance().getAndTrace(convertHttpRequestName(requestNameActionParameterOperation.getValue()), getActionExecution(), REQUEST_KEY),
                    body.get());
        } else {
            httpRequest = HttpComponentService.getInstance().buildHttpRequest(
                    HttpComponentService.getInstance().getAndTrace(convertHttpRequestName(requestNameActionParameterOperation.getValue()), getActionExecution(), REQUEST_KEY));
        }
        getActionExecution().getActionControl().logOutput("request.uri", httpRequest.getHttpRequest().getURI().toString());
        getActionExecution().getActionControl().logOutput("request.method", httpRequest.getHttpRequest().getMethod());

        for (int i = 0; i < httpRequest.getHttpRequest().getAllHeaders().length; i++) {
            Header header = httpRequest.getHttpRequest().getAllHeaders()[i];
            getActionExecution().getActionControl().logOutput("request.header." + i, header.toString());
        }

        expectedStatusCodes = convertExpectStatusCodes(expectedStatusCodesActionParameterOperation.getValue());
        proxyConnection = convertProxyName(proxyActionParameterOperation.getValue());
        outputDataset = convertOutputDatasetReferenceName(setDatasetActionParameterOperation.getValue());

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
        Optional<KeyValueDataset> outputDataset = getOutputDataset();
        if (outputDataset.isPresent()) {
            if (!KeyValueDatasetService.getInstance().isEmpty(outputDataset.get())) {
                log.warn(String.format("Output dataset %s already contains data items. Clearing old data items before writing output", outputDataset.get()));
                KeyValueDatasetService.getInstance().clean(outputDataset.get(), getExecutionControl().getExecutionRuntime());
            }
            HttpResponseService.getInstance().writeToDataset(httpResponse, outputDataset.get(), getExecutionControl().getExecutionRuntime());
        }
        HttpResponseService.getInstance().traceOutput(httpResponse, getActionExecution().getActionControl());

    }

    private KeyValueDataset convertOutputDatasetReferenceName(DataType outputDatasetReferenceName) {
        if (outputDatasetReferenceName == null) {
            return null;
        } else if (outputDatasetReferenceName instanceof Text) {
            return getExecutionControl().getExecutionRuntime()
                    .getDataset(((Text) outputDatasetReferenceName).getString())
                    .map(dataset -> (KeyValueDataset) dataset)
                    .orElseThrow(() -> new RuntimeException(MessageFormat.format("No dataset found with name ''{0}''", ((Text) outputDatasetReferenceName).getString())));
        } else if (outputDatasetReferenceName instanceof KeyValueDataset) {
            return (KeyValueDataset) outputDatasetReferenceName;
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

    private Optional<KeyValueDataset> getOutputDataset() {
        return Optional.ofNullable(outputDataset);
    }

    private Optional<List<String>> getExpectedStatusCodes() {
        return Optional.ofNullable(expectedStatusCodes);
    }

    private Optional<ProxyConnection> getProxyConnection() {
        return Optional.ofNullable(proxyConnection);
    }

}