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

public class DataSetDatasetConnection {

	private ActionExecution actionExecution;
	private FrameworkExecution frameworkExecution;
	private ExecutionControl executionControl;

	// Parameters
	private ActionParameterOperation referenceName;
	private ActionParameterOperation datasetName;
	private ActionParameterOperation datasetLabels;
	private HashMap<String, ActionParameterOperation> actionParameterOperationMap;

	// Constructors
	public DataSetDatasetConnection() {

	}

	public DataSetDatasetConnection(FrameworkExecution frameworkExecution, ExecutionControl executionControl,
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
		this.setReferenceName(new ActionParameterOperation(this.getFrameworkExecution(), this.getExecutionControl(),
				this.getActionExecution(), this.getActionExecution().getAction().getType(), "name"));
		this.setDatasetName(new ActionParameterOperation(this.getFrameworkExecution(), this.getExecutionControl(),
				this.getActionExecution(), this.getActionExecution().getAction().getType(), "dataset"));
		this.setDatasetLabels(new ActionParameterOperation(this.getFrameworkExecution(), this.getExecutionControl(),
				this.getActionExecution(), this.getActionExecution().getAction().getType(), "labels"));
		// Get Parameters
		for (ActionParameter actionParameter : this.getActionExecution().getAction().getParameters()) {
			if (actionParameter.getName().equalsIgnoreCase("name")) {
				this.getReferenceName().setInputValue(actionParameter.getValue());
			} else if (actionParameter.getName().equalsIgnoreCase("dataset")) {
				this.getDatasetName().setInputValue(actionParameter.getValue());
			} else if (actionParameter.getName().equalsIgnoreCase("labels")) {
				this.getDatasetLabels().setInputValue(actionParameter.getValue());
			}
		}

		// Create parameter list
		this.getActionParameterOperationMap().put("name", this.getReferenceName());
		this.getActionParameterOperationMap().put("dataset", this.getDatasetName());
		this.getActionParameterOperationMap().put("labels", this.getDatasetLabels());
	}
	
	//
	public boolean execute() {
		try {
			// Run the action
			this.getExecutionControl().getExecutionRuntime().setDataset(this.getDatasetName().getValue(),
					this.getDatasetLabels().getValue());

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

	public ActionParameterOperation getDatasetName() {
		return datasetName;
	}

	public void setDatasetName(ActionParameterOperation datasetName) {
		this.datasetName = datasetName;
	}

	public ActionParameterOperation getDatasetLabels() {
		return datasetLabels;
	}

	public void setDatasetLabels(ActionParameterOperation datasetLabels) {
		this.datasetLabels = datasetLabels;
	}

	public ActionParameterOperation getReferenceName() {
		return referenceName;
	}

	public void setReferenceName(ActionParameterOperation referenceName) {
		this.referenceName = referenceName;
	}

}