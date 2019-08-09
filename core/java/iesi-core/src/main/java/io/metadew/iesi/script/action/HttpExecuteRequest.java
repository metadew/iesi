//package io.metadew.iesi.script.action;
//
//import com.fasterxml.jackson.databind.JsonNode;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.fasterxml.jackson.databind.node.JsonNodeType;
//import io.metadew.iesi.common.json.JsonParsed;
//import io.metadew.iesi.common.text.ParsingTools;
//import io.metadew.iesi.connection.HttpConnection;
//import io.metadew.iesi.connection.http.HttpRequest;
//import io.metadew.iesi.connection.http.HttpResponse;
//import io.metadew.iesi.datatypes.array.Array;
//import io.metadew.iesi.datatypes.DataType;
//import io.metadew.iesi.datatypes.text.Text;
//import io.metadew.iesi.datatypes.dataset.Dataset;
//import io.metadew.iesi.framework.execution.FrameworkExecution;
//import io.metadew.iesi.metadata.definition.action.ActionParameter;
//import io.metadew.iesi.script.execution.ActionExecution;
//import io.metadew.iesi.script.execution.ExecutionControl;
//import io.metadew.iesi.script.execution.ScriptExecution;
//import io.metadew.iesi.script.operation.ActionParameterOperation;
//import io.metadew.iesi.script.operation.HttpRequestOperation;
//import io.metadew.iesi.script.operation.HttpRequestParameterOperation;
//import org.apache.http.Header;
//import org.apache.http.HttpHeaders;
//import org.apache.http.entity.ContentType;
//import org.apache.logging.log4j.Level;
//
//import java.io.PrintWriter;
//import java.io.StringWriter;
//import java.text.MessageFormat;
//import java.util.*;
//import java.util.regex.Pattern;
//import java.util.stream.Collectors;
//
//
//public class HttpExecuteRequest {
//
//    private ActionExecution actionExecution;
//    private FrameworkExecution frameworkExecution;
//    private ExecutionControl executionControl;
//
//    // Parameters
//    private ActionParameterOperation requestType;
//    private ActionParameterOperation requestName;
//    private ActionParameterOperation setRuntimeVariables;
//    private ActionParameterOperation requestBody;
//    private ActionParameterOperation setDataset;
//    private ActionParameterOperation expectedStatusCodes;
//
//    private HashMap<String, ActionParameterOperation> actionParameterOperationMap;
//
//    private final Pattern INFORMATION_STATUS_CODE = Pattern.compile("1\\d\\d");
//    private final Pattern SUCCESS_STATUS_CODE = Pattern.compile("2\\d\\d");
//    private final Pattern REDIRECT_STATUS_CODE = Pattern.compile("3\\d\\d");
//    @SuppressWarnings("unused")
//    private final Pattern SERVER_ERROR_STATUS_CODE = Pattern.compile("4\\d\\d");
//    @SuppressWarnings("unused")
//    private final Pattern CLIENT_ERROR_STATUS_CODE = Pattern.compile("5\\d\\d");
//
//    // Constructors
//    public HttpExecuteRequest() {
//
//    }
//
//    public HttpExecuteRequest(FrameworkExecution frameworkExecution, ExecutionControl executionControl,
//                              ScriptExecution scriptExecution, ActionExecution actionExecution) {
//        this.init(frameworkExecution, executionControl, scriptExecution, actionExecution);
//    }
//
//    public void init(FrameworkExecution frameworkExecution, ExecutionControl executionControl,
//                     ScriptExecution scriptExecution, ActionExecution actionExecution) {
//        this.setFrameworkExecution(frameworkExecution);
//        this.setExecutionControl(executionControl);
//        this.setActionExecution(actionExecution);
//        this.setActionParameterOperationMap(new HashMap<>());
//    }
//
//    public void prepare() {
//        // Reset Parameters
//        this.setRequestType(new ActionParameterOperation(this.getFrameworkExecution(), this.getExecutionControl(),
//                this.getActionExecution(), this.getActionExecution().get().getType(), "type"));
//        this.setRequestName(new ActionParameterOperation(this.getFrameworkExecution(), this.getExecutionControl(),
//                this.getActionExecution(), this.getActionExecution().get().getType(), "request"));
//        this.setRequestBody(new ActionParameterOperation(this.getFrameworkExecution(), this.getExecutionControl(),
//                this.getActionExecution(), this.getActionExecution().get().getType(), "body"));
//        this.setSetRuntimeVariables(new ActionParameterOperation(this.getFrameworkExecution(),
//                this.getExecutionControl(), this.getActionExecution(), this.getActionExecution().get().getType(),
//                "setRuntimeVariables"));
//        this.setSetDataset(new ActionParameterOperation(this.getFrameworkExecution(), this.getExecutionControl(),
//                this.getActionExecution(), this.getActionExecution().get().getType(), "setDataset"));
//        expectedStatusCodes = new ActionParameterOperation(this.getFrameworkExecution(), this.getExecutionControl(),
//                this.getActionExecution(), this.getActionExecution().get().getType(), "expectedStatusCodes");
//
//        // Get Parameters
//        for (ActionParameter actionParameter : this.getActionExecution().get().getParameters()) {
//            if (actionParameter.getName().equalsIgnoreCase("request")) {
//                this.getRequestName().setInputValue(actionParameter.getValue());
//            } else if (actionParameter.getName().equalsIgnoreCase("type")) {
//                this.getRequestType().setInputValue(actionParameter.getValue());
//            } else if (actionParameter.getName().equalsIgnoreCase("body")) {
//                this.getRequestBody().setInputValue(actionParameter.getValue());
//            } else if (actionParameter.getName().equalsIgnoreCase("setruntimevariables")) {
//                this.getSetRuntimeVariables().setInputValue(actionParameter.getValue());
//            } else if (actionParameter.getName().equalsIgnoreCase("setDataset")) {
//                this.getSetDataset().setInputValue(actionParameter.getValue());
//            } else if (actionParameter.getName().equalsIgnoreCase("expectedStatusCodes")) {
//                expectedStatusCodes.setInputValue(actionParameter.getValue());
//            }
//        }
//
//        // Create parameter list
//        this.getActionParameterOperationMap().put("request", this.getRequestName());
//        this.getActionParameterOperationMap().put("type", this.getRequestType());
//        this.getActionParameterOperationMap().put("body", this.getRequestBody());
//        this.getActionParameterOperationMap().put("setRuntimeVariables", this.getSetRuntimeVariables());
//        this.getActionParameterOperationMap().put("setDataset", this.getSetDataset());
//        this.getActionParameterOperationMap().put("expectedStatusCodes", expectedStatusCodes);
//    }
//
//    public boolean execute() {
//        try {
//            String requestName = convertHttpRequestName(getRequestName().getValue());
//            String requestType = convertHttpRequestType(getRequestType().getValue());
//            Optional<String> requestBody = convertHttpRequestBody(getRequestBody().getValue());
//            Optional<List<String>> expectedStatusCodes = convertExpectStatusCodes(this.expectedStatusCodes.getValue());
//            boolean setRuntimeVariables = convertSetRuntimeVariables(getSetRuntimeVariables().getValue());
//            // TODO: convert from string to dataset DataType
//            String outputDatasetReferenceName = convertOutputDatasetReferenceName(getSetDataset().getValue());
//
//            return executeHttpRequest(requestName, requestType, requestBody, setRuntimeVariables, outputDatasetReferenceName, expectedStatusCodes);
//
//        } catch (Exception e) {
//            StringWriter StackTrace = new StringWriter();
//            e.printStackTrace(new PrintWriter(StackTrace));
//
//            this.getActionExecution().getActionControl().increaseErrorCount();
//
//            this.getActionExecution().getActionControl().logOutput("exception", e.getMessage());
//            this.getActionExecution().getActionControl().logOutput("stacktrace", StackTrace.toString());
//
//            return false;
//        }
//    }
//
//    private Optional<List<String>> convertExpectStatusCodes(DataType expectedStatusCodes) {
//        if (expectedStatusCodes == null) {
//            return Optional.empty();
//        }
//        if (expectedStatusCodes instanceof Text) {
//            return Optional.of(Arrays.stream(expectedStatusCodes.toString().split(","))
//                    .map(String::trim)
//                    .collect(Collectors.toList()));
//        } else if (expectedStatusCodes instanceof Array){
//            return Optional.of(((Array) expectedStatusCodes).getList().stream()
//                    .map(this::convertExpectedStatusCode)
//                    .collect(Collectors.toList()));
//        } else {
//            this.getFrameworkExecution().getFrameworkLog().log(MessageFormat.format(this.getActionExecution().get().getType() + " does not accept {0} as type for expectedStatusCode",
//                    expectedStatusCodes.getClass()), Level.WARN);
//            return Optional.empty();
//        }
//    }
//
//    private String convertExpectedStatusCode(DataType expectedStatusCode) {
//        if (expectedStatusCode instanceof Text) {
//            return expectedStatusCode.toString();
//        } else {
//            frameworkExecution.getFrameworkLog().log(MessageFormat.format(this.getActionExecution().get().getType() + " does not accept {0} as type for expectedStatusCode",
//                    expectedStatusCode.getClass()), Level.WARN);
//            return expectedStatusCode.toString();
//        }
//    }
//
//    @SuppressWarnings("rawtypes")
//	private boolean executeHttpRequest(String requestName, String requestType, Optional<String> requestBody, boolean setRuntimeVariables, String outputDatasetReferenceName, Optional<List<String>> expectedStatusCodes) {
//        // Get request configuration
//        HttpRequestOperation httpRequestOperation = new HttpRequestOperation(this.getFrameworkExecution(),
//                this.getExecutionControl(), this.getActionExecution(), requestName);
//
//        // Run the action
//        HttpRequest httpRequest = new HttpRequest(httpRequestOperation.getUrl().getValue());
//        Iterator iterator;
//        ObjectMapper objectMapper = new ObjectMapper();
//        // Headers
//        iterator = httpRequestOperation.getHeaderMap().entrySet().iterator();
//        while (iterator.hasNext()) {
//            Map.Entry pair = (Map.Entry) iterator.next();
//            HttpRequestParameterOperation httpRequestParameterOperation = objectMapper.convertValue(pair.getValue(),
//                    HttpRequestParameterOperation.class);
//            String[] headerPair = ParsingTools.getValuesForDelimitedList(true,
//                    httpRequestParameterOperation.getValue());
//            httpRequest.addHeader(headerPair[0], headerPair[1]);
//            iterator.remove();
//        }
//
//        // QueryParams
//        iterator = httpRequestOperation.getQueryParamMap().entrySet().iterator();
//        while (iterator.hasNext()) {
//            Map.Entry pair = (Map.Entry) iterator.next();
//            HttpRequestParameterOperation httpRequestParameterOperation = objectMapper.convertValue(pair.getValue(),
//                    HttpRequestParameterOperation.class);
//            String[] headerPair = ParsingTools.getValuesForDelimitedList(true,
//                    httpRequestParameterOperation.getValue());
//            httpRequest.addQueryParam(headerPair[0], headerPair[1]);
//            iterator.remove();
//        }
//
//        HttpConnection httpConnection = new HttpConnection(httpRequest);
//        HttpResponse httpResponse = new HttpResponse();
//
//        if (requestType.equalsIgnoreCase("get")) {
//            httpResponse = httpConnection.executeGetRequest();
//        } else if (requestType.equalsIgnoreCase("post")) {
//            httpResponse = httpConnection.executePostRequest(requestBody.orElse(""));
//        }
//
//        outputResponse(httpResponse);
//        // Parsing entity
//        writeResponseToOutputDataset(httpResponse, outputDatasetReferenceName, setRuntimeVariables);
//        // Check error code
//        checkStatusCode(httpResponse, expectedStatusCodes);
//        return true;
//    }
//
//    private void outputResponse(HttpResponse httpResponse) {
//        //this.getActionExecution().getActionControl().logOutput("response", httpResponse.getResponse().toString());
//        this.getActionExecution().getActionControl().logOutput("status", httpResponse.getStatusLine().toString());
//        this.getActionExecution().getActionControl().logOutput("status.code", String.valueOf(httpResponse.getStatusLine().getStatusCode()));
//        this.getActionExecution().getActionControl().logOutput("body", httpResponse.getEntityString());
//        int headerCounter = 1;
//        for (Header header : httpResponse.getHeaders()) {
//            actionExecution.getActionControl().logOutput("header." + headerCounter, header.getName() + ":" + header.getValue());
//            headerCounter++;
//        }
//
//    }
//
//    private String convertOutputDatasetReferenceName(DataType outputDatasetReferenceName) {
//    	if (outputDatasetReferenceName instanceof Text) {
//            return outputDatasetReferenceName.toString();
//        } else {
//            frameworkExecution.getFrameworkLog().log(MessageFormat.format(this.getActionExecution().get().getType() + " does not accept {0} as type for OutputDatasetReferenceName",
//                    outputDatasetReferenceName.getClass()), Level.WARN);
//            return outputDatasetReferenceName.toString();
//        }
//    }
//
//    private boolean convertSetRuntimeVariables(DataType setRuntimeVariables) {
//        if (setRuntimeVariables == null) {
//            return false;
//        }
//        if (setRuntimeVariables instanceof Text) {
//            return setRuntimeVariables.toString().equalsIgnoreCase("y");
//        } else {
//            this.getFrameworkExecution().getFrameworkLog().log(MessageFormat.format(this.getActionExecution().get().getType() + " does not accept {0} as type for setRuntimeVariables",
//                    setRuntimeVariables.getClass()), Level.WARN);
//            return false;
//        }
//    }
//
//    private Optional<String> convertHttpRequestBody(DataType httpRequestBody) {
//        if (httpRequestBody == null) {
//            return Optional.empty();
//        }
//        if (httpRequestBody instanceof Text) {
//            return Optional.of(httpRequestBody.toString());
//        } else {
//            this.getFrameworkExecution().getFrameworkLog().log(MessageFormat.format(this.getActionExecution().get().getType() + " does not accept {0} as type for request body",
//                    httpRequestBody.getClass()), Level.WARN);
//            return Optional.of(httpRequestBody.toString());
//        }
//    }
//
//    private String convertHttpRequestType(DataType httpRequestType) {
//        if (httpRequestType instanceof Text) {
//            return httpRequestType.toString();
//        } else {
//            this.getFrameworkExecution().getFrameworkLog().log(MessageFormat.format(this.getActionExecution().get().getType() + " does not accept {0} as type for request type",
//                    httpRequestType.getClass()), Level.WARN);
//            return httpRequestType.toString();
//        }
//    }
//
//    private String convertHttpRequestName(DataType httpRequestName) {
//        if (httpRequestName instanceof Text) {
//            return httpRequestName.toString();
//        } else {
//            this.getFrameworkExecution().getFrameworkLog().log(MessageFormat.format(this.getActionExecution().get().getType() + " does not accept {0} as type for request name",
//                    httpRequestName.getClass()), Level.WARN);
//            return httpRequestName.toString();
//        }
//    }
//
//    private void checkStatusCode(HttpResponse httpResponse, Optional<List<String>> expectedStatusCodes) {
//        if (expectedStatusCodes.isPresent()) {
//            System.out.println(expectedStatusCodes.get());
//            if (expectedStatusCodes.get().contains(String.valueOf(httpResponse.getStatusLine().getStatusCode()))) {
//                actionExecution.getActionControl().increaseSuccessCount();
//            } else {
//                frameworkExecution.getFrameworkLog().log(MessageFormat.format("Status code of response {0} is not member of expected status codes {1}",
//                        httpResponse.getStatusLine().getStatusCode(), expectedStatusCodes.get()), Level.WARN);
//                actionExecution.getActionControl().increaseErrorCount();
//            }
//        } else {
//            checkStatusCode(httpResponse);
//        }
//    }
//
//    private void checkStatusCode(HttpResponse httpResponse) {
//        if (SUCCESS_STATUS_CODE.matcher(Integer.toString(httpResponse.getStatusLine().getStatusCode())).find()) {
//            this.getActionExecution().getActionControl().increaseSuccessCount();
//        } else if (INFORMATION_STATUS_CODE.matcher(Integer.toString(httpResponse.getStatusLine().getStatusCode())).find()) {
//            this.getActionExecution().getActionControl().increaseSuccessCount();
//        } else if (REDIRECT_STATUS_CODE.matcher(Integer.toString(httpResponse.getStatusLine().getStatusCode())).find()) {
//            this.getActionExecution().getActionControl().increaseSuccessCount();
//        } else {
//            frameworkExecution.getFrameworkLog().log(MessageFormat.format("Status code of response {0} is not member of success status codes (1XX, 2XX, 3XX).",
//                    httpResponse.getStatusLine().getStatusCode()), Level.WARN);
//            this.getActionExecution().getActionControl().increaseErrorCount();
//        }
//    }
//
//    private void writeResponseToOutputDataset(HttpResponse httpResponse, String outputDatasetReferenceName, boolean setRuntimeVariables) {
//        // TODO: how to handle multiple content-types
//        List<Header> contentTypeHeaders = httpResponse.getHeaders().stream()
//                .filter(header -> header.getName().equals(HttpHeaders.CONTENT_TYPE))
//                .collect(Collectors.toList());
//        if (contentTypeHeaders.size() > 1) {
//            this.getActionExecution().getActionControl().logWarning("content-type",
//                    MessageFormat.format("Http response contains multiple headers ({0}) defining the content type", contentTypeHeaders.size()));
//        } else if (contentTypeHeaders.size() == 0) {
//            this.getActionExecution().getActionControl().logWarning("content-type", "Http response contains no header defining the content type. Assuming text/plain");
//            writeTextPlainResponseToOutputDataset(httpResponse, outputDatasetReferenceName);
//
//        }
//
//        if (contentTypeHeaders.stream().anyMatch(header -> header.getValue().contains(ContentType.APPLICATION_JSON.getMimeType()))) {
//            writeJSONResponseToOutputDataset(httpResponse, outputDatasetReferenceName, setRuntimeVariables);
//        } else if (contentTypeHeaders.stream().anyMatch(header -> header.getValue().contains(ContentType.TEXT_PLAIN.getMimeType()))) {
//            writeTextPlainResponseToOutputDataset(httpResponse, outputDatasetReferenceName);
//        } else {
//            this.getActionExecution().getActionControl().logWarning("content-type", "Http response contains unsupported content-type header. Response will be written to dataset as plain text.");
//            writeTextPlainResponseToOutputDataset(httpResponse, outputDatasetReferenceName);
//        }
//    }
//
//    private void writeTextPlainResponseToOutputDataset(HttpResponse httpResponse, String outputDatasetReferenceName) {
//        if (!outputDatasetReferenceName.isEmpty()) {
////			String[] parts = outputDatasetReferenceName.split("\\.");
////			String datasetName = parts[0];
////			String datasetTableName = parts[1];
////			this.getExecutionControl().getExecutionRuntime().getDatasetOperation(datasetName).resetDataset(datasetTableName);
////			this.getExecutionControl().getExecutionRuntime().getDatasetOperation(datasetName).setDatasetEntry(datasetTableName, "response", httpResponse.getEntityString());
//
//            Optional<Dataset> outputDataset = executionControl.getExecutionRuntime().getDataset(outputDatasetReferenceName);
//            outputDataset.ifPresent(dataset -> {
//                dataset.clean();
//                dataset.setDataItem("response", new Text(httpResponse.getEntityString()));
//            });
//        }
//    }
//
//    private void writeJSONResponseToOutputDataset(JsonNode jsonNode, String keyPrefix, Dataset outputDataset) {
//
//        Iterator<Map.Entry<String, JsonNode>> fields = jsonNode.fields();
//        while (fields.hasNext()) {
//            Map.Entry<String, JsonNode> field = fields.next();
//            if (field.getValue().getNodeType().equals(JsonNodeType.OBJECT)) {
//                writeJSONResponseToOutputDataset(field.getValue(), keyPrefix + field.getKey() + ".", outputDataset);
//            } else if(field.getValue().getNodeType().equals(JsonNodeType.ARRAY)) {
//                int arrayCounter = 1;
//                for (JsonNode element : field.getValue()) {
//                    writeJSONResponseToOutputDataset(element, keyPrefix + field.getKey() + "." + arrayCounter + ".",
//                            outputDataset);
//                    arrayCounter++;
//                }
//            } else if (field.getValue().getNodeType().equals(JsonNodeType.NULL)) {
//                outputDataset.setDataItem(keyPrefix + field.getKey(), new Text(""));
//            } else if (field.getValue().isValueNode()) {
//                outputDataset.setDataItem(keyPrefix + field.getKey(), new Text(field.getValue().asText()));
//            }
//            else {
//                //TODO:
//            }
//        }
//
//
//    }
//
//
//        private void writeJSONResponseToOutputDataset(HttpResponse httpResponse, String outputDatasetReferenceName, boolean setRuntimeVariables) {
//        JsonParsed jsonParsed;
//        try {
//            JsonNode jsonNode = new ObjectMapper().readTree(httpResponse.getEntityString());
//            setRuntimeVariable(jsonNode, setRuntimeVariables);
//
//            if (!outputDatasetReferenceName.isEmpty()) {
////				String[] parts = outputDatasetReferenceName.split("\\.");
////				String datasetName = parts[0];
////				String datasetTableName = parts[1];
////				this.getExecutionControl().getExecutionRuntime().getDatasetOperation(datasetName)
////						.setKeyValueDataset(datasetTableName, jsonParsed);
//
//                Optional<Dataset> outputDataset = executionControl.getExecutionRuntime().getDataset(outputDatasetReferenceName);
//                outputDataset.ifPresent(dataset -> {
//                    dataset.clean();
//                    writeJSONResponseToOutputDataset(jsonNode, "", dataset);
//                });
//            }
//        } catch (Exception e) {
//            this.getActionExecution().getActionControl().logError("json", e.getMessage());
//        }
//    }
//
//
//    private void setRuntimeVariable(JsonNode jsonNode, String keyPrefix, boolean setRuntimeVariables) {
//        if (setRuntimeVariables) {
//            try {
//                Iterator<Map.Entry<String, JsonNode>> fields = jsonNode.fields();
//                while (fields.hasNext()) {
//                    Map.Entry<String, JsonNode> field = fields.next();
//                    if (field.getValue().getNodeType().equals(JsonNodeType.OBJECT)) {
//                        setRuntimeVariable(field.getValue(), keyPrefix + field.getKey() + ".", setRuntimeVariables);
//                    } else if(field.getValue().getNodeType().equals(JsonNodeType.ARRAY)) {
//                        int arrayCounter = 1;
//                        for (JsonNode element : field.getValue()) {
//                            setRuntimeVariable(element, keyPrefix + field.getKey() + "." + arrayCounter + ".",
//                                    setRuntimeVariables);
//                            arrayCounter++;
//                        }
//                    } else if (field.getValue().getNodeType().equals(JsonNodeType.NULL)) {
//                        executionControl.getExecutionRuntime().setRuntimeVariable(actionExecution, keyPrefix + field.getKey(), "");
//
//                    } else if (field.getValue().isValueNode()) {
//                        executionControl.getExecutionRuntime().setRuntimeVariable(actionExecution, keyPrefix + field.getKey(), field.getValue().asText());
//                    } else {
//                        // TODO:
//                    }
//                }
//            } catch (Exception e) {
//                this.getActionExecution().getActionControl().increaseWarningCount();
//                this.getExecutionControl().logExecutionOutput(this.getActionExecution(), "SET_RUN_VAR", e.getMessage());
//            }
//        }
//    }
//
//    private void setRuntimeVariable(JsonNode jsonNode, boolean setRuntimeVariables) {
//        setRuntimeVariable(jsonNode, "", setRuntimeVariables);
//    }
//
//    // Getters and Setters
//    public FrameworkExecution getFrameworkExecution() {
//        return frameworkExecution;
//    }
//
//    public void setFrameworkExecution(FrameworkExecution frameworkExecution) {
//        this.frameworkExecution = frameworkExecution;
//    }
//
//    public ExecutionControl getExecutionControl() {
//        return executionControl;
//    }
//
//    public void setExecutionControl(ExecutionControl executionControl) {
//        this.executionControl = executionControl;
//    }
//
//    public ActionExecution getActionExecution() {
//        return actionExecution;
//    }
//
//    public void setActionExecution(ActionExecution actionExecution) {
//        this.actionExecution = actionExecution;
//    }
//
//    public HashMap<String, ActionParameterOperation> getActionParameterOperationMap() {
//        return actionParameterOperationMap;
//    }
//
//    public void setActionParameterOperationMap(HashMap<String, ActionParameterOperation> actionParameterOperationMap) {
//        this.actionParameterOperationMap = actionParameterOperationMap;
//    }
//
//    public ActionParameterOperation getActionParameterOperation(String key) {
//        return this.getActionParameterOperationMap().get(key);
//    }
//
//    public ActionParameterOperation getRequestName() {
//        return requestName;
//    }
//
//    public void setRequestName(ActionParameterOperation requestName) {
//        this.requestName = requestName;
//    }
//
//    public ActionParameterOperation getSetRuntimeVariables() {
//        return setRuntimeVariables;
//    }
//
//    public void setSetRuntimeVariables(ActionParameterOperation setRuntimeVariables) {
//        this.setRuntimeVariables = setRuntimeVariables;
//    }
//
//    public ActionParameterOperation getRequestType() {
//        return requestType;
//    }
//
//    public void setRequestType(ActionParameterOperation requestType) {
//        this.requestType = requestType;
//    }
//
//    public ActionParameterOperation getRequestBody() {
//        return requestBody;
//    }
//
//    public void setRequestBody(ActionParameterOperation requestBody) {
//        this.requestBody = requestBody;
//    }
//
//    public ActionParameterOperation getSetDataset() {
//        return setDataset;
//    }
//
//    public void setSetDataset(ActionParameterOperation setDataset) {
//        this.setDataset = setDataset;
//    }
//
//}