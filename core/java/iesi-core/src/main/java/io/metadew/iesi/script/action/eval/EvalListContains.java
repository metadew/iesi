package io.metadew.iesi.script.action.eval;

import io.metadew.iesi.datatypes.DataType;
import io.metadew.iesi.datatypes.DataTypeHandler;
import io.metadew.iesi.datatypes.array.Array;
import io.metadew.iesi.datatypes.template.TemplateService;
import io.metadew.iesi.metadata.definition.action.ActionParameter;
import io.metadew.iesi.metadata.definition.template.Template;
import io.metadew.iesi.script.action.ActionTypeExecution;
import io.metadew.iesi.script.execution.ActionExecution;
import io.metadew.iesi.script.execution.ExecutionControl;
import io.metadew.iesi.script.execution.ScriptExecution;
import io.metadew.iesi.script.operation.ActionParameterOperation;
import lombok.extern.log4j.Log4j2;

import java.text.MessageFormat;

@Log4j2
public class EvalListContains extends ActionTypeExecution {

    private static final String LIST_KEY = "list";
    private static final String TEMPLATE_KEY = "value";

    // Parameters
    private Array list;
    private DataType value;

    public EvalListContains(ExecutionControl executionControl,
                            ScriptExecution scriptExecution, ActionExecution actionExecution) {
        super(executionControl, scriptExecution, actionExecution);
    }

    public void prepare() {
        ActionParameterOperation listActionParameterOperation = new ActionParameterOperation(getExecutionControl(), getActionExecution(), getActionExecution().getAction().getType(), LIST_KEY);
        ActionParameterOperation valueActionParameterOperation = new ActionParameterOperation(getExecutionControl(), getActionExecution(), getActionExecution().getAction().getType(), TEMPLATE_KEY);

        // Get Parameters
        for (ActionParameter actionParameter : getActionExecution().getAction().getParameters()) {
            if (actionParameter.getMetadataKey().getParameterName().equalsIgnoreCase(LIST_KEY)) {
                listActionParameterOperation.setInputValue(actionParameter.getValue(), getExecutionControl().getExecutionRuntime());
            } else if (actionParameter.getMetadataKey().getParameterName().equalsIgnoreCase(TEMPLATE_KEY)) {
                valueActionParameterOperation.setInputValue(actionParameter.getValue(), getExecutionControl().getExecutionRuntime());
            }
        }

        //template = convertTemplate(templateActionParameterOperation.getValue());
        list = convertList(listActionParameterOperation.getValue());
        value = valueActionParameterOperation.getValue();
        // Create parameter list
        getActionParameterOperationMap().put(LIST_KEY, listActionParameterOperation);
        getActionParameterOperationMap().put(TEMPLATE_KEY, valueActionParameterOperation);
    }

    protected boolean executeAction() throws InterruptedException {
        boolean result = executeOperation(list, value);
        if (!result) {
            getActionExecution().getActionControl().logOutput("action.error", "List " + list.toString() + " does not contain " + value.toString());
            getActionExecution().getActionControl().increaseErrorCount();
        }
        return result;
    }

    @Override
    protected String getKeyword() {
        return "eval.listContains";
    }

    private boolean executeOperation(Array list, DataType element) throws InterruptedException {
        if (element instanceof Template) {
            for (DataType dataType : list.getList()) {
                if (TemplateService.getInstance().matches(dataType, (Template) element, getExecutionControl().getExecutionRuntime())) {
                    return true;
                }
            }
            return false;
        } else {
            for (DataType dataType : list.getList()) {
                if (DataTypeHandler.getInstance().equals(dataType, element, getExecutionControl().getExecutionRuntime())) {
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
            throw new RuntimeException(MessageFormat.format(getActionExecution().getAction().getType() + " does not accept {0} as type for expectedValue",
                    expectedValue.getClass()));
        }
    }

}