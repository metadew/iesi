package io.metadew.iesi.script.action.http;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.JsonNodeType;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.metadew.iesi.connection.http.*;
import io.metadew.iesi.datatypes.DataType;
import io.metadew.iesi.datatypes.DataTypeService;
import io.metadew.iesi.datatypes.array.Array;
import io.metadew.iesi.datatypes.dataset.KeyValueDataset;
import io.metadew.iesi.datatypes.text.Text;
import io.metadew.iesi.metadata.configuration.connection.ConnectionConfiguration;
import io.metadew.iesi.metadata.configuration.exception.ComponentDoesNotExistException;
import io.metadew.iesi.metadata.definition.HttpRequestComponent;
import io.metadew.iesi.metadata.definition.action.ActionParameter;
import io.metadew.iesi.script.execution.ActionExecution;
import io.metadew.iesi.script.execution.ExecutionControl;
import io.metadew.iesi.script.execution.ScriptExecution;
import io.metadew.iesi.script.operation.ActionParameterOperation;
import io.metadew.iesi.script.operation.HttpRequestComponentService;
import org.apache.http.Header;
import org.apache.http.HttpHeaders;
import org.apache.http.entity.ContentType;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URISyntaxException;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;


public class HttpExecuteRequest {

    private static Logger LOGGER = LogManager.getLogger();

    private HttpRequestService httpRequestService;
    private HttpRequestComponentService httpRequestComponentService;
    private ActionExecution actionExecution;
    private ExecutionControl executionControl;
    private DataTypeService dataTypeService;
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
        this.httpRequestComponentService = new HttpRequestComponentService(executionControl);
        this.httpRequestService = new HttpRequestService();
        this.dataTypeService = new DataTypeService();
    }

    public void prepare() throws URISyntaxException, HttpRequestBuilderException, IOException, SQLException, ComponentDoesNotExistException {
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
            if (actionParameter.getName().equalsIgnoreCase(requestKey)) {
                requestNameActionParameterOperation.setInputValue(actionParameter.getValue(), executionControl.getExecutionRuntime());
            } else if (actionParameter.getName().equalsIgnoreCase(typeKey)) {
                requestTypeActionParameterOperation.setInputValue(actionParameter.getValue(), executionControl.getExecutionRuntime());
            } else if (actionParameter.getName().equalsIgnoreCase(bodyKey)) {
                requestBodyActionParameterOperation.setInputValue(actionParameter.getValue(), executionControl.getExecutionRuntime());
            } else if (actionParameter.getName().equalsIgnoreCase(setRuntimeVariablesKey)) {
                setRuntimeVariablesActionParameterOperation.setInputValue(actionParameter.getValue(), executionControl.getExecutionRuntime());
            } else if (actionParameter.getName().equalsIgnoreCase(setDatasetKey)) {
                setDatasetActionParameterOperation.setInputValue(actionParameter.getValue(), executionControl.getExecutionRuntime());
            } else if (actionParameter.getName().equalsIgnoreCase(expectedStatusCodesKey)) {
                expectedStatusCodesActionParameterOperation.setInputValue(actionParameter.getValue(), executionControl.getExecutionRuntime());
            } else if (actionParameter.getName().equalsIgnoreCase(proxyKey)) {
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

        HttpRequestComponent httpRequestComponent = httpRequestComponentService.getHttpRequestComponent(convertHttpRequestName(requestNameActionParameterOperation.getValue()), actionExecution);
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
        if (getOutputDataset().isPresent()) {
            List<String> labels = new ArrayList<>(outputDataset.getLabels());
            labels.add("typed");
            rawOutputDataset = new KeyValueDataset(outputDataset.getName(), labels, executionControl.getExecutionRuntime());
        }

    }

    public boolean execute() {
        try {
            HttpResponse httpResponse;
            if (getProxyConnection().isPresent()) {
                httpResponse = httpRequestService.send(httpRequest, proxyConnection);
            }else {
                httpResponse = httpRequestService.send(httpRequest);
            }
            outputResponse(httpResponse);
            // Parsing entity
            writeResponseToOutputDataset(httpResponse);
            // Check error code
            checkStatusCode(httpResponse);
            return true;
        } catch (Exception e) {
            StringWriter StackTrace = new StringWriter();
            e.printStackTrace(new PrintWriter(StackTrace));

            actionExecution.getActionControl().increaseErrorCount();

            actionExecution.getActionControl().logOutput("exception", e.getMessage());
            actionExecution.getActionControl().logOutput("stacktrace", StackTrace.toString());

            return false;
        }
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
            ConnectionConfiguration connectionConfiguration = new ConnectionConfiguration();
            return proxyConnection = connectionConfiguration.get(((Text) connectionName).getString(), executionControl.getEnvName())
                    .map(ProxyConnection::from)
                    .orElseThrow(() -> new RuntimeException(MessageFormat.format("Cannot find connection {0}", ((Text) connectionName).getString())));
        } else {
            throw new RuntimeException(MessageFormat.format("{0} does not accept {1} as type for proxy connection name",
                    actionExecution.getAction().getType(), connectionName.getClass()));
        }
    }

    private void outputResponse(HttpResponse httpResponse) {
        //actionExecution.getActionControl().logOutput("response", httpResponse.getResponse().toString());
        actionExecution.getActionControl().logOutput("status", httpResponse.getStatusLine().toString());
        actionExecution.getActionControl().logOutput("status.code", String.valueOf(httpResponse.getStatusLine().getStatusCode()));
        actionExecution.getActionControl().logOutput("body", httpResponse.getEntityString().orElse("<empty>"));
        int headerCounter = 1;
        for (Header header : httpResponse.getHeaders()) {
            actionExecution.getActionControl().logOutput("header." + headerCounter, header.getName() + ":" + header.getValue());
            headerCounter++;
        }

    }

    private KeyValueDataset convertOutputDatasetReferenceName(DataType outputDatasetReferenceName) {
        if (outputDatasetReferenceName == null) {
            return null;
        } else if (outputDatasetReferenceName instanceof Text) {
            return executionControl.getExecutionRuntime().getDataset(((Text) outputDatasetReferenceName).getString())
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

    private void writeResponseToOutputDataset(HttpResponse httpResponse) {
        // TODO: how to handle multiple content-types
        List<Header> contentTypeHeaders = httpResponse.getHeaders().stream()
                .filter(header -> header.getName().equals(HttpHeaders.CONTENT_TYPE))
                .collect(Collectors.toList());
        if (contentTypeHeaders.size() > 1) {
            actionExecution.getActionControl().logWarning("content-type",
                    MessageFormat.format("Http response contains multiple headers ({0}) defining the content type", contentTypeHeaders.size()));
        } else if (contentTypeHeaders.size() == 0) {
            actionExecution.getActionControl().logWarning("content-type", "Http response contains no header defining the content type. Assuming text/plain");
            writeTextPlainResponseToOutputDataset(httpResponse);
        }

        if (contentTypeHeaders.stream().anyMatch(header -> header.getValue().contains(ContentType.APPLICATION_JSON.getMimeType()))) {
            writeJSONResponseToOutputDataset(httpResponse);
        } else if (contentTypeHeaders.stream().anyMatch(header -> header.getValue().contains(ContentType.TEXT_PLAIN.getMimeType()))) {
            writeTextPlainResponseToOutputDataset(httpResponse);
        } else {
            actionExecution.getActionControl().logWarning("content-type", "Http response contains unsupported content-type header. Response will be written to dataset as plain text.");
            writeTextPlainResponseToOutputDataset(httpResponse);
        }
    }

    private void writeTextPlainResponseToOutputDataset(HttpResponse httpResponse) {
        getOutputDataset().ifPresent(dataset -> {
            dataset.clean(getExecutionControl().getExecutionRuntime());
            dataset.setDataItem("response", new Text(httpResponse.getEntityString().orElse("")));
        });
        getRawOutputDataset().ifPresent(dataset -> {
            dataset.clean(getExecutionControl().getExecutionRuntime());
            dataset.setDataItem("response", new Text(httpResponse.getEntityString().orElse("")));
        });
    }

    private void writeJSONResponseToOutputDataset(HttpResponse httpResponse) {
        if (httpResponse.getEntityString().isPresent()) {
            try {
                JsonNode jsonNode = new ObjectMapper().readTree(httpResponse.getEntityString().get());
                setRuntimeVariable(jsonNode, setRuntimeVariables);
                // TODO: flip raw/normal if ready to migrate
                getOutputDataset().ifPresent(dataset -> {
                    dataset.clean(getExecutionControl().getExecutionRuntime());
                    dataTypeService.getKeyValueDatasetService().writeRawJSON(dataset, jsonNode);
                });
                getRawOutputDataset().ifPresent(dataset -> {
                    dataset.clean(getExecutionControl().getExecutionRuntime());
                    try {
                        dataTypeService.getKeyValueDatasetService().write(dataset, (ObjectNode) jsonNode, executionControl.getExecutionRuntime());
                    } catch (IOException | SQLException e) {
                        StringWriter StackTrace = new StringWriter();
                        e.printStackTrace(new PrintWriter(StackTrace));
                        actionExecution.getActionControl().logOutput("json.exception", e.getMessage());
                        actionExecution.getActionControl().logOutput("json.stacktrace", StackTrace.toString());
                    }
                });
            } catch (Exception e) {
                StringWriter StackTrace = new StringWriter();
                e.printStackTrace(new PrintWriter(StackTrace));
                actionExecution.getActionControl().logOutput("json.exception", e.getMessage());
                actionExecution.getActionControl().logOutput("json.stacktrace", StackTrace.toString());
            }
        }
    }


    private void setRuntimeVariable(JsonNode jsonNode, String keyPrefix) {
        if (setRuntimeVariables) {
            try {
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
            } catch (Exception e) {
                actionExecution.getActionControl().increaseWarningCount();
                executionControl.logExecutionOutput(actionExecution, "SET_RUN_VAR", e.getMessage());
            }
        }
    }

    private void setRuntimeVariable(JsonNode jsonNode, boolean setRuntimeVariables) {
        setRuntimeVariable(jsonNode, "");
    }

    public ExecutionControl getExecutionControl() {
        return executionControl;
    }

    public void setExecutionControl(ExecutionControl executionControl) {
        this.executionControl = executionControl;
    }

    public ActionExecution getActionExecution() {
        return actionExecution;
    }

    public void setActionExecution(ActionExecution actionExecution) {
        this.actionExecution = actionExecution;
    }

    public HashMap<String, ActionParameterOperation> getActionParameterOperationMap() {
        return actionParameterOperationMap;
    }

    public void setActionParameterOperationMap(HashMap<String, ActionParameterOperation> actionParameterOperationMap) {
        this.actionParameterOperationMap = actionParameterOperationMap;
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