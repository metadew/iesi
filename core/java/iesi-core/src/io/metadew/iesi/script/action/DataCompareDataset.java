package io.metadew.iesi.script.action;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Optional;

import io.metadew.iesi.framework.execution.FrameworkExecution;
import io.metadew.iesi.metadata.configuration.MappingConfiguration;
import io.metadew.iesi.metadata.definition.ActionParameter;
import io.metadew.iesi.metadata.definition.Mapping;
import io.metadew.iesi.metadata.definition.Transformation;
import io.metadew.iesi.script.execution.ActionExecution;
import io.metadew.iesi.script.execution.ExecutionControl;
import io.metadew.iesi.script.execution.ScriptExecution;
import io.metadew.iesi.script.operation.ActionParameterOperation;
import io.metadew.iesi.script.operation.DatasetOperation;

import javax.swing.text.html.Option;

public class DataCompareDataset {

	private ActionExecution actionExecution;
	private FrameworkExecution frameworkExecution;
	private ExecutionControl executionControl;

	// Parameters
	private ActionParameterOperation leftDatasetName;
	private ActionParameterOperation rightDatasetName;
	private ActionParameterOperation mappingName;
	private HashMap<String, ActionParameterOperation> actionParameterOperationMap;

	// Constructors
	public DataCompareDataset() {

	}

	public DataCompareDataset(FrameworkExecution frameworkExecution, ExecutionControl executionControl,
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
		this.setLeftDatasetName(new ActionParameterOperation(this.getFrameworkExecution(), this.getExecutionControl(),
				this.getActionExecution(), this.getActionExecution().getAction().getType(), "leftDataset"));
		this.setRightDatasetName(new ActionParameterOperation(this.getFrameworkExecution(), this.getExecutionControl(),
				this.getActionExecution(), this.getActionExecution().getAction().getType(), "rightDataset"));
		this.setMappingName(new ActionParameterOperation(this.getFrameworkExecution(), this.getExecutionControl(),
				this.getActionExecution(), this.getActionExecution().getAction().getType(), "mapping"));
		// Get Parameters
		for (ActionParameter actionParameter : this.getActionExecution().getAction().getParameters()) {
			if (actionParameter.getName().equalsIgnoreCase("leftdataset")) {
				this.getLeftDatasetName().setInputValue(actionParameter.getValue());
			} else if (actionParameter.getName().equalsIgnoreCase("rightdataset")) {
				this.getRightDatasetName().setInputValue(actionParameter.getValue());
			} else if (actionParameter.getName().equalsIgnoreCase("mapping")) {
				this.getMappingName().setInputValue(actionParameter.getValue());
			}
		}

		// Create parameter list
		this.getActionParameterOperationMap().put("leftDataset", this.getLeftDatasetName());
		this.getActionParameterOperationMap().put("rightDataset", this.getRightDatasetName());
		this.getActionParameterOperationMap().put("mapping", this.getMappingName());
	}
	
	//
	public boolean execute() {
		try {
			// Run the action
			String[] leftDatasetparts = this.getLeftDatasetName().getValue().split("\\.");
			String[] rightDatasetparts = this.getRightDatasetName().getValue().split("\\.");
			DatasetOperation leftDatasetOperation = this.getExecutionControl().getExecutionRuntime()
					.getDatasetOperation(leftDatasetparts[0]);
			DatasetOperation rightDatasetOperation = this.getExecutionControl().getExecutionRuntime()
					.getDatasetOperation(rightDatasetparts[0]);

			long errorsDetected = 0;
			MappingConfiguration mappingConfiguration = new MappingConfiguration(this.getFrameworkExecution());
			Mapping mapping = mappingConfiguration.getMapping(this.getMappingName().getValue());
			for (Transformation transformation : mapping.getTransformations()) {
				Optional<String> leftFieldValue = leftDatasetOperation.getDataItem(leftDatasetparts[1] + "." + transformation.getLeftField());
				Optional<String> rightFieldValue = rightDatasetOperation.getDataItem(rightDatasetparts[1] + "." + transformation.getRightField());
				if (!leftFieldValue.isPresent()) {
					this.getActionExecution().getActionControl().logWarning("field.left",
							MessageFormat.format("Cannot find value for {0}.",leftDatasetparts[1] + "." + transformation.getLeftField()));
				}
				if (!rightFieldValue.isPresent()) {
					this.getActionExecution().getActionControl().logWarning("field.right",
							MessageFormat.format("Cannot find value for {0}.",leftDatasetparts[1] + "." + transformation.getLeftField()));
				}
				if (!leftFieldValue.equals(rightFieldValue)) {
					this.getActionExecution().getActionControl().logError("field.mismatch", MessageFormat.format(
							"{0}:{1}<>{2}:{3}", transformation.getLeftField(), leftFieldValue, transformation.getRightField(), rightFieldValue));
					this.getActionExecution().getActionControl().increaseErrorCount();
					errorsDetected++;
				} else {
					this.getActionExecution().getActionControl().increaseSuccessCount();
				}
			}
			return errorsDetected <= 0;

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

	public ActionParameterOperation getLeftDatasetName() {
		return leftDatasetName;
	}

	public void setLeftDatasetName(ActionParameterOperation leftDatasetName) {
		this.leftDatasetName = leftDatasetName;
	}

	public ActionParameterOperation getRightDatasetName() {
		return rightDatasetName;
	}

	public void setRightDatasetName(ActionParameterOperation rightDatasetName) {
		this.rightDatasetName = rightDatasetName;
	}

	public ActionParameterOperation getMappingName() {
		return mappingName;
	}

	public void setMappingName(ActionParameterOperation mappingName) {
		this.mappingName = mappingName;
	}

}