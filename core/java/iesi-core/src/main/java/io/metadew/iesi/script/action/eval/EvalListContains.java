package io.metadew.iesi.script.action.eval;

import io.metadew.iesi.datatypes.DataType;
import io.metadew.iesi.datatypes.DataTypeHandler;
import io.metadew.iesi.datatypes.array.Array;
import io.metadew.iesi.datatypes.template.TemplateService;
import io.metadew.iesi.metadata.definition.action.ActionParameter;
import io.metadew.iesi.metadata.definition.template.Template;
import io.metadew.iesi.script.execution.ActionExecution;
import io.metadew.iesi.script.execution.ExecutionControl;
import io.metadew.iesi.script.execution.ScriptExecution;
import io.metadew.iesi.script.operation.ActionParameterOperation;
import lombok.extern.log4j.Log4j2;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;

@Log4j2
public class EvalListContains {

    private static final String LIST_KEY = "list";
    private static final String TEMPLATE_KEY = "template";

    private ActionExecution actionExecution;
    private ExecutionControl executionControl;

    // Parameters
    private DataType template;
    private Array list;
    private Map<String, ActionParameterOperation> actionParameterOperationMap;

    public EvalListContains(ExecutionControl executionControl,
                            ScriptExecution scriptExecution, ActionExecution actionExecution) {
        this.executionControl = executionControl;
        this.actionExecution = actionExecution;
        this.actionParameterOperationMap = new HashMap<>();
    }

    public void prepare() {
        ActionParameterOperation listActionParameterOperation = new ActionParameterOperation(executionControl, actionExecution, actionExecution.getAction().getType(), LIST_KEY);
        ActionParameterOperation templateActionParameterOperation = new ActionParameterOperation(executionControl, actionExecution, actionExecution.getAction().getType(), TEMPLATE_KEY);

        // Get Parameters
        for (ActionParameter actionParameter : actionExecution.getAction().getParameters()) {
            if (actionParameter.getMetadataKey().getParameterName().equalsIgnoreCase(LIST_KEY)) {
                listActionParameterOperation.setInputValue(actionParameter.getValue(), executionControl.getExecutionRuntime());
            } else if (actionParameter.getMetadataKey().getParameterName().equalsIgnoreCase(TEMPLATE_KEY)) {
                templateActionParameterOperation.setInputValue(actionParameter.getValue(), executionControl.getExecutionRuntime());
            }
        }

        //template = convertTemplate(templateActionParameterOperation.getValue());
        list = convertList(listActionParameterOperation.getValue());

        // Create parameter list
        actionParameterOperationMap.put(LIST_KEY, listActionParameterOperation);
        actionParameterOperationMap.put(TEMPLATE_KEY, templateActionParameterOperation);
    }

    public boolean execute() throws InterruptedException {
        try {

            return executeOperation(list, template);
            //return compare(expected, actual);
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

    private boolean executeOperation(Array list, DataType element) throws InterruptedException {
        if (element instanceof Template) {
            for (DataType dataType : list.getList()) {
                if (TemplateService.getInstance().matches(dataType, (Template) element, executionControl.getExecutionRuntime())) {
                    return true;
                }
            }
            return false;
        } else {
            for (DataType dataType : list.getList()) {
                if (DataTypeHandler.getInstance().equals(dataType, element, executionControl.getExecutionRuntime())) {
                    return true;
                }
            }
            return false;
        }
    }

    private Array convertList(DataType expectedValue) {
        if (expectedValue instanceof Array) {
            return (Array) expectedValue;
        } else {
            throw new RuntimeException(MessageFormat.format(actionExecution.getAction().getType() + " does not accept {0} as type for expectedValue",
                    expectedValue.getClass()));
        }
    }

    public Map<String, ActionParameterOperation> getActionParameterOperationMap() {
        return actionParameterOperationMap;
    }

}