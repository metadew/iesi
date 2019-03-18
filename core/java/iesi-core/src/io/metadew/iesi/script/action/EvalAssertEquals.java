package io.metadew.iesi.script.action;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.HashMap;

import io.metadew.iesi.framework.execution.FrameworkExecution;
import io.metadew.iesi.metadata.definition.ActionParameter;
import io.metadew.iesi.script.execution.ActionExecution;
import io.metadew.iesi.script.execution.ExecutionControl;
import io.metadew.iesi.script.execution.ScriptExecution;
import io.metadew.iesi.script.operation.ActionParameterOperation;

public class EvalAssertEquals {

	private ActionExecution actionExecution;
	private FrameworkExecution frameworkExecution;
	private ExecutionControl executionControl;

	// Parameters
	private ActionParameterOperation expectedValue;
	private ActionParameterOperation actualValue;
	private HashMap<String, ActionParameterOperation> actionParameterOperationMap;

	// Constructors
	public EvalAssertEquals() {

	}

	public EvalAssertEquals(FrameworkExecution frameworkExecution, ExecutionControl executionControl,
			ScriptExecution scriptExecution, ActionExecution actionExecution) {
		this.init(frameworkExecution, executionControl, scriptExecution, actionExecution);
	}

	public void init(FrameworkExecution frameworkExecution, ExecutionControl executionControl, ScriptExecution scriptExecution,
			ActionExecution actionExecution) {
		this.setFrameworkExecution(frameworkExecution);
		this.setExecutionControl(executionControl);
		this.setActionExecution(actionExecution);
		this.setActionParameterOperationMap(new HashMap<String, ActionParameterOperation>());
	}

	public void prepare() {
		// Reset Parameters
		this.setExpectedValue(new ActionParameterOperation(this.getFrameworkExecution(), this.getExecutionControl(),
				this.getActionExecution(), this.getActionExecution().getAction().getType(), "expected"));
		this.setActualValue(new ActionParameterOperation(this.getFrameworkExecution(), this.getExecutionControl(),
				this.getActionExecution(), this.getActionExecution().getAction().getType(), "actual"));

		// Get Parameters
		for (ActionParameter actionParameter : this.getActionExecution().getAction().getParameters()) {
			if (actionParameter.getName().equalsIgnoreCase("expected")) {
				this.getExpectedValue().setInputValue(actionParameter.getValue());
			} else if (actionParameter.getName().equalsIgnoreCase("actual")) {
				this.getActualValue().setInputValue(actionParameter.getValue());
			}
		}

		// Create parameter list
		this.getActionParameterOperationMap().put("expected", this.getExpectedValue());
		this.getActionParameterOperationMap().put("actual", this.getActualValue());
	}
	
	//
	public boolean execute() {
		try {
			// Run the action
			boolean evaluation = false;
			if (this.getExpectedValue().getValue().equals(this.getActualValue().getValue())) evaluation = true;

			if (evaluation) {
				if (this.getActionExecution().getAction().getErrorExpected().equalsIgnoreCase("y")) {
					this.getActionExecution().getActionControl().increaseErrorCount();
				} else {
					this.getActionExecution().getActionControl().increaseSuccessCount();
				}
			} else {
				if (this.getActionExecution().getAction().getErrorExpected().equalsIgnoreCase("n")) {
					this.getActionExecution().getActionControl().increaseErrorCount();
				} else {
					this.getActionExecution().getActionControl().increaseSuccessCount();
				}
			}

			return true;
		} catch (Exception e) {
			StringWriter StackTrace = new StringWriter();
			e.printStackTrace(new PrintWriter(StackTrace));

			this.getActionExecution().getActionControl().increaseErrorCount();

			this.getActionExecution().getActionControl().logOutput("exception",e.getMessage());
			this.getActionExecution().getActionControl().logOutput("stacktrace",StackTrace.toString());

			return false;
		}

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

	public HashMap<String, ActionParameterOperation> getActionParameterOperationMap() {
		return actionParameterOperationMap;
	}

	public void setActionParameterOperationMap(HashMap<String, ActionParameterOperation> actionParameterOperationMap) {
		this.actionParameterOperationMap = actionParameterOperationMap;
	}

	public ActionParameterOperation getExpectedValue() {
		return expectedValue;
	}

	public void setExpectedValue(ActionParameterOperation expectedValue) {
		this.expectedValue = expectedValue;
	}

	public ActionParameterOperation getActualValue() {
		return actualValue;
	}

	public void setActualValue(ActionParameterOperation actualValue) {
		this.actualValue = actualValue;
	}

}