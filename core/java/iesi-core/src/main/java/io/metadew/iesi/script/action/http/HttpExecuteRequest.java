package io.metadew.iesi.script.action.http;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeType;
import io.metadew.iesi.connection.http.ProxyConnection;
import io.metadew.iesi.connection.http.request.HttpRequest;
import io.metadew.iesi.connection.http.request.HttpRequestBuilder;
import io.metadew.iesi.connection.http.request.HttpRequestBuilderException;
import io.metadew.iesi.connection.http.request.HttpRequestService;
import io.metadew.iesi.connection.http.response.HttpResponse;
import io.metadew.iesi.connection.http.response.HttpResponseService;
import io.metadew.iesi.datatypes.DataType;
import io.metadew.iesi.datatypes.array.Array;
import io.metadew.iesi.datatypes.dataset.keyvalue.KeyValueDataset;
import io.metadew.iesi.datatypes.text.Text;
import io.metadew.iesi.metadata.configuration.connection.ConnectionConfiguration;
import io.metadew.iesi.metadata.configuration.exception.MetadataDoesNotExistException;
import io.metadew.iesi.metadata.definition.HttpRequestComponent;
import io.metadew.iesi.metadata.definition.action.ActionParameter;
import io.metadew.iesi.metadata.definition.connection.key.ConnectionKey;
import io.metadew.iesi.metadata.service.HttpRequestComponentService;
import io.metadew.iesi.script.execution.ActionExecution;
import io.metadew.iesi.script.execution.ExecutionControl;
import io.metadew.iesi.script.execution.ScriptExecution;
import io.metadew.iesi.script.operation.ActionParameterOperation;
import org.apache.http.entity.ContentType;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URISyntaxException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.text.MessageFormat;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;


public class HttpExecuteRequest {

    private static Logger LOGGER = LogManager.getLogger();

    private ActionExecution actionExecution;
    private ExecutionControl executionControl;
    // Parameters
    private static final String typeKey = "type";
    private static final String requestKey = "request";
    private static final String bodyKey = "body";
    private static final String proxyKey = "proxy";
    private static final String setRuntimeVariablesKey = "setRuntimeVariables";
    private static final String setDatasetKey = "setDataset";
    private static final String expectedStatusCodesKey = "expectedStatusCodes";

    private HashMap<String, ActionParameterOperation> actionParameterOperationMap;

    private HttpRequest httpRequest;
    private boolean setRuntimeVariables;
    private KeyValueDataset outputDataset;
    private ProxyConnection proxyConnection;
    private KeyValueDataset rawOutputDataset;
    private List<String> expectedStatusCodes;

    private final Pattern INFORMATION_STATUS_CODE = Pattern.compile("1\\d\\d");
    private final Pattern SUCCESS_STATUS_CODE = Pattern.compile("2\\d\\d");
    private final Pattern REDIRECT_STATUS_CODE = Pattern.compile("3\\d\\d");
    @SuppressWarnings("unused")
    private final Pattern SERVER_ERROR_STATUS_CODE = Pattern.compile("4\\d\\d");
    @SuppressWarnings("unused")
    private final Pattern CLIENT_ERROR_STATUS_CODE = Pattern.compile("5\\d\\d");

    // Constructors
    public HttpExecuteRequest() {

    }

    public HttpExecuteRequest(ExecutionControl executionControl,
                              ScriptExecution scriptExecution, ActionExecution actionExecution) {
        this.executionControl = executionControl;
        this.actionExecution = actionExecution;
        this.actionParameterOperationMap = new HashMap<>();
    }

    public void prepare() throws URISyntaxException, HttpRequestBuilderException, IOException, MetadataDoesNotExistException {
        // Reset Parameters
        ActionParameterOperation requestTypeActionParameterOperation = new ActionParameterOperation(executionControl, actionExecution, actionExecution.getAction().getType(), typeKey);
        ActionParameterOperation requestNameActionParameterOperation = new ActionParameterOperation(executionControl, actionExecution, actionExecution.getAction().getType(), requestKey);
        ActionParameterOperation requestBodyActionParameterOperation = new ActionParameterOperation(executionControl, actionExecution, actionExecution.getAction().getType(), bodyKey);
        ActionParameterOperation setRuntimeVariablesActionParameterOperation = new ActionParameterOperation(executionControl, actionExecution, actionExecution.getAction().getType(), setRuntimeVariablesKey);
        ActionParameterOperation setDatasetActionParameterOperation = new ActionParameterOperation(executionControl, actionExecution, actionExecution.getAction().getType(), setDatasetKey);
        ActionParameterOperation expectedStatusCodesActionParameterOperation = new ActionParameterOperation(executionControl, actionExecution, actionExecution.getAction().getType(), expectedStatusCodesKey);
        ActionParameterOperation proxyActionParameterOperation = new ActionParameterOperation(executionControl, actionExecution, actionExecution.getAction().getType(), proxyKey);

        // Get Parameters
        for (ActionParameter actionParameter : actionExecution.getAction().getParameters()) {
            if (actionParameter.getMetadataKey().getParameterName().equalsIgnoreCase(requestKey)) {
                requestNameActionParameterOperation.setInputValue(actionParameter.getValue(), executionControl.getExecutionRuntime());
            } else if (actionParameter.getMetadataKey().getParameterName().equalsIgnoreCase(typeKey)) {
                requestTypeActionParameterOperation.setInputValue(actionParameter.getValue(), executionControl.getExecutionRuntime());
            } else if (actionParameter.getMetadataKey().getParameterName().equalsIgnoreCase(bodyKey)) {
                requestBodyActionParameterOperation.setInputValue(actionParameter.getValue(), executionControl.getExecutionRuntime());
            } else if (actionParameter.getMetadataKey().getParameterName().equalsIgnoreCase(setRuntimeVariablesKey)) {
                setRuntimeVariablesActionParameterOperation.setInputValue(actionParameter.getValue(), executionControl.getExecutionRuntime());
            } else if (actionParameter.getMetadataKey().getParameterName().equalsIgnoreCase(setDatasetKey)) {
                setDatasetActionParameterOperation.setInputValue(actionParameter.getValue(), executionControl.getExecutionRuntime());
            } else if (actionParameter.getMetadataKey().getParameterName().equalsIgnoreCase(expectedStatusCodesKey)) {
                expectedStatusCodesActionParameterOperation.setInputValue(actionParameter.getValue(), executionControl.getExecutionRuntime());
            } else if (actionParameter.getMetadataKey().getParameterName().equalsIgnoreCase(proxyKey)) {
                proxyActionParameterOperation.setInputValue(actionParameter.getValue(), executionControl.getExecutionRuntime());
            }
        }

        // Create parameter list
        actionParameterOperationMap.put(requestKey, requestNameActionParameterOperation);
        actionParameterOperationMap.put(typeKey, requestTypeActionParameterOperation);
        actionParameterOperationMap.put(bodyKey, requestBodyActionParameterOperation);
        actionParameterOperationMap.put(setRuntimeVariablesKey, setRuntimeVariablesActionParameterOperation);
        actionParameterOperationMap.put(setDatasetKey, setDatasetActionParameterOperation);
        actionParameterOperationMap.put(expectedStatusCodesKey, expectedStatusCodesActionParameterOperation);
        actionParameterOperationMap.put(proxyKey, proxyActionParameterOperation);

        HttpRequestComponent httpRequestComponent = HttpRequestComponentService.getInstance()
                .getHttpRequestComponent(convertHttpRequestName(requestNameActionParameterOperation.getValue()), actionExecution, executionControl);
        HttpRequestBuilder httpRequestBuilder = new HttpRequestBuilder()
                .type(convertHttpRequestType(requestTypeActionParameterOperation.getValue()))
                .uri(httpRequestComponent.getUri())
                .headers(httpRequestComponent.getHeaders())
                .queryParameters(httpRequestComponent.getQueryParameters());

        convertHttpRequestBody(requestBodyActionParameterOperation.getValue())
                .map(body -> httpRequestBuilder.body(body,
                        ContentType.getByMimeType(httpRequestComponent.getHeaders().getOrDefault("Content-Type", "text/plain"))));

        httpRequest = httpRequestBuilder.build();
        expectedStatusCodes = convertExpectStatusCodes(expectedStatusCodesActionParameterOperation.getValue());
        setRuntimeVariables = convertSetRuntimeVariables(setRuntimeVariablesActionParameterOperation.getValue());
        proxyConnection = convertProxyName(proxyActionParameterOperation.getValue());
        // TODO: convert from string to dataset DataType
        outputDataset = convertOutputDatasetReferenceName(setDatasetActionParameterOperation.getValue());
//        if (getOutputDataset().isPresent()) {
//            List<String> labels = new ArrayList<>(outputDataset.getLabels());
//            labels.add("typed");
//            rawOutputDataset = (KeyValueDataset) DatasetHandler.getInstance().getByNameAndLabels(outputDataset.getName(), labels, executionControl.getExecutionRuntime());
//        }

    }

    public boolean execute() throws InterruptedException {
        try {
            return executionOperation();
        } catch (InterruptedException e) {
            throw e;
        } catch (Exception e) {
            StringWriter StackTrace = new StringWriter();
            e.printStackTrace(new PrintWriter(StackTrace));

            actionExecution.getActionControl().increaseErrorCount();

            actionExecution.getActionControl().logOutput("exception", e.getMessage());
            actionExecution.getActionControl().logOutput("stacktrace", StackTrace.toString());

            return false;
        }
    }

    private boolean executionOperation() throws NoSuchAlgorithmException, IOException, KeyManagementException, InterruptedException {
        HttpResponse httpResponse;
        if (getProxyConnection().isPresent()) {
            httpResponse = HttpRequestService.getInstance().send(httpRequest, proxyConnection);
        } else {
            httpResponse = HttpRequestService.getInstance().send(httpRequest);
        }
        outputResponse(httpResponse);
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
            LOGGER.warn(MessageFormat.format(actionExecution.getAction().getType() + " does not accept {0} as type for expectedStatusCode",
                    expectedStatusCodes.getClass()));
            return null;
        }
    }

    private String convertExpectedStatusCode(DataType expectedStatusCode) {
        if (expectedStatusCode instanceof Text) {
            return expectedStatusCode.toString();
        } else {
            LOGGER.warn(MessageFormat.format(actionExecution.getAction().getType() + " does not accept {0} as type for expectedStatusCode",
                    expectedStatusCode.getClass()));
            return expectedStatusCode.toString();
        }
    }

    private ProxyConnection convertProxyName(DataType connectionName) {
        if (connectionName == null) {
            return null;
        } else if (connectionName instanceof Text) {
            return proxyConnection = ConnectionConfiguration.getInstance()
                    .get(new ConnectionKey(((Text) connectionName).getString(), executionControl.getEnvName()))
                    .map(ProxyConnection::from)
                    .orElseThrow(() -> new RuntimeException(MessageFormat.format("Cannot find connection {0}", ((Text) connectionName).getString())));
        } else {
            throw new RuntimeException(MessageFormat.format("{0} does not accept {1} as type for proxy connection name",
                    actionExecution.getAction().getType(), connectionName.getClass()));
        }
    }

    private void outputResponse(HttpResponse httpResponse) throws IOException {
        if (getOutputDataset().isPresent()) {
            HttpResponseService.getInstance().writeToDataset(httpResponse, getOutputDataset().get(), executionControl.getExecutionRuntime());
        };
        //HttpResponseService.getInstance().writeToDataset(httpResponse, getOutputDataset());
        //actionExecution.getActionControl().logOutput("response", httpResponse.getResponse().toString());
//        actionExecution.getActionControl().logOutput("status", httpResponse.getStatusLine().toString());
//        actionExecution.getActionControl().logOutput("status.code", String.valueOf(httpResponse.getStatusLine().getStatusCode()));
//        actionExecution.getActionControl().logOutput("body", httpResponse.getEntityString().orElse("<empty>"));
//        int headerCounter = 1;
//        for (Header header : httpResponse.getHeaders()) {
//            actionExecution.getActionControl().logOutput("header." + headerCounter, header.getName() + ":" + header.getValue());
//            headerCounter++;
//        }
    }

    private KeyValueDataset convertOutputDatasetReferenceName(DataType outputDatasetReferenceName) {
        if (outputDatasetReferenceName == null) {
            return null;
        } else if (outputDatasetReferenceName instanceof Text) {
            return executionControl.getExecutionRuntime()
                    .getDataset(((Text) outputDatasetReferenceName).getString())
                    .map(dataset -> (KeyValueDataset) dataset)
                    .orElseThrow(() -> new RuntimeException(MessageFormat.format("No dataset found with name ''{0}''", ((Text) outputDatasetReferenceName).getString())));
        } else {
            LOGGER.warn(MessageFormat.format(actionExecution.getAction().getType() + " does not accept {0} as type for OutputDatasetReferenceName",
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
            LOGGER.warn(MessageFormat.format(actionExecution.getAction().getType() + " does not accept {0} as type for setRuntimeVariablesActionParameterOperation",
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
            LOGGER.warn(MessageFormat.format(actionExecution.getAction().getType() + " does not accept {0} as type for request body",
                    httpRequestBody.getClass()));
            return Optional.of(httpRequestBody.toString());
        }
    }

    private String convertHttpRequestType(DataType httpRequestType) {
        if (httpRequestType instanceof Text) {
            return httpRequestType.toString();
        } else {
            LOGGER.warn(MessageFormat.format(actionExecution.getAction().getType() + " does not accept {0} as type for request type",
                    httpRequestType.getClass()));
            return httpRequestType.toString();
        }
    }

    private String convertHttpRequestName(DataType httpRequestName) {
        if (httpRequestName instanceof Text) {
            return ((Text) httpRequestName).getString();
        } else {
            LOGGER.warn(MessageFormat.format(actionExecution.getAction().getType() + " does not accept {0} as type for request name",
                    httpRequestName.getClass()));
            return httpRequestName.toString();
        }
    }

    private void checkStatusCode(HttpResponse httpResponse) {
        if (getExpectedStatusCodes().isPresent()) {
            if (expectedStatusCodes.contains(String.valueOf(httpResponse.getStatusLine().getStatusCode()))) {
                actionExecution.getActionControl().increaseSuccessCount();
            } else {
                LOGGER.warn(MessageFormat.format("Status code of response {0} is not member of expected status codes {1}",
                        httpResponse.getStatusLine().getStatusCode(), expectedStatusCodes));
                actionExecution.getActionControl().increaseErrorCount();
            }
        } else {
            checkStatusCodeDefault(httpResponse);
        }
    }

    private void checkStatusCodeDefault(HttpResponse httpResponse) {
        if (SUCCESS_STATUS_CODE.matcher(Integer.toString(httpResponse.getStatusLine().getStatusCode())).find()) {
            actionExecution.getActionControl().increaseSuccessCount();
        } else if (INFORMATION_STATUS_CODE.matcher(Integer.toString(httpResponse.getStatusLine().getStatusCode())).find()) {
            actionExecution.getActionControl().increaseSuccessCount();
        } else if (REDIRECT_STATUS_CODE.matcher(Integer.toString(httpResponse.getStatusLine().getStatusCode())).find()) {
            actionExecution.getActionControl().increaseSuccessCount();
        } else {
            LOGGER.warn((MessageFormat.format("Status code of response {0} is not member of success status codes (1XX, 2XX, 3XX).",
                    httpResponse.getStatusLine().getStatusCode())));
            actionExecution.getActionControl().increaseErrorCount();
        }
    }

    private void setRuntimeVariable(JsonNode jsonNode, String keyPrefix) {
        if (setRuntimeVariables) {
            Iterator<Map.Entry<String, JsonNode>> fields = jsonNode.fields();
            while (fields.hasNext()) {
                Map.Entry<String, JsonNode> field = fields.next();
                if (field.getValue().getNodeType().equals(JsonNodeType.OBJECT)) {
                    setRuntimeVariable(field.getValue(), keyPrefix + field.getKey() + ".");
                } else if (field.getValue().getNodeType().equals(JsonNodeType.ARRAY)) {
                    int arrayCounter = 1;
                    for (JsonNode element : field.getValue()) {
                        setRuntimeVariable(element, keyPrefix + field.getKey() + "." + arrayCounter + ".");
                        arrayCounter++;
                    }
                } else if (field.getValue().getNodeType().equals(JsonNodeType.NULL)) {
                    executionControl.getExecutionRuntime().setRuntimeVariable(actionExecution, keyPrefix + field.getKey(), "");
                } else if (field.getValue().isValueNode()) {
                    executionControl.getExecutionRuntime().setRuntimeVariable(actionExecution, keyPrefix + field.getKey(), field.getValue().asText());
                } else {
                    // TODO:
                }
            }
        }
    }

    private void setRuntimeVariable(JsonNode jsonNode, boolean setRuntimeVariables) {
        setRuntimeVariable(jsonNode, "");
    }


    public HashMap<String, ActionParameterOperation> getActionParameterOperationMap() {
        return actionParameterOperationMap;
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

    private Optional<KeyValueDataset> getRawOutputDataset() {
        return Optional.ofNullable(rawOutputDataset);
    }
}