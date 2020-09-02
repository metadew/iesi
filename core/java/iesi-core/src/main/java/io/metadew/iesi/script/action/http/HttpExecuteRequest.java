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
import io.metadew.iesi.datatypes.text.Text;
import io.metadew.iesi.metadata.configuration.connection.ConnectionConfiguration;
import io.metadew.iesi.metadata.configuration.exception.MetadataDoesNotExistException;
import io.metadew.iesi.metadata.definition.action.ActionParameter;
import io.metadew.iesi.metadata.definition.connection.key.ConnectionKey;
import io.metadew.iesi.metadata.definition.dataset.InMemoryDatasetImplementation;
import io.metadew.iesi.script.action.ActionTypeExecution;
import io.metadew.iesi.script.execution.ActionExecution;
import io.metadew.iesi.script.execution.ActionPerformanceLogger;
import io.metadew.iesi.script.execution.ExecutionControl;
import io.metadew.iesi.script.execution.ScriptExecution;
import io.metadew.iesi.script.operation.ActionParameterOperation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

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


public class HttpExecuteRequest extends ActionTypeExecution {

    private static Logger LOGGER = LogManager.getLogger();
    // Parameters
    private static final String requestKey = "request";
    private static final String bodyKey = "body";
    private static final String proxyKey = "proxy";
    private static final String setDatasetKey = "setDataset";
    private static final String expectedStatusCodesKey = "expectedStatusCodes";

    private HttpRequest httpRequest;
    private InMemoryDatasetImplementation outputDataset;
    private ProxyConnection proxyConnection;
    private List<String> expectedStatusCodes;

    private final Pattern INFORMATION_STATUS_CODE = Pattern.compile("1\\d\\d");
    private final Pattern SUCCESS_STATUS_CODE = Pattern.compile("2\\d\\d");
    private final Pattern REDIRECT_STATUS_CODE = Pattern.compile("3\\d\\d");
    @SuppressWarnings("unused")
    private final Pattern SERVER_ERROR_STATUS_CODE = Pattern.compile("4\\d\\d");
    @SuppressWarnings("unused")
    private final Pattern CLIENT_ERROR_STATUS_CODE = Pattern.compile("5\\d\\d");

    public HttpExecuteRequest(ExecutionControl executionControl,
                              ScriptExecution scriptExecution, ActionExecution actionExecution) {
        super(executionControl, scriptExecution, actionExecution);
    }

    public void prepare() throws URISyntaxException, HttpRequestBuilderException, IOException, MetadataDoesNotExistException {
        // Reset Parameters
        ActionParameterOperation requestNameActionParameterOperation = new ActionParameterOperation(getExecutionControl(), getActionExecution(), getActionExecution().getAction().getType(), requestKey);
        ActionParameterOperation requestBodyActionParameterOperation = new ActionParameterOperation(getExecutionControl(), getActionExecution(), getActionExecution().getAction().getType(), bodyKey);
        ActionParameterOperation setDatasetActionParameterOperation = new ActionParameterOperation(getExecutionControl(), getActionExecution(), getActionExecution().getAction().getType(), setDatasetKey);
        ActionParameterOperation expectedStatusCodesActionParameterOperation = new ActionParameterOperation(getExecutionControl(), getActionExecution(), getActionExecution().getAction().getType(), expectedStatusCodesKey);
        ActionParameterOperation proxyActionParameterOperation = new ActionParameterOperation(getExecutionControl(), getActionExecution(), getActionExecution().getAction().getType(), proxyKey);

        // Get Parameters
        for (ActionParameter actionParameter : getActionExecution().getAction().getParameters()) {
            if (actionParameter.getMetadataKey().getParameterName().equalsIgnoreCase(requestKey)) {
                requestNameActionParameterOperation.setInputValue(actionParameter.getValue(), getExecutionControl().getExecutionRuntime());
            } else if (actionParameter.getMetadataKey().getParameterName().equalsIgnoreCase(bodyKey)) {
                requestBodyActionParameterOperation.setInputValue(actionParameter.getValue(), getExecutionControl().getExecutionRuntime());
            } else if (actionParameter.getMetadataKey().getParameterName().equalsIgnoreCase(setDatasetKey)) {
                setDatasetActionParameterOperation.setInputValue(actionParameter.getValue(), getExecutionControl().getExecutionRuntime());
            } else if (actionParameter.getMetadataKey().getParameterName().equalsIgnoreCase(expectedStatusCodesKey)) {
                expectedStatusCodesActionParameterOperation.setInputValue(actionParameter.getValue(), getExecutionControl().getExecutionRuntime());
            } else if (actionParameter.getMetadataKey().getParameterName().equalsIgnoreCase(proxyKey)) {
                proxyActionParameterOperation.setInputValue(actionParameter.getValue(), getExecutionControl().getExecutionRuntime());
            }
        }

        // Create parameter list
        getActionParameterOperationMap().put(requestKey, requestNameActionParameterOperation);
        getActionParameterOperationMap().put(bodyKey, requestBodyActionParameterOperation);
        getActionParameterOperationMap().put(setDatasetKey, setDatasetActionParameterOperation);
        getActionParameterOperationMap().put(expectedStatusCodesKey, expectedStatusCodesActionParameterOperation);
        getActionParameterOperationMap().put(proxyKey, proxyActionParameterOperation);

        if (convertHttpRequestBody(requestBodyActionParameterOperation.getValue()).isPresent()) {
            httpRequest = HttpComponentService.getInstance().buildHttpRequest(
                    HttpComponentService.getInstance().get(convertHttpRequestName(requestNameActionParameterOperation.getValue()), getActionExecution()),
                    convertHttpRequestBody(requestBodyActionParameterOperation.getValue()).get());
        } else {
            httpRequest = HttpComponentService.getInstance().buildHttpRequest(
                    HttpComponentService.getInstance().get(convertHttpRequestName(requestNameActionParameterOperation.getValue()), getActionExecution()));
        }

        expectedStatusCodes = convertExpectStatusCodes(expectedStatusCodesActionParameterOperation.getValue());
        proxyConnection = convertProxyName(proxyActionParameterOperation.getValue());
        outputDataset = convertOutputDatasetReferenceName(setDatasetActionParameterOperation.getValue());
//        if (getOutputDataset().isPresent()) {
//            List<String> labels = new ArrayList<>(outputDataset.getLabels());
//            labels.add("typed");
//            rawOutputDataset = (KeyValueDataset) DatasetHandler.getInstance().getByNameAndLabels(outputDataset.getName(), labels, getExecutionControl().getExecutionRuntime());
//        }

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
        //writeResponseToOutputDataset(httpResponse);
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
            LOGGER.warn(MessageFormat.format(getActionExecution().getAction().getType() + " does not accept {0} as type for expectedStatusCode",
                    expectedStatusCodes.getClass()));
            return null;
        }
    }

    private String convertExpectedStatusCode(DataType expectedStatusCode) {
        if (expectedStatusCode instanceof Text) {
            return expectedStatusCode.toString();
        } else {
            LOGGER.warn(MessageFormat.format(getActionExecution().getAction().getType() + " does not accept {0} as type for expectedStatusCode",
                    expectedStatusCode.getClass()));
            return expectedStatusCode.toString();
        }
    }

    private ProxyConnection convertProxyName(DataType connectionName) {
        if (connectionName == null) {
            return null;
        } else if (connectionName instanceof Text) {
            return proxyConnection = ConnectionConfiguration.getInstance()
                    .get(new ConnectionKey(((Text) connectionName).getString(), getExecutionControl().getEnvName()))
                    .map(ProxyConnection::from)
                    .orElseThrow(() -> new RuntimeException(MessageFormat.format("Cannot find connection {0}", ((Text) connectionName).getString())));
        } else {
            throw new RuntimeException(MessageFormat.format("{0} does not accept {1} as type for proxy connection name",
                    getActionExecution().getAction().getType(), connectionName.getClass()));
        }
    }

    private void outputResponse(HttpResponse httpResponse) throws IOException {
        if (getOutputDataset().isPresent()) {
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
            LOGGER.warn(MessageFormat.format(getActionExecution().getAction().getType() + " does not accept {0} as type for OutputDatasetReferenceName",
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
            LOGGER.warn(MessageFormat.format(getActionExecution().getAction().getType() + " does not accept {0} as type for setRuntimeVariablesActionParameterOperation",
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
            LOGGER.warn(MessageFormat.format(getActionExecution().getAction().getType() + " does not accept {0} as type for request body",
                    httpRequestBody.getClass()));
            return Optional.of(httpRequestBody.toString());
        }
    }

    private String convertHttpRequestName(DataType httpRequestName) {
        if (httpRequestName instanceof Text) {
            return ((Text) httpRequestName).getString();
        } else {
            LOGGER.warn(MessageFormat.format(getActionExecution().getAction().getType() + " does not accept {0} as type for request name",
                    httpRequestName.getClass()));
            return httpRequestName.toString();
        }
    }

    private void checkStatusCode(HttpResponse httpResponse) {
        if (getExpectedStatusCodes().isPresent()) {
            if (expectedStatusCodes.contains(String.valueOf(httpResponse.getStatusLine().getStatusCode()))) {
                getActionExecution().getActionControl().increaseSuccessCount();
            } else {
                LOGGER.warn(MessageFormat.format("Status code of response {0} is not member of expected status codes {1}",
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
            LOGGER.warn((MessageFormat.format("Status code of response {0} is not member of success status codes (1XX, 2XX, 3XX).",
                    httpResponse.getStatusLine().getStatusCode())));
            getActionExecution().getActionControl().increaseErrorCount();
        }
    }

    private Optional<InMemoryDatasetImplementation> getOutputDataset() {
        return Optional.ofNullable(outputDataset);
    }

    private Optional<List<String>> getExpectedStatusCodes() {
        return Optional.ofNullable(expectedStatusCodes);
    }

    private Optional<ProxyConnection> getProxyConnection() {
        return Optional.ofNullable(proxyConnection);
    }

}