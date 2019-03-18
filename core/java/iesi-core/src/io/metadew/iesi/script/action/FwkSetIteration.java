package io.metadew.iesi.script.action;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.HashMap;

import io.metadew.iesi.framework.execution.FrameworkExecution;
import io.metadew.iesi.metadata.definition.ActionParameter;
import io.metadew.iesi.metadata.definition.Iteration;
import io.metadew.iesi.script.execution.ActionExecution;
import io.metadew.iesi.script.execution.ExecutionControl;
import io.metadew.iesi.script.execution.ScriptExecution;
import io.metadew.iesi.script.operation.ActionParameterOperation;

public class FwkSetIteration {

	private ActionExecution actionExecution;
	private FrameworkExecution frameworkExecution;
	private ExecutionControl executionControl;

	// Parameters
	private ActionParameterOperation iterationName;
	private ActionParameterOperation iterationType;
	private ActionParameterOperation iterationList;
	private ActionParameterOperation iterationValues;
	private ActionParameterOperation iterationFrom;
	private ActionParameterOperation iterationTo;
	private ActionParameterOperation iterationStep;
	private ActionParameterOperation iterationInterrupt;
	private HashMap<String, ActionParameterOperation> actionParameterOperationMap;

	// Constructors
	public FwkSetIteration() {

	}

	public FwkSetIteration(FrameworkExecution frameworkExecution, ExecutionControl executionControl,
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
		this.setIterationName(new ActionParameterOperation(this.getFrameworkExecution(), this.getExecutionControl(),
				this.getActionExecution(), this.getActionExecution().getAction().getType(), "name"));
		this.setIterationType(new ActionParameterOperation(this.getFrameworkExecution(), this.getExecutionControl(),
				this.getActionExecution(), this.getActionExecution().getAction().getType(), "type"));
		this.setIterationList(new ActionParameterOperation(this.getFrameworkExecution(), this.getExecutionControl(),
				this.getActionExecution(), this.getActionExecution().getAction().getType(), "list"));
		this.setIterationValues(new ActionParameterOperation(this.getFrameworkExecution(), this.getExecutionControl(),
				this.getActionExecution(), this.getActionExecution().getAction().getType(), "values"));
		this.setIterationFrom(new ActionParameterOperation(this.getFrameworkExecution(), this.getExecutionControl(),
				this.getActionExecution(), this.getActionExecution().getAction().getType(), "from"));
		this.setIterationTo(new ActionParameterOperation(this.getFrameworkExecution(), this.getExecutionControl(),
				this.getActionExecution(), this.getActionExecution().getAction().getType(), "to"));
		this.setIterationStep(new ActionParameterOperation(this.getFrameworkExecution(), this.getExecutionControl(),
				this.getActionExecution(), this.getActionExecution().getAction().getType(), "step"));
		this.setIterationInterrupt(new ActionParameterOperation(this.getFrameworkExecution(), this.getExecutionControl(),
				this.getActionExecution(), this.getActionExecution().getAction().getType(), "interrupt"));
		// Get Parameters
		for (ActionParameter actionParameter : this.getActionExecution().getAction().getParameters()) {
			if (actionParameter.getName().equalsIgnoreCase("name")) {
				this.getIterationName().setInputValue(actionParameter.getValue());
			} else if (actionParameter.getName().equalsIgnoreCase("type")) {
				this.getIterationType().setInputValue(actionParameter.getValue());
			} else if (actionParameter.getName().equalsIgnoreCase("list")) {
				this.getIterationList().setInputValue(actionParameter.getValue());
			} else if (actionParameter.getName().equalsIgnoreCase("values")) {
				this.getIterationValues().setInputValue(actionParameter.getValue());
			} else if (actionParameter.getName().equalsIgnoreCase("from")) {
				this.getIterationFrom().setInputValue(actionParameter.getValue());
			} else if (actionParameter.getName().equalsIgnoreCase("to")) {
				this.getIterationTo().setInputValue(actionParameter.getValue());
			} else if (actionParameter.getName().equalsIgnoreCase("step")) {
				this.getIterationStep().setInputValue(actionParameter.getValue());
			} else if (actionParameter.getName().equalsIgnoreCase("interrupt")) {
				this.getIterationInterrupt().setInputValue(actionParameter.getValue());
			}
		}

		// Create parameter list
		this.getActionParameterOperationMap().put("name", this.getIterationName());
		this.getActionParameterOperationMap().put("type", this.getIterationType());
		this.getActionParameterOperationMap().put("list", this.getIterationList());
		this.getActionParameterOperationMap().put("values", this.getIterationValues());
		this.getActionParameterOperationMap().put("from", this.getIterationFrom());
		this.getActionParameterOperationMap().put("to", this.getIterationTo());
		this.getActionParameterOperationMap().put("step", this.getIterationStep());
		this.getActionParameterOperationMap().put("interrupt", this.getIterationInterrupt());
	}

	//
	public boolean execute() {
		try {
			// Run the action
			Iteration iteration = new Iteration();
			iteration.setName(this.getIterationName().getValue());
			iteration.setType(this.getIterationType().getValue());
			iteration.setList(this.getIterationList().getValue());
			iteration.setValues(this.getIterationValues().getValue());
			iteration.setFrom(this.getIterationFrom().getValue());
			iteration.setTo(this.getIterationTo().getValue());
			iteration.setStep(this.getIterationStep().getValue());
			iteration.setInterrupt(this.getIterationInterrupt().getValue());
			this.getExecutionControl().getExecutionRuntime().setIteration(iteration);

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

	public ActionParameterOperation getIterationName() {
		return iterationName;
	}

	public void setIterationName(ActionParameterOperation iterationName) {
		this.iterationName = iterationName;
	}

	public ActionParameterOperation getIterationType() {
		return iterationType;
	}

	public void setIterationType(ActionParameterOperation iterationType) {
		this.iterationType = iterationType;
	}

	public ActionParameterOperation getIterationList() {
		return iterationList;
	}

	public void setIterationList(ActionParameterOperation iterationList) {
		this.iterationList = iterationList;
	}

	public ActionParameterOperation getIterationValues() {
		return iterationValues;
	}

	public void setIterationValues(ActionParameterOperation iterationValues) {
		this.iterationValues = iterationValues;
	}

	public ActionParameterOperation getIterationFrom() {
		return iterationFrom;
	}

	public void setIterationFrom(ActionParameterOperation iterationFrom) {
		this.iterationFrom = iterationFrom;
	}

	public ActionParameterOperation getIterationTo() {
		return iterationTo;
	}

	public void setIterationTo(ActionParameterOperation iterationTo) {
		this.iterationTo = iterationTo;
	}

	public ActionParameterOperation getIterationStep() {
		return iterationStep;
	}

	public void setIterationStep(ActionParameterOperation iterationStep) {
		this.iterationStep = iterationStep;
	}

	public ActionParameterOperation getIterationInterrupt() {
		return iterationInterrupt;
	}

	public void setIterationInterrupt(ActionParameterOperation iterationInterrupt) {
		this.iterationInterrupt = iterationInterrupt;
	}

}