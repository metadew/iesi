package io.metadew.iesi.script.action.eval;

import io.metadew.iesi.datatypes.DataType;
import io.metadew.iesi.datatypes.DataTypeHandler;
import io.metadew.iesi.datatypes.array.Array;
import io.metadew.iesi.datatypes.template.TemplateService;
<<<<<<< HEAD
=======
import io.metadew.iesi.datatypes.text.Text;
import io.metadew.iesi.metadata.definition.action.ActionParameter;
>>>>>>> master
import io.metadew.iesi.metadata.definition.template.Template;
import io.metadew.iesi.script.action.ActionTypeExecution;
import io.metadew.iesi.script.execution.ActionExecution;
import io.metadew.iesi.script.execution.ExecutionControl;
import io.metadew.iesi.script.execution.ScriptExecution;
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
        //template = convertTemplate(templateActionParameterOperation.getValue());
        list = convertList(getParameterResolvedValue(LIST_KEY));
        value = getParameterResolvedValue(TEMPLATE_KEY);
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
        } else if (expectedValue instanceof Text) {
            return getExecutionControl().getExecutionRuntime()
                    .getArray(((Text) expectedValue).getString())
                    .orElseThrow(() -> new RuntimeException(
                            String.format("Cannot find list with reference name %s",
                                    ((Text) expectedValue).getString())));
        } else {
            throw new RuntimeException(MessageFormat.format(getActionExecution().getAction().getType() + " does not accept {0} as type for expectedValue",
                    expectedValue.getClass()));
        }
    }

}