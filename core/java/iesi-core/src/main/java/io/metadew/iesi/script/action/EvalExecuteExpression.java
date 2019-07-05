package io.metadew.iesi.script.action;

import io.metadew.iesi.datatypes.DataType;
import io.metadew.iesi.datatypes.Text;
import io.metadew.iesi.framework.execution.FrameworkExecution;
import io.metadew.iesi.metadata.definition.ActionParameter;
import io.metadew.iesi.script.execution.ActionExecution;
import io.metadew.iesi.script.execution.ExecutionControl;
import io.metadew.iesi.script.execution.ScriptExecution;
import io.metadew.iesi.script.operation.ActionParameterOperation;
import io.metadew.iesi.script.operation.ConditionOperation;
import org.apache.logging.log4j.Level;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.MessageFormat;
import java.util.HashMap;


public class EvalExecuteExpression {

    private ActionExecution actionExecution;
    private FrameworkExecution frameworkExecution;
    private ExecutionControl executionControl;

    // Parameters
    private ActionParameterOperation evaluationExpression;
    private HashMap<String, ActionParameterOperation> actionParameterOperationMap;

    // Constructors
    public EvalExecuteExpression() {

    }

    public EvalExecuteExpression(FrameworkExecution frameworkExecution, ExecutionControl executionControl,
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
        this.setEvaluationExpression(
                new ActionParameterOperation(this.getFrameworkExecution(), this.getExecutionControl(),
                        this.getActionExecution(), this.getActionExecution().getAction().getType(), "expression"));

        // Get Parameters
        for (ActionParameter actionParameter : this.getActionExecution().getAction().getParameters()) {
            if (actionParameter.getName().equalsIgnoreCase("expression")) {
                this.getEvaluationExpression().setInputValue(actionParameter.getValue());
            }
        }

        // Create parameter list
        this.getActionParameterOperationMap().put("expression", this.getEvaluationExpression());
    }

    public boolean execute() {
        try {
            String expression = convertExpression(getEvaluationExpression().getValue());
            return evaluatedExpression(expression);
        } catch (

                Exception e) {
            StringWriter StackTrace = new StringWriter();
            e.printStackTrace(new PrintWriter(StackTrace));

            this.getActionExecution().getActionControl().increaseErrorCount();

            this.getActionExecution().getActionControl().logOutput("exception", e.getMessage());
            this.getActionExecution().getActionControl().logOutput("stacktrace", StackTrace.toString());

            return false;
        }

    }

    private String convertExpression(DataType expression) {
        if (expression instanceof Text) {
            return expression.toString();
        } else {
            this.getFrameworkExecution().getFrameworkLog().log(MessageFormat.format(this.getActionExecution().getAction().getType() + " does not accept {0} as type for expression",
                    expression.getClass()), Level.WARN);
            return expression.toString();
        }
    }

    private boolean evaluatedExpression(String expression) {
        boolean evaluation;
        ConditionOperation conditionOperation = new ConditionOperation(this.getActionExecution(), expression);
        try {
            evaluation = conditionOperation.evaluateCondition();
        } catch (Exception exception) {
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

    public ActionParameterOperation getEvaluationExpression() {
        return evaluationExpression;
    }

    public void setEvaluationExpression(ActionParameterOperation evaluationExpression) {
        this.evaluationExpression = evaluationExpression;
    }

    public HashMap<String, ActionParameterOperation> getActionParameterOperationMap() {
        return actionParameterOperationMap;
    }

    public void setActionParameterOperationMap(HashMap<String, ActionParameterOperation> actionParameterOperationMap) {
        this.actionParameterOperationMap = actionParameterOperationMap;
    }

}