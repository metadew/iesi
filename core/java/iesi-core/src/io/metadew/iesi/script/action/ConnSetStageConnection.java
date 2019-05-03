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

public class ConnSetStageConnection {

	private ActionExecution actionExecution;
	private FrameworkExecution frameworkExecution;
	private ExecutionControl executionControl;

	// Parameters
	private ActionParameterOperation stageName;
	private HashMap<String, ActionParameterOperation> actionParameterOperationMap;

	// Constructors
	public ConnSetStageConnection() {
		
	}
	
	public ConnSetStageConnection(FrameworkExecution frameworkExecution, ExecutionControl executionControl, ScriptExecution scriptExecution, ActionExecution actionExecution) {
		this.init(frameworkExecution, executionControl, scriptExecution, actionExecution);
	}
	
	public void init(FrameworkExecution frameworkExecution, ExecutionControl executionControl, ScriptExecution scriptExecution, ActionExecution actionExecution) {
		this.setFrameworkExecution(frameworkExecution);
		this.setExecutionControl(executionControl);
		this.setActionExecution(actionExecution);
		this.setActionParameterOperationMap(new HashMap<String, ActionParameterOperation>());
	}
	
	public void prepare() {
		// Reset Parameters
		this.setStageName(new ActionParameterOperation(this.getFrameworkExecution(), this.getExecutionControl(), this.getActionExecution(),
				this.getActionExecution().getAction().getType(), "stage"));

		// Get Parameters
		for (ActionParameter actionParameter : this.getActionExecution().getAction().getParameters()) {
			if (actionParameter.getName().equalsIgnoreCase("stage")) {
				this.getStageName().setInputValue(actionParameter.getValue());
			}
		}

		//Create parameter list
		this.getActionParameterOperationMap().put("stage", this.getStageName());
	}

	//
	public boolean execute() {
		try {
			this.getExecutionControl().getExecutionRuntime().setStage(this.getStageName().getValue());
			this.getActionExecution().getActionControl().increaseSuccessCount();
			
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

	public ActionParameterOperation getStageName() {
		return stageName;
	}

	public void setStageName(ActionParameterOperation stageName) {
		this.stageName = stageName;
	}

	public HashMap<String, ActionParameterOperation> getActionParameterOperationMap() {
		return actionParameterOperationMap;
	}

	public void setActionParameterOperationMap(HashMap<String, ActionParameterOperation> actionParameterOperationMap) {
		this.actionParameterOperationMap = actionParameterOperationMap;
	}

}