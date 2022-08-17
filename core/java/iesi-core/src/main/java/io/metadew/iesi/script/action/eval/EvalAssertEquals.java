package io.metadew.iesi.script.action.eval;

import io.metadew.iesi.SpringContext;
import io.metadew.iesi.datatypes.DataType;
import io.metadew.iesi.datatypes.DataTypeHandler;
import io.metadew.iesi.datatypes.dataset.Dataset;
import io.metadew.iesi.datatypes.template.TemplateService;
import io.metadew.iesi.metadata.definition.template.Template;
import io.metadew.iesi.script.action.ActionTypeExecution;
import io.metadew.iesi.script.execution.ActionExecution;
import io.metadew.iesi.script.execution.ExecutionControl;
import io.metadew.iesi.script.execution.ScriptExecution;

public class EvalAssertEquals extends ActionTypeExecution {

    private static final String EXPECTED_KEY = "expected";
    private static final String ACTUAL_KEY = "actual";

    // Parameters
    private DataType expectedValue;
    private DataType actualValue;

    private final DataTypeHandler dataTypeHandler = SpringContext.getBean(DataTypeHandler.class);


    public EvalAssertEquals(ExecutionControl executionControl,
                            ScriptExecution scriptExecution, ActionExecution actionExecution) {
        super(executionControl, scriptExecution, actionExecution);
    }

    public void prepareAction() {
        expectedValue = getParameterResolvedValue(EXPECTED_KEY);
        actualValue = getParameterResolvedValue(ACTUAL_KEY);

    }

    protected boolean executeAction() throws InterruptedException {
        boolean evaluation;
        if (expectedValue instanceof Template && actualValue instanceof Dataset) {
            evaluation = ((TemplateService) dataTypeHandler.getDataTypeService(Template.class)).matches(actualValue, (Template) expectedValue, getExecutionControl().getExecutionRuntime());
        } else {
            evaluation = dataTypeHandler.equals(expectedValue, actualValue, getExecutionControl().getExecutionRuntime());
        }

        if (evaluation) {
            getActionExecution().getActionControl().increaseSuccessCount();
        } else {
            getActionExecution().getActionControl().logOutput("action.error", "Actual value '" + actualValue + "' does not match '" + expectedValue + "'");
            getActionExecution().getActionControl().increaseErrorCount();
        }
        return evaluation;
    }

    @Override
    protected String getKeyword() {
        return "eval.assertEquals";
    }

}