package io.metadew.iesi.script.action.eval;

import io.metadew.iesi.datatypes.DataType;
import io.metadew.iesi.datatypes.text.Text;
import io.metadew.iesi.metadata.definition.action.ActionParameter;
import io.metadew.iesi.script.action.ActionTypeExecution;
import io.metadew.iesi.script.execution.ActionExecution;
import io.metadew.iesi.script.execution.ExecutionControl;
import io.metadew.iesi.script.execution.ScriptExecution;
import io.metadew.iesi.script.operation.ActionParameterOperation;
import io.metadew.iesi.script.operation.ConditionOperation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.script.ScriptException;
import java.text.MessageFormat;


public class EvalExecuteExpression extends ActionTypeExecution {

    // Parameters
    private ActionParameterOperation evaluationExpression;
    private static final Logger LOGGER = LogManager.getLogger();


    public EvalExecuteExpression(ExecutionControl executionControl,
                                 ScriptExecution scriptExecution, ActionExecution actionExecution) {
        super(executionControl, scriptExecution, actionExecution);
    }


    public void prepare() {
        // Reset Parameters
        this.setEvaluationExpression(
                new ActionParameterOperation(this.getExecutionControl(),
                        this.getActionExecution(), this.getActionExecution().getAction().getType(), "expression"));

        // Get Parameters
        for (ActionParameter actionParameter : this.getActionExecution().getAction().getParameters()) {
            if (actionParameter.getMetadataKey().getParameterName().equalsIgnoreCase("expression")) {
                this.getEvaluationExpression().setInputValue(actionParameter.getValue(), getExecutionControl().getExecutionRuntime());
            }
        }

        // Create parameter list
        this.getActionParameterOperationMap().put("expression", this.getEvaluationExpression());
    }


    private String convertExpression(DataType expression) {
        if (expression instanceof Text) {
            return expression.toString();
        } else {
            LOGGER.warn(MessageFormat.format(this.getActionExecution().getAction().getType() + " does not accept {0} as type for expression",
                    expression.getClass()));
            return expression.toString();
        }
    }

    protected boolean executeAction() throws InterruptedException {
        String expression = convertExpression(getEvaluationExpression().getValue());
        boolean evaluation;
        ConditionOperation conditionOperation = new ConditionOperation(this.getActionExecution(), expression);
        try {
            evaluation = conditionOperation.evaluateCondition();
        } catch (ScriptException exception) {
            evaluation = false;
            this.getActionExecution().getActionControl().logWarning("expression", expression);
            this.getActionExecution().getActionControl().logWarning("expression.error", exception.getMessage());
        }
        if (evaluation) {
            this.getActionExecution().getActionControl().increaseSuccessCount();
        } else {
            this.getActionExecution().getActionControl().increaseErrorCount();
        }
        return true;
    }

    public ActionParameterOperation getEvaluationExpression() {
        return evaluationExpression;
    }

    public void setEvaluationExpression(ActionParameterOperation evaluationExpression) {
        this.evaluationExpression = evaluationExpression;
    }

}