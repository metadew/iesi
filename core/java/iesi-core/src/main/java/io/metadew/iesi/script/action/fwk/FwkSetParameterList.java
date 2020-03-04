package io.metadew.iesi.script.action.fwk;

import io.metadew.iesi.datatypes.DataType;
import io.metadew.iesi.datatypes.DataTypeHandler;
import io.metadew.iesi.datatypes.array.Array;
import io.metadew.iesi.datatypes.text.Text;
import io.metadew.iesi.metadata.definition.action.ActionParameter;
import io.metadew.iesi.script.execution.ActionExecution;
import io.metadew.iesi.script.execution.ExecutionControl;
import io.metadew.iesi.script.execution.ScriptExecution;
import io.metadew.iesi.script.operation.ActionParameterOperation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.MessageFormat;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FwkSetParameterList {

    private ActionExecution actionExecution;
    private ExecutionControl executionControl;

    private final Pattern keyValuePattern = Pattern.compile("\\s*(?<parameter>.+)\\s*=\\s*(?<value>.+)\\s*");

    // Parameters
    private ActionParameterOperation parameterList;
    private HashMap<String, ActionParameterOperation> actionParameterOperationMap;

    private static final Logger LOGGER = LogManager.getLogger();

    public FwkSetParameterList(ExecutionControl executionControl, ScriptExecution scriptExecution, ActionExecution actionExecution) {
        this.setExecutionControl(executionControl);
        this.setActionExecution(actionExecution);
        this.setActionParameterOperationMap(new HashMap<>());
    }

    public void prepare()  {
        // Reset Parameters
        this.setParameterList(new ActionParameterOperation(this.getExecutionControl(), this.getActionExecution(),
                this.getActionExecution().getAction().getType(), "list"));

        // Get Parameters
        for (ActionParameter actionParameter : this.getActionExecution().getAction().getParameters()) {
            if (actionParameter.getMetadataKey().getParameterName().equalsIgnoreCase("list"))
                this.getParameterList().setInputValue(actionParameter.getValue(), executionControl.getExecutionRuntime());
        }

        //Create parameter list
        this.getActionParameterOperationMap().put("list", this.getParameterList());
    }

    public boolean execute() throws InterruptedException {
        try {
            return executionOperation();
        } catch (InterruptedException e) {
            throw (e);
        } catch (Exception e) {
            StringWriter StackTrace = new StringWriter();
            e.printStackTrace(new PrintWriter(StackTrace));

            this.getActionExecution().getActionControl().increaseErrorCount();

            this.getActionExecution().getActionControl().logOutput("exception", e.getMessage());
            this.getActionExecution().getActionControl().logOutput("stacktrace", StackTrace.toString());

            return false;
        }

    }

    private boolean executionOperation() throws InterruptedException {
        Map<String, String> list = convertList(getParameterList().getValue());
        for (Map.Entry<String, String> parameter : list.entrySet()) {
            executionControl.getExecutionRuntime().setRuntimeVariable(actionExecution, parameter.getKey(), parameter.getValue());
        }
        return true;
    }

    private Map<String, String> convertList(DataType list) {
        Map<String, String> parameterMap = new HashMap<>();
        if (list instanceof Text) {
            Arrays.stream(list.toString().split(","))
                    .forEach(parameterEntry -> parameterMap.putAll(convertParameterEntry(DataTypeHandler.getInstance().resolve(parameterEntry, executionControl.getExecutionRuntime()))));
            return parameterMap;
        } else if (list instanceof Array) {
            for (DataType parameterEntry : ((Array) list).getList()) {
                parameterMap.putAll(convertParameterEntry(parameterEntry));
            }
            return parameterMap;
        } else {
            LOGGER.warn(MessageFormat.format(this.getActionExecution().getAction().getType() + " does not accept {0} as type for list",
                    list.getClass()));
            return parameterMap;
        }
    }

    private Map<String, String> convertParameterEntry(DataType parameterEntry) {
        Map<String, String> parameterMap = new HashMap<>();
        if (parameterEntry instanceof Text) {
            Matcher matcher = keyValuePattern.matcher(parameterEntry.toString());
            if (matcher.find()) {
                parameterMap.put(matcher.group("parameter"), matcher.group("value"));
            } else {
                LOGGER.warn(MessageFormat.format(this.getActionExecution().getAction().getType() + ": parameter entry ''{0}'' does not follow correct syntax",
                        parameterEntry));
            }
            return parameterMap;
        } else {
            LOGGER.warn(MessageFormat.format(this.getActionExecution().getAction().getType() + " does not accept {0} as type for parameter entry",
                    parameterEntry.getClass()));
            return parameterMap;
        }
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

    public ActionParameterOperation getParameterList() {
        return parameterList;
    }

    public void setParameterList(ActionParameterOperation parameterList) {
        this.parameterList = parameterList;
    }

}