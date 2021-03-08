package io.metadew.iesi.script.action.eval;

import io.metadew.iesi.datatypes.DataType;
import io.metadew.iesi.datatypes.text.Text;
import io.metadew.iesi.metadata.definition.action.ActionParameter;
import io.metadew.iesi.script.action.ActionTypeExecution;
import io.metadew.iesi.script.execution.ActionExecution;
import io.metadew.iesi.script.execution.ExecutionControl;
import io.metadew.iesi.script.execution.ScriptExecution;
import io.metadew.iesi.script.operation.ActionParameterOperation;
import io.metadew.iesi.script.service.ConditionService;
import lombok.extern.log4j.Log4j2;

import javax.script.ScriptException;
import java.text.MessageFormat;

@Log4j2
public class EvalExecuteExpression extends ActionTypeExecution {

    private String expression;

    public EvalExecuteExpression(ExecutionControl executionControl, ScriptExecution scriptExecution, ActionExecution actionExecution) {
        super(executionControl, scriptExecution, actionExecution);
    }

    public void prepare() {
        // Reset Parameters
        ActionParameterOperation evaluationExpression = new ActionParameterOperation(this.getExecutionControl(),
                this.getActionExecution(), this.getActionExecution().getAction().getType(), "expression");

        // Get Parameters
        for (ActionParameter actionParameter : this.getActionExecution().getAction().getParameters()) {
            if (actionParameter.getMetadataKey().getParameterName().equalsIgnoreCase("expression")) {
                evaluationExpression.setInputValue(actionParameter.getValue(), getExecutionControl().getExecutionRuntime());
            }
        }

        // Create parameter list
        this.getActionParameterOperationMap().put("expression", evaluationExpression);
        this.expression = convertExpression(evaluationExpression.getValue());
    }

    protected boolean executeAction() throws InterruptedException, ScriptException {
        if (ConditionService.getInstance().evaluateCondition(expression, getExecutionControl().getExecutionRuntime(), getActionExecution())) {
            getActionExecution().getActionControl().increaseSuccessCount();
            return true;
        } else {
            getActionExecution().getActionControl().logOutput("action.error", "Expression " + expression + " evaluated to false");
            getActionExecution().getActionControl().increaseErrorCount();
            return false;
        }
    }

    @Override
    protected String getKeyword() {
        return "eval.executeExpression";
    }

    private String convertExpression(DataType expression) {
        if (expression instanceof Text) {
            return expression.toString();
        } else {
            log.warn(MessageFormat.format(this.getActionExecution().getAction().getType() + " does not accept {0} as type for expression",
                    expression.getClass().getSimpleName()));
            return expression.toString();
        }
    }

}