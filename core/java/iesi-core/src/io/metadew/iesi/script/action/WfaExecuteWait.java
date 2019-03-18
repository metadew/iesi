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

public class WfaExecuteWait {

	private ActionExecution actionExecution;
	private FrameworkExecution frameworkExecution;
	private ExecutionControl executionControl;

	// Parameters
	private int waitInterval;
	private long startTime;
	private HashMap<String, ActionParameterOperation> actionParameterOperationMap;

	// Constructors
	public WfaExecuteWait() {
		
	}
	
	public WfaExecuteWait(FrameworkExecution frameworkExecution, ExecutionControl executionControl, ScriptExecution scriptExecution, ActionExecution actionExecution) {
		this.init(frameworkExecution, executionControl, scriptExecution, actionExecution);
	}
	
	public void init(FrameworkExecution frameworkExecution, ExecutionControl executionControl, ScriptExecution scriptExecution, ActionExecution actionExecution) {
		this.setFrameworkExecution(frameworkExecution);
		this.setExecutionControl(executionControl);
		this.setActionExecution(actionExecution);
		this.setActionParameterOperationMap(new HashMap<String, ActionParameterOperation>());
	}

	public void prepare() {
		// Set Parameters
		this.setWaitInterval(1000);

		// Get Parameters
		for (ActionParameter actionParameter : this.getActionExecution().getAction().getParameters()) {
			if (actionParameter.getName().equalsIgnoreCase("wait")) {
				this.setWaitInterval(Integer.parseInt(actionParameter.getValue()));
			}
		}
	
		//Create parameter list
	}
	
	public void execute() {
		try {
			// Run the action
			long wait = this.getWaitInterval() * 1000;
			if (wait <= 0)
				wait = 1000;
			boolean done = false;
			
			this.setStartTime(System.currentTimeMillis());
			try {
				Thread.sleep(wait);
				done = true;
			} catch (InterruptedException ex) {
				Thread.currentThread().interrupt();
			}

			long elapsedTime = System.currentTimeMillis() - this.getStartTime();
			if (done) {
				this.getActionExecution().getActionControl().increaseSuccessCount();

				this.getActionExecution().getActionControl().logOutput("out","result found");
				this.getActionExecution().getActionControl().logOutput("time",Long.toString(elapsedTime));
			} else {
				this.getActionExecution().getActionControl().increaseErrorCount();

				this.getActionExecution().getActionControl().logOutput("out","time-out");
				this.getActionExecution().getActionControl().logOutput("time",Long.toString(elapsedTime));
			}
		} catch (Exception e) {
			StringWriter StackTrace = new StringWriter();
			e.printStackTrace(new PrintWriter(StackTrace));

			this.getActionExecution().getActionControl().increaseErrorCount();

			this.getActionExecution().getActionControl().logOutput("exception",e.getMessage());
			this.getActionExecution().getActionControl().logOutput("stacktrace",StackTrace.toString());

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

	public int getWaitInterval() {
		return waitInterval;
	}

	public void setWaitInterval(int waitInterval) {
		this.waitInterval = waitInterval;
	}

	public long getStartTime() {
		return startTime;
	}

	public void setStartTime(long startTime) {
		this.startTime = startTime;
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

}