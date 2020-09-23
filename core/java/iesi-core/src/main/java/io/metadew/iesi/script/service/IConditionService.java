package io.metadew.iesi.script.service;

import io.metadew.iesi.script.execution.ActionExecution;
import io.metadew.iesi.script.execution.ExecutionRuntime;

import javax.script.ScriptException;

public interface IConditionService {

    public boolean evaluateCondition(String expression, String syntax, ExecutionRuntime executionRuntime, ActionExecution actionExecution) throws ScriptException;

    public boolean evaluateCondition(String expression, ExecutionRuntime executionRuntime, ActionExecution actionExecution) throws ScriptException;

}
