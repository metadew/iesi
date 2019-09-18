package io.metadew.iesi.script.action.data;

import io.metadew.iesi.datatypes.DataType;
import io.metadew.iesi.datatypes.dataset.Dataset;
import io.metadew.iesi.datatypes.text.Text;
import io.metadew.iesi.framework.instance.FrameworkInstance;
import io.metadew.iesi.metadata.configuration.mapping.MappingConfiguration;
import io.metadew.iesi.metadata.definition.mapping.Mapping;
import io.metadew.iesi.metadata.definition.Transformation;
import io.metadew.iesi.metadata.definition.action.ActionParameter;
import io.metadew.iesi.script.execution.ActionExecution;
import io.metadew.iesi.script.execution.ExecutionControl;
import io.metadew.iesi.script.execution.ScriptExecution;
import io.metadew.iesi.script.operation.ActionParameterOperation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Optional;
import java.util.regex.Pattern;

public class DataCompareDataset {

    private ActionExecution actionExecution;
    private ExecutionControl executionControl;

    @SuppressWarnings("unused")
	private final Pattern datasetNamePattern = Pattern.compile("\\s*(?<name>\\w+)\\.(?<table>[\\w\\.]+)\\s*");

    // Parameters
    private ActionParameterOperation leftDatasetName;
    private ActionParameterOperation rightDatasetName;
    private ActionParameterOperation mappingName;
    private HashMap<String, ActionParameterOperation> actionParameterOperationMap;
    private static final Logger LOGGER = LogManager.getLogger();

    // Constructors
    public DataCompareDataset() {

    }

    public DataCompareDataset(ExecutionControl executionControl,
                              ScriptExecution scriptExecution, ActionExecution actionExecution) {
        this.init(executionControl, scriptExecution, actionExecution);
    }

    public void init(ExecutionControl executionControl, ScriptExecution scriptExecution,
                     ActionExecution actionExecution) {
        this.setExecutionControl(executionControl);
        this.setActionExecution(actionExecution);
        this.setActionParameterOperationMap(new HashMap<>());
    }

    public void prepare() {
        // Reset Parameters
        this.setLeftDatasetName(new ActionParameterOperation(this.getExecutionControl(),
                this.getActionExecution(), this.getActionExecution().getAction().getType(), "leftDataset"));
        this.setRightDatasetName(new ActionParameterOperation(this.getExecutionControl(),
                this.getActionExecution(), this.getActionExecution().getAction().getType(), "rightDataset"));
        this.setMappingName(new ActionParameterOperation(this.getExecutionControl(),
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

            this.getActionExecution().getActionControl().logOutput("exception", e.getMessage());
            this.getActionExecution().getActionControl().logOutput("stacktrace", StackTrace.toString());

            return false;
        }

    }

    private boolean compareDataset(String leftDatasetName, String rightDatasetName, String mappingName) {
        Dataset leftDataset = executionControl.getExecutionRuntime().getDataset(leftDatasetName)
                .orElseThrow(() -> new RuntimeException(MessageFormat.format("data.comparedataset could not find dataset {0} as left dataset", leftDatasetName)));
        Dataset rightDataset = executionControl.getExecutionRuntime().getDataset(rightDatasetName)
                .orElseThrow(() -> new RuntimeException(MessageFormat.format("data.comparedataset could not find dataset {0} as right dataset", rightDatasetName)));


        long errorsDetected = 0;
        MappingConfiguration mappingConfiguration = new MappingConfiguration(FrameworkInstance.getInstance());
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
            LOGGER.warn(MessageFormat.format(this.getActionExecution().getAction().getType() +  " does not accept {0} as type for dataset name",
                    datasetName.getClass()));
            return datasetName.toString();
        }
    }

    private String convertMappingName(DataType mappingName) {
        if (mappingName instanceof Text) {
            return mappingName.toString();
        } else {
            LOGGER.warn(MessageFormat.format(this.getActionExecution().getAction().getType() +  " does not accept {0} as type for mapping name",
                    mappingName.getClass()));
            return mappingName.toString();
        }
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