package io.metadew.iesi.script.action;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.metadew.iesi.common.json.JsonParsed;
import io.metadew.iesi.common.json.JsonParsedItem;
import io.metadew.iesi.common.json.JsonTools;
import io.metadew.iesi.common.text.ParsingTools;
import io.metadew.iesi.connection.HttpConnection;
import io.metadew.iesi.connection.http.HttpRequest;
import io.metadew.iesi.connection.http.HttpResponse;
import io.metadew.iesi.datatypes.DataType;
import io.metadew.iesi.datatypes.dataset.Dataset;
import io.metadew.iesi.datatypes.Text;
import io.metadew.iesi.framework.execution.FrameworkExecution;
import io.metadew.iesi.metadata.definition.ActionParameter;
import io.metadew.iesi.script.execution.ActionExecution;
import io.metadew.iesi.script.execution.ExecutionControl;
import io.metadew.iesi.script.execution.ScriptExecution;
import io.metadew.iesi.script.operation.ActionParameterOperation;
import io.metadew.iesi.script.operation.HttpRequestOperation;
import io.metadew.iesi.script.operation.HttpRequestParameterOperation;
import org.apache.http.Header;
import org.apache.http.HttpHeaders;
import org.apache.http.entity.ContentType;
import org.apache.logging.log4j.Level;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.MessageFormat;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;


public class HttpExecuteRequest {

    private ActionExecution actionExecution;

    private FrameworkExecution frameworkExecution;

    private ExecutionControl executionControl;

    // Parameters
    private ActionParameterOperation requestType;

    private ActionParameterOperation requestName;

    private ActionParameterOperation setRuntimeVariables;

    private ActionParameterOperation requestBody;

    private ActionParameterOperation setDataset;

    private HashMap<String, ActionParameterOperation> actionParameterOperationMap;

    private final Pattern INFORMATION_STATUS_CODE = Pattern.compile("1\\d\\d*");
    private final Pattern SUCCESS_STATUS_CODE = Pattern.compile("2\\d\\d*");
    private final Pattern REDIRECT_STATUS_CODE = Pattern.compile("3\\d\\d*");
    @SuppressWarnings("unused")
    private final Pattern SERVER_ERROR_STATUS_CODE = Pattern.compile("4\\d\\d*");
    @SuppressWarnings("unused")
    private final Pattern CLIENT_ERROR_STATUS_CODE = Pattern.compile("5\\d\\d*");

    // Constructors
    public HttpExecuteRequest() {

    }

    public HttpExecuteRequest(FrameworkExecution frameworkExecution, ExecutionControl executionControl,
                              ScriptExecution scriptExecution, ActionExecution actionExecution) {
        this.init(frameworkExecution, executionControl, scriptExecution, actionExecution);
    }

    public void init(FrameworkExecution frameworkExecution, ExecutionControl executionControl,
                     ScriptExecution scriptExecution, ActionExecution actionExecution) {
        this.setFrameworkExecution(frameworkExecution);
        this.setExecutionControl(executionControl);
        this.setActionExecution(actionExecution);
        this.setActionParameterOperationMap(new HashMap<String, ActionParameterOperation>());
    }

    public void prepare() {
        // Reset Parameters
        this.setRequestType(new ActionParameterOperation(this.getFrameworkExecution(), this.getExecutionControl(),
                this.getActionExecution(), this.getActionExecution().getAction().getType(), "type"));
        this.setRequestName(new ActionParameterOperation(this.getFrameworkExecution(), this.getExecutionControl(),
                this.getActionExecution(), this.getActionExecution().getAction().getType(), "request"));
        this.setRequestBody(new ActionParameterOperation(this.getFrameworkExecution(), this.getExecutionControl(),
                this.getActionExecution(), this.getActionExecution().getAction().getType(), "body"));
        this.setSetRuntimeVariables(new ActionParameterOperation(this.getFrameworkExecution(),
                this.getExecutionControl(), this.getActionExecution(), this.getActionExecution().getAction().getType(),
                "setRuntimeVariables"));
        this.setSetDataset(new ActionParameterOperation(this.getFrameworkExecution(), this.getExecutionControl(),
                this.getActionExecution(), this.getActionExecution().getAction().getType(), "setDataset"));

        // Get Parameters
        for (ActionParameter actionParameter : this.getActionExecution().getAction().getParameters()) {
            if (actionParameter.getName().equalsIgnoreCase("request")) {
                this.getRequestName().setInputValue(actionParameter.getValue());
            } else if (actionParameter.getName().equalsIgnoreCase("type")) {
                this.getRequestType().setInputValue(actionParameter.getValue());
            } else if (actionParameter.getName().equalsIgnoreCase("body")) {
                this.getRequestBody().setInputValue(actionParameter.getValue());
            } else if (actionParameter.getName().equalsIgnoreCase("setruntimevariables")) {
                this.getSetRuntimeVariables().setInputValue(actionParameter.getValue());
            } else if (actionParameter.getName().equalsIgnoreCase("setDataset")) {
                this.getSetDataset().setInputValue(actionParameter.getValue());
            }
        }

        // Create parameter list
        this.getActionParameterOperationMap().put("request", this.getRequestName());
        this.getActionParameterOperationMap().put("type", this.getRequestType());
        this.getActionParameterOperationMap().put("body", this.getRequestBody());
        this.getActionParameterOperationMap().put("setRuntimeVariables", this.getSetRuntimeVariables());
        this.getActionParameterOperationMap().put("setDataset", this.getSetDataset());
    }

    public boolean execute() {
        try {
            String requestName = convertHttpRequestName(getRequestName().getValue());
            String requestType = convertHttpRequestType(getRequestType().getValue());
            Optional<String> requestBody = convertHttpRequestBody(getRequestBody().getValue());
            boolean setRuntimeVariables = convertSetRuntimeVariables(getSetRuntimeVariables().getValue());
            // TODO: convert from string to dataset DataType
            String outputDatasetReferenceName = convertOutputDatasetReferenceName(getSetDataset().getValue());

            return executeHttpRequest(requestName, requestType, requestBody, setRuntimeVariables, outputDatasetReferenceName);

        } catch (Exception e) {
            StringWriter StackTrace = new StringWriter();
            e.printStackTrace(new PrintWriter(StackTrace));

            this.getActionExecution().getActionControl().increaseErrorCount();

            this.getActionExecution().getActionControl().logOutput("exception", e.getMessage());
            this.getActionExecution().getActionControl().logOutput("stacktrace", StackTrace.toString());

            return false;
        }
    }

    @SuppressWarnings("rawtypes")
	private boolean executeHttpRequest(String requestName, String requestType, Optional<String> requestBody, boolean setRuntimeVariables, String outputDatasetReferenceName) {
        // Get request configuration
        HttpRequestOperation httpRequestOperation = new HttpRequestOperation(this.getFrameworkExecution(),
                this.getExecutionControl(), this.getActionExecution(), requestName);

        // Run the action
        HttpRequest httpRequest = new HttpRequest(httpRequestOperation.getUrl().getValue());
        Iterator iterator;
        ObjectMapper objectMapper = new ObjectMapper();
        // Headers
        iterator = httpRequestOperation.getHeaderMap().entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry pair = (Map.Entry) iterator.next();
            HttpRequestParameterOperation httpRequestParameterOperation = objectMapper.convertValue(pair.getValue(),
                    HttpRequestParameterOperation.class);
            String[] headerPair = ParsingTools.getValuesForDelimitedList(true,
                    httpRequestParameterOperation.getValue());
            httpRequest.addHeader(headerPair[0], headerPair[1]);
            iterator.remove();
        }

        // QueryParams
        iterator = httpRequestOperation.getQueryParamMap().entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry pair = (Map.Entry) iterator.next();
            HttpRequestParameterOperation httpRequestParameterOperation = objectMapper.convertValue(pair.getValue(),
                    HttpRequestParameterOperation.class);
            String[] headerPair = ParsingTools.getValuesForDelimitedList(true,
                    httpRequestParameterOperation.getValue());
            httpRequest.addQueryParam(headerPair[0], headerPair[1]);
            iterator.remove();
        }

        HttpConnection httpConnection = new HttpConnection(httpRequest);
        HttpResponse httpResponse = new HttpResponse();

        if (requestType.equalsIgnoreCase("get")) {
            httpResponse = httpConnection.executeGetRequest();
        } else if (requestType.equalsIgnoreCase("post")) {
            httpResponse = httpConnection.executePostRequest(requestBody.orElse(""));
        }

        this.getActionExecution().getActionControl().logOutput("response", httpResponse.getResponse().toString());
        this.getActionExecution().getActionControl().logOutput("status", httpResponse.getStatusLine().toString());
        this.getActionExecution().getActionControl().logOutput("entity", httpResponse.getEntityString());
        this.getActionExecution().getActionControl().logOutput("headers", httpResponse.getHeaders().stream()
                .map(header -> header.getName() + ":" + header.getValue())
                .collect(Collectors.joining(",\n")));
        // Parsing entity
        writeResponseToOutputDataset(httpResponse, outputDatasetReferenceName, setRuntimeVariables);
        // Check error code
        checkStatusCode(httpResponse);
        return true;
    }

    private String convertOutputDatasetReferenceName(DataType outputDatasetReferenceName) {
        if (outputDatasetReferenceName instanceof Text) {
            return outputDatasetReferenceName.toString();
        } else {
            frameworkExecution.getFrameworkLog().log(MessageFormat.format(this.getActionExecution().getAction().getType() + " does not accept {0} as type for OutputDatasetReferenceName",
                    outputDatasetReferenceName.getClass()), Level.WARN);
            return outputDatasetReferenceName.toString();
        }
    }

    private boolean convertSetRuntimeVariables(DataType setRuntimeVariables) {
        if (setRuntimeVariables == null) {
            return false;
        }
        if (setRuntimeVariables instanceof Text) {
            return setRuntimeVariables.toString().equalsIgnoreCase("y");
        } else {
            this.getFrameworkExecution().getFrameworkLog().log(MessageFormat.format(this.getActionExecution().getAction().getType() + " does not accept {0} as type for setRuntimeVariables",
                    setRuntimeVariables.getClass()), Level.WARN);
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
            this.getFrameworkExecution().getFrameworkLog().log(MessageFormat.format(this.getActionExecution().getAction().getType() + " does not accept {0} as type for request body",
                    httpRequestBody.getClass()), Level.WARN);
            return Optional.of(httpRequestBody.toString());
        }
    }

    private String convertHttpRequestType(DataType httpRequestType) {
        if (httpRequestType instanceof Text) {
            return httpRequestType.toString();
        } else {
            this.getFrameworkExecution().getFrameworkLog().log(MessageFormat.format(this.getActionExecution().getAction().getType() + " does not accept {0} as type for request type",
                    httpRequestType.getClass()), Level.WARN);
            return httpRequestType.toString();
        }
    }

    private String convertHttpRequestName(DataType httpRequestName) {
        if (httpRequestName instanceof Text) {
            return httpRequestName.toString();
        } else {
            this.getFrameworkExecution().getFrameworkLog().log(MessageFormat.format(this.getActionExecution().getAction().getType() + " does not accept {0} as type for request name",
                    httpRequestName.getClass()), Level.WARN);
            return httpRequestName.toString();
        }
    }

    private void checkStatusCode(HttpResponse httpResponse) {
        if (SUCCESS_STATUS_CODE.matcher(Integer.toString(httpResponse.getStatusLine().getStatusCode())).find()) {
            this.getActionExecution().getActionControl().increaseSuccessCount();
        } else if (INFORMATION_STATUS_CODE.matcher(Integer.toString(httpResponse.getStatusLine().getStatusCode())).find()) {
            this.getActionExecution().getActionControl().increaseSuccessCount();
        } else if (REDIRECT_STATUS_CODE.matcher(Integer.toString(httpResponse.getStatusLine().getStatusCode())).find()) {
            this.getActionExecution().getActionControl().increaseSuccessCount();
        } else {
            this.getActionExecution().getActionControl().increaseErrorCount();
        }
    }

    private void writeResponseToOutputDataset(HttpResponse httpResponse, String outputDatasetReferenceName, boolean setRuntimeVariables) {
        // TODO: how to handle multiple content-types
        List<Header> contentTypeHeaders = httpResponse.getHeaders().stream()
                .filter(header -> header.getName().equals(HttpHeaders.CONTENT_TYPE))
                .collect(Collectors.toList());
        if (contentTypeHeaders.size() > 1) {
            this.getActionExecution().getActionControl().logWarning("content-type",
                    MessageFormat.format("Http response contains multiple headers ({0}) defining the content type", contentTypeHeaders.size()));
        } else if (contentTypeHeaders.size() == 0) {
            this.getActionExecution().getActionControl().logWarning("content-type", "Http response contains no header defining the content type.");
            return;
        }

        if (contentTypeHeaders.stream().anyMatch(header -> header.getValue().contains(ContentType.APPLICATION_JSON.getMimeType()))) {
            writeJSONResponseToOutputDataset(httpResponse, outputDatasetReferenceName, setRuntimeVariables);
        } else if (contentTypeHeaders.stream().anyMatch(header -> header.getValue().contains(ContentType.TEXT_PLAIN.getMimeType()))) {
            writeTextPlainResponseToOutputDataset(httpResponse, outputDatasetReferenceName);
        } else {
            this.getActionExecution().getActionControl().logWarning("content-type", "Http response contains unsupported content-type header. Response will be written to dataset as plain text.");
        }
    }

    private void writeTextPlainResponseToOutputDataset(HttpResponse httpResponse, String outputDatasetReferenceName) {
        if (!outputDatasetReferenceName.isEmpty()) {
//			String[] parts = outputDatasetReferenceName.split("\\.");
//			String datasetName = parts[0];
//			String datasetTableName = parts[1];
//			this.getExecutionControl().getExecutionRuntime().getDatasetOperation(datasetName).resetDataset(datasetTableName);
//			this.getExecutionControl().getExecutionRuntime().getDatasetOperation(datasetName).setDatasetEntry(datasetTableName, "response", httpResponse.getEntityString());

            Optional<Dataset> outputDataset = executionControl.getExecutionRuntime().getDataset(outputDatasetReferenceName);
            outputDataset.ifPresent(dataset -> {
                dataset.clean();
                dataset.setDataItem("response", new Text(httpResponse.getEntityString()));
            });
        }
    }

    private void writeJSONResponseToOutputDataset(HttpResponse httpResponse, String outputDatasetReferenceName, boolean setRuntimeVariables) {
        JsonParsed jsonParsed;
        try {
            jsonParsed = new JsonTools().parseJson("string", httpResponse.getEntityString());
            this.setRuntimeVariable(jsonParsed, setRuntimeVariables);

            if (!outputDatasetReferenceName.isEmpty()) {
//				String[] parts = outputDatasetReferenceName.split("\\.");
//				String datasetName = parts[0];
//				String datasetTableName = parts[1];
//				this.getExecutionControl().getExecutionRuntime().getDatasetOperation(datasetName)
//						.setKeyValueDataset(datasetTableName, jsonParsed);

                Optional<Dataset> outputDataset = executionControl.getExecutionRuntime().getDataset(outputDatasetReferenceName);
                outputDataset.ifPresent(dataset -> {
                    dataset.clean();
                    jsonParsed.getJsonParsedItemList().forEach(jsonParsedItem -> dataset.setDataItem(jsonParsedItem.getPath(), new Text(jsonParsedItem.getValue())));
                });

            }
        } catch (Exception e) {
            this.getActionExecution().getActionControl().logError("json", e.getMessage());
        }
    }

    private void setRuntimeVariable(JsonParsed jsonParsed, boolean setRuntimeVariables) {
        if (setRuntimeVariables) {
            try {
                for (JsonParsedItem jsonParsedItem : jsonParsed.getJsonParsedItemList()) {
                    this.getExecutionControl().getExecutionRuntime().setRuntimeVariable(actionExecution, jsonParsedItem.getPath(),
                            jsonParsedItem.getValue());
                }
            } catch (Exception e) {
                this.getActionExecution().getActionControl().increaseWarningCount();
                this.getExecutionControl().logExecutionOutput(this.getActionExecution(), "SET_RUN_VAR", e.getMessage());
            }
        }
    }

    // Getters and Setters
    public FrameworkExecution getFrameworkExecution() {
        return frameworkExecution;
    }

    public void setFrameworkExecution(FrameworkExecution frameworkExecution) {
        this.frameworkExecution = frameworkExecution;
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

    public ActionParameterOperation getActionParameterOperation(String key) {
        return this.getActionParameterOperationMap().get(key);
    }

    public ActionParameterOperation getRequestName() {
        return requestName;
    }

    public void setRequestName(ActionParameterOperation requestName) {
        this.requestName = requestName;
    }

    public ActionParameterOperation getSetRuntimeVariables() {
        return setRuntimeVariables;
    }

    public void setSetRuntimeVariables(ActionParameterOperation setRuntimeVariables) {
        this.setRuntimeVariables = setRuntimeVariables;
    }

    public ActionParameterOperation getRequestType() {
        return requestType;
    }

    public void setRequestType(ActionParameterOperation requestType) {
        this.requestType = requestType;
    }

    public ActionParameterOperation getRequestBody() {
        return requestBody;
    }

    public void setRequestBody(ActionParameterOperation requestBody) {
        this.requestBody = requestBody;
    }

    public ActionParameterOperation getSetDataset() {
        return setDataset;
    }

    public void setSetDataset(ActionParameterOperation setDataset) {
        this.setDataset = setDataset;
    }

}