package io.metadew.iesi.script.action;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import io.metadew.iesi.datatypes.DataType;
import io.metadew.iesi.datatypes.Dataset;
import io.metadew.iesi.datatypes.Text;
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
import org.apache.logging.log4j.Level;

public class DataCompareDataset {

	private ActionExecution actionExecution;
	private FrameworkExecution frameworkExecution;
	private ExecutionControl executionControl;

	private final Pattern  datasetNamePattern = Pattern.compile("\\s*(?<name>\\w+)\\.(?<table>[\\w\\.]+)\\s*");

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
		this.setActionParameterOperationMap(new HashMap<>());
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
			String leftDatasetName = convertDatasetName(getLeftDatasetName().getValue());
			String rightDatasetName = convertDatasetName(getRightDatasetName().getValue());
			String mappingName = convertMappingName(getMappingName().getValue());
			return compareDataset(leftDatasetName, rightDatasetName, mappingName);
		} catch (Exception e) {
			StringWriter StackTrace = new StringWriter();
			e.printStackTrace(new PrintWriter(StackTrace));

			this.getActionExecution().getActionControl().increaseErrorCount();

			this.getActionExecution().getActionControl().logOutput("exception",e.getMessage());
			this.getActionExecution().getActionControl().logOutput("stacktrace",StackTrace.toString());

			return false;
		}

	}

	private boolean compareDataset(String leftDatasetName, String rightDatasetName, String mappingName) {
		// Run the action
//		Matcher leftDatasetNameMatcher = datasetNamePattern.matcher(leftDatasetName);
//		Matcher rightDatasetNameMatcher = datasetNamePattern.matcher(rightDatasetName);
//		if (!leftDatasetNameMatcher.find()) {
//			throw new RuntimeException(MessageFormat.format("data.comparedataset does not accept {0} as left dataset name", leftDatasetName));
//		}
//		if (!rightDatasetNameMatcher.find()) {
//			throw new RuntimeException(MessageFormat.format("data.comparedataset does not accept {0} as right dataset name", rightDatasetName));
//		}
//		String leftDatasetReferenceName = leftDatasetNameMatcher.group("name");
//		String leftDatasetTable = leftDatasetNameMatcher.group("table");
//		String rightDatasetReferenceName = rightDatasetNameMatcher.group("name");
//		String rightDatasetTable = rightDatasetNameMatcher.group("table");
        Dataset leftDataset = executionControl.getExecutionRuntime().getDataset(leftDatasetName)
                .orElseThrow(() -> new RuntimeException(MessageFormat.format("data.comparedataset could not find dataset {0} as left dataset", leftDatasetName)));
        Dataset rightDataset = executionControl.getExecutionRuntime().getDataset(rightDatasetName)
                .orElseThrow(() -> new RuntimeException(MessageFormat.format("data.comparedataset could not find dataset {0} as right dataset", rightDatasetName)));


		long errorsDetected = 0;
		MappingConfiguration mappingConfiguration = new MappingConfiguration(this.getFrameworkExecution());
		Mapping mapping = mappingConfiguration.getMapping(mappingName);
		for (Transformation transformation : mapping.getTransformations()) {

            Optional<DataType> leftFieldValue = leftDataset.getDataItem(transformation.getLeftField());
            Optional<DataType> rightFieldValue = rightDataset.getDataItem(transformation.getRightField());
			if (!leftFieldValue.isPresent()) {
				this.getActionExecution().getActionControl().logWarning("field.left",
						MessageFormat.format("cannot find value for {0} in dataset {1}.", transformation.getLeftField(), leftDatasetName));
			}
			if (!rightFieldValue.isPresent()) {
				this.getActionExecution().getActionControl().logWarning("field.right",
						MessageFormat.format("cannot find value for {0} in dataset {1}.", transformation.getRightField(), rightDatasetName));
			}
			if (!leftFieldValue.equals(rightFieldValue)) {
				this.getActionExecution().getActionControl().logError("field.mismatch", MessageFormat.format(
						"{0}:{1}<>{2}:{3}", transformation.getLeftField(), leftFieldValue.map(DataType::toString).orElse("null"), transformation.getRightField(), rightFieldValue.map(DataType::toString).orElse("null")));
				this.getActionExecution().getActionControl().increaseErrorCount();
				errorsDetected++;
			} else {
				this.getActionExecution().getActionControl().increaseSuccessCount();
			}
		}
		return errorsDetected <= 0;
	}

	private String convertDatasetName(DataType datasetName) {
		if (datasetName instanceof Text) {
			return datasetName.toString();
		} else {
			frameworkExecution.getFrameworkLog().log(MessageFormat.format("data.compareDataset does not accept {0} as type for dataset name",
					datasetName.getClass()), Level.WARN);
			return datasetName.toString();
		}
	}

	private String convertMappingName(DataType mappingName) {
		if (mappingName instanceof Text) {
			return mappingName.toString();
		} else {
			frameworkExecution.getFrameworkLog().log(MessageFormat.format("data.compareDataset does not accept {0} as type for mapping name",
					mappingName.getClass()), Level.WARN);
			return mappingName.toString();
		}
	}

//	private Dataset convertDataset(DataType dataset) {
//		if (dataset instanceof Text) {
//			// TODO: clean up --> dataset only by reachable by reference? make ExecutionRuntime hold map with referenceName to dataset?
//			return new Dataset(new Text(executionControl.getExecutionRuntime().getDatasetOperation(dataset.toString()).getDatasetName()),
//					new Text(executionControl.getExecutionRuntime().getDatasetOperation(dataset.toString()).getDatasetLabels()));
//		} else if (dataset instanceof Dataset) {
//			return (Dataset) dataset;
//		} else {
//			frameworkExecution.getFrameworkLog().log(MessageFormat.format("data.compareDataset does not accept {0} as type for dataset",
//					dataset.getClass()), Level.WARN);
//			throw new RuntimeException(MessageFormat.format("data.compareDataset does not accept {0} as type for dataset",
//					dataset.getClass()));
//		}
//	}

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