package io.metadew.iesi.script.action.eval;

import io.metadew.iesi.datatypes.DataType;
import io.metadew.iesi.datatypes.text.Text;
import io.metadew.iesi.script.action.ActionTypeExecution;
import io.metadew.iesi.script.execution.ActionExecution;
import io.metadew.iesi.script.execution.ExecutionControl;
import io.metadew.iesi.script.execution.ScriptExecution;
import io.metadew.iesi.script.service.ConditionService;
import lombok.extern.log4j.Log4j2;

import javax.script.ScriptException;
import java.text.MessageFormat;

@Log4j2
public class EvalExecuteExpression extends ActionTypeExecution {

    private static final String EXPRESSION_KEY = "expression";
    private String expression;

    public EvalExecuteExpression(ExecutionControl executionControl, ScriptExecution scriptExecution, ActionExecution actionExecution) {
        super(executionControl, scriptExecution, actionExecution);
    }

    public void prepareAction() {
        this.expression = convertExpression(getParameterResolvedValue(EXPRESSION_KEY));
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