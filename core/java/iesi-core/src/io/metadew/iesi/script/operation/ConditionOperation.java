package io.metadew.iesi.script.operation;

import io.metadew.iesi.runtime.definition.LookupResult;
import io.metadew.iesi.script.execution.ActionExecution;
import io.metadew.iesi.script.execution.ExecutionControl;
import org.apache.commons.jexl2.Expression;
import org.apache.commons.jexl2.JexlContext;
import org.apache.commons.jexl2.JexlEngine;
import org.apache.commons.jexl2.MapContext;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

/**
 * Operation to manage the conditions that have been defined in the script
 *
 * @author peter.billen
 */
public class ConditionOperation {

    private ActionExecution actionExecution;
    private ExecutionControl executionControl;
    private String inputValue;
    private String value;
    private String syntax;

    private static final JexlEngine jexl = new JexlEngine();

    static {
        jexl.setCache(512);
        jexl.setLenient(false);
        jexl.setSilent(false);
    }

    public ConditionOperation() {

    }

    public ConditionOperation(ActionExecution actionExecution, String inputValue) {
        this.setActionExecution(actionExecution);
        this.setExecutionControl(actionExecution.getExecutionControl());
        this.setInputValue(inputValue);
    }

    public ConditionOperation(ExecutionControl executionControl, String inputValue) {
        this.setExecutionControl(executionControl);
        this.setInputValue(inputValue);
    }

    public boolean evaluateCondition() throws ScriptException {
        boolean result = false;
        switch (this.getSyntax()) {
            case "jexl":
                result = this.evaluateJexlCondition(this.getValue());
                break;
            case "javascript":
            case "js":
                result = this.evaluateJavaScriptCondition(this.getValue());
                break;
            default:
                result = false;
                break;
        }
        return result;
    }

    public boolean evaluateJexlCondition(String condition) {
        Expression expression = jexl.createExpression(condition);
        JexlContext context = new MapContext();

        String evaluation = expression.evaluate(context).toString().toLowerCase();

        return this.getEvaluationResult(evaluation);

    }

    public boolean evaluateJavaScriptCondition(String condition) throws ScriptException {
        ScriptEngineManager scriptEngineManager = new ScriptEngineManager();
        ScriptEngine scriptEngine = scriptEngineManager.getEngineByName("JavaScript");

        String evaluation = scriptEngine.eval(condition).toString().toString().toLowerCase();

        return this.getEvaluationResult(evaluation);
    }

    private boolean getEvaluationResult(String evaluation) {
        boolean result = false;
        switch (evaluation) {
            case "true":
            case "1":
            case "yes":
            case "y":
                result = true;
                break;
            case "false":
            case "0":
            case "no":
            case "n":
                result = false;
                break;
            default:
                result = false;
                break;
        }
        return result;
    }

    public String getInputValue() {
        return inputValue;
    }

    public void setInputValue(String inputValue) {
        this.inputValue = inputValue;
        this.setValue(inputValue);

        // Cross concept lookup
        LookupResult lookupResult = this.getExecutionControl().getExecutionRuntime()
                .resolveConceptLookup(this.getExecutionControl(), this.getValue(), true);
        this.setValue(lookupResult.getValue());
        this.setSyntax(lookupResult.getContext());
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = this.getExecutionControl().getExecutionRuntime()
                .resolveVariables(this.getActionExecution(), value);
    }

    public ActionExecution getActionExecution() {
        return actionExecution;
    }

    public void setActionExecution(ActionExecution actionExecution) {
        this.actionExecution = actionExecution;
    }

    public String getSyntax() {
        return syntax;
    }

    public void setSyntax(String syntax) {
        if (syntax == null) {
            this.syntax = "jexl";
        } else {
            this.syntax = syntax.toLowerCase();
        }
    }

    public ExecutionControl getExecutionControl() {
        return executionControl;
    }

    public void setExecutionControl(ExecutionControl executionControl) {
        this.executionControl = executionControl;
    }
}