package io.metadew.iesi.script.service;

import io.metadew.iesi.script.execution.ActionExecution;
import io.metadew.iesi.script.execution.ExecutionRuntime;
import io.metadew.iesi.script.execution.LookupResult;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.jexl2.Expression;
import org.apache.commons.jexl2.JexlContext;
import org.apache.commons.jexl2.JexlEngine;
import org.apache.commons.jexl2.MapContext;
import org.springframework.stereotype.Service;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

@Service
@Log4j2
public class ConditionService implements IConditionService {

    private final JexlEngine jexl;

    private ConditionService() {
        jexl = new JexlEngine();
        jexl.setCache(512);
        jexl.setLenient(false);
        jexl.setSilent(false);
    }

    public boolean evaluateCondition(String expression, String syntax, ExecutionRuntime executionRuntime, ActionExecution actionExecution) throws ScriptException {
        String resolvedExpression = resolveExpression(expression, executionRuntime, actionExecution);
        log.info("action.condition.resolved=" + resolvedExpression);
        switch (syntax) {
            case "javascript":
            case "js":
                return evaluateJavaScriptCondition(resolvedExpression);
            case "jexl":
            default:
                return evaluateJexlCondition(resolvedExpression);
        }
    }

    public boolean evaluateCondition(String expression, ExecutionRuntime executionRuntime, ActionExecution actionExecution) throws ScriptException {
        return evaluateCondition(expression, "jexl", executionRuntime, actionExecution);
    }

    private boolean evaluateJexlCondition(String condition) {
        Expression expression = jexl.createExpression(condition);
        JexlContext context = new MapContext();

        String evaluation = expression.evaluate(context).toString().toLowerCase();

        return getEvaluationResult(evaluation);

    }

    private boolean evaluateJavaScriptCondition(String condition) throws ScriptException {
        ScriptEngineManager scriptEngineManager = new ScriptEngineManager();
        ScriptEngine scriptEngine = scriptEngineManager.getEngineByName("JavaScript");
        String evaluation = scriptEngine.eval(condition).toString().toLowerCase();
        return getEvaluationResult(evaluation);
    }

    private boolean getEvaluationResult(String evaluation) {
        switch (evaluation) {
            case "true":
            case "1":
            case "yes":
            case "y":
                return true;
            case "false":
            case "0":
            case "no":
            case "n":
            default:
                return false;
        }
    }

    private String resolveExpression(String expression, ExecutionRuntime executionRuntime, ActionExecution actionExecution) {
        expression = executionRuntime.resolveVariables(actionExecution, expression);
        LookupResult lookupResult = executionRuntime.resolveConceptLookup(expression);
        return lookupResult.getValue();
    }

}