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

public class FwkSetRepository {

	private ActionExecution actionExecution;
	private FrameworkExecution frameworkExecution;
	private ExecutionControl executionControl;

	// Parameters
	private ActionParameterOperation repositoryReferenceName;
	private ActionParameterOperation repositoryName;
	private ActionParameterOperation repositoryInstanceName;
	private ActionParameterOperation repositoryInstanceLabels;
	private HashMap<String, ActionParameterOperation> actionParameterOperationMap;

	// Constructors
	public FwkSetRepository() {

	}

	public FwkSetRepository(FrameworkExecution frameworkExecution, ExecutionControl executionControl,
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
		this.setRepositoryReferenceName(
				new ActionParameterOperation(this.getFrameworkExecution(), this.getExecutionControl(),
						this.getActionExecution(), this.getActionExecution().getAction().getType(), "name"));
		this.setRepositoryName(new ActionParameterOperation(this.getFrameworkExecution(), this.getExecutionControl(),
				this.getActionExecution(), this.getActionExecution().getAction().getType(), "repository"));
		this.setRepositoryInstanceName(
				new ActionParameterOperation(this.getFrameworkExecution(), this.getExecutionControl(),
						this.getActionExecution(), this.getActionExecution().getAction().getType(), "instance"));
		this.setRepositoryInstanceLabels(
				new ActionParameterOperation(this.getFrameworkExecution(), this.getExecutionControl(),
						this.getActionExecution(), this.getActionExecution().getAction().getType(), "labels"));
		// Get Parameters
		for (ActionParameter actionParameter : this.getActionExecution().getAction().getParameters()) {
			if (actionParameter.getName().equalsIgnoreCase("repository")) {
				this.getRepositoryName().setInputValue(actionParameter.getValue());
			} else if (actionParameter.getName().equalsIgnoreCase("name")) {
				this.getRepositoryReferenceName().setInputValue(actionParameter.getValue());
			} else if (actionParameter.getName().equalsIgnoreCase("instance")) {
				this.getRepositoryInstanceName().setInputValue(actionParameter.getValue());
			} else if (actionParameter.getName().equalsIgnoreCase("labels")) {
				this.getRepositoryInstanceLabels().setInputValue(actionParameter.getValue());
			}
		}

		// Create parameter list
		this.getActionParameterOperationMap().put("name", this.getRepositoryReferenceName());
		this.getActionParameterOperationMap().put("repository", this.getRepositoryName());
		this.getActionParameterOperationMap().put("instance", this.getRepositoryInstanceName());
		this.getActionParameterOperationMap().put("labels", this.getRepositoryInstanceLabels());
	}

	//
	public boolean execute() {
		try {
//			// Run the action
//			this.getExecutionControl().getExecutionRuntime().setRepository(this.getExecutionControl(), this.getRepositoryReferenceName().getValue(),
//					this.getRepositoryName().getValue(), this.getRepositoryInstanceName().getValue(),
//					this.getRepositoryInstanceLabels().getValue());
//
//			this.getActionExecution().getActionControl().increaseSuccessCount();
//
			return true;
		} catch (Exception e) {
			StringWriter StackTrace = new StringWriter();
			e.printStackTrace(new PrintWriter(StackTrace));

			this.getActionExecution().getActionControl().increaseErrorCount();

			this.getActionExecution().getActionControl().logOutput("exception", e.getMessage());
			this.getActionExecution().getActionControl().logOutput("stacktrace", StackTrace.toString());

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

	public ActionParameterOperation getRepositoryName() {
		return repositoryName;
	}

	public void setRepositoryName(ActionParameterOperation repositoryName) {
		this.repositoryName = repositoryName;
	}

	public ActionParameterOperation getRepositoryInstanceName() {
		return repositoryInstanceName;
	}

	public void setRepositoryInstanceName(ActionParameterOperation repositoryInstanceName) {
		this.repositoryInstanceName = repositoryInstanceName;
	}

	public ActionParameterOperation getRepositoryInstanceLabels() {
		return repositoryInstanceLabels;
	}

	public void setRepositoryInstanceLabels(ActionParameterOperation repositoryInstanceLabels) {
		this.repositoryInstanceLabels = repositoryInstanceLabels;
	}

	public ActionParameterOperation getRepositoryReferenceName() {
		return repositoryReferenceName;
	}

	public void setRepositoryReferenceName(ActionParameterOperation repositoryReferenceName) {
		this.repositoryReferenceName = repositoryReferenceName;
	}

}