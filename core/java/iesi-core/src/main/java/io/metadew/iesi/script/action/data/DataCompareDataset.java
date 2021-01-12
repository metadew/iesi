package io.metadew.iesi.script.action.data;

import io.metadew.iesi.datatypes.DataType;
import io.metadew.iesi.datatypes.dataset.implementation.inmemory.InMemoryDatasetImplementation;
import io.metadew.iesi.datatypes.dataset.implementation.inmemory.InMemoryDatasetImplementationService;
import io.metadew.iesi.datatypes.text.Text;
import io.metadew.iesi.metadata.configuration.mapping.MappingConfiguration;
import io.metadew.iesi.metadata.definition.Transformation;
import io.metadew.iesi.metadata.definition.action.ActionParameter;
import io.metadew.iesi.metadata.definition.mapping.Mapping;
import io.metadew.iesi.script.action.ActionTypeExecution;
import io.metadew.iesi.script.execution.ActionExecution;
import io.metadew.iesi.script.execution.ExecutionControl;
import io.metadew.iesi.script.execution.ScriptExecution;
import io.metadew.iesi.script.operation.ActionParameterOperation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.text.MessageFormat;
import java.util.Optional;
import java.util.regex.Pattern;
@Deprecated
public class DataCompareDataset extends ActionTypeExecution {

    @SuppressWarnings("unused")
    private final Pattern datasetNamePattern = Pattern.compile("\\s*(?<name>\\w+)\\.(?<table>[\\w\\.]+)\\s*");

    // Parameters
    private ActionParameterOperation leftDatasetName;
    private ActionParameterOperation rightDatasetName;
    private ActionParameterOperation mappingName;
    private static final Logger LOGGER = LogManager.getLogger();

    public DataCompareDataset(ExecutionControl executionControl,
                              ScriptExecution scriptExecution, ActionExecution actionExecution) {
        super(executionControl, scriptExecution, actionExecution);
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
            if (actionParameter.getMetadataKey().getParameterName().equalsIgnoreCase("leftdataset")) {
                this.getLeftDatasetName().setInputValue(actionParameter.getValue(), getExecutionControl().getExecutionRuntime());
            } else if (actionParameter.getMetadataKey().getParameterName().equalsIgnoreCase("rightdataset")) {
                this.getRightDatasetName().setInputValue(actionParameter.getValue(), getExecutionControl().getExecutionRuntime());
            } else if (actionParameter.getMetadataKey().getParameterName().equalsIgnoreCase("mapping")) {
                this.getMappingName().setInputValue(actionParameter.getValue(), getExecutionControl().getExecutionRuntime());
            }
        }

        // Create parameter list
        this.getActionParameterOperationMap().put("leftDataset", this.getLeftDatasetName());
        this.getActionParameterOperationMap().put("rightDataset", this.getRightDatasetName());
        this.getActionParameterOperationMap().put("mapping", this.getMappingName());
    }

    protected boolean executeAction() throws InterruptedException {
        String leftDatasetName = convertDatasetName(getLeftDatasetName().getValue());
        String rightDatasetName = convertDatasetName(getRightDatasetName().getValue());
        String mappingName = convertMappingName(getMappingName().getValue());
        InMemoryDatasetImplementation leftDataset = getExecutionControl().getExecutionRuntime().getDataset(leftDatasetName)
                .orElseThrow(() -> new RuntimeException(MessageFormat.format("data.comparedataset could not find dataset {0} as left dataset", leftDatasetName)));
        InMemoryDatasetImplementation rightDataset = getExecutionControl().getExecutionRuntime().getDataset(rightDatasetName)
                .orElseThrow(() -> new RuntimeException(MessageFormat.format("data.comparedataset could not find dataset {0} as right dataset", rightDatasetName)));


        long errorsDetected = 0;
        Mapping mapping = MappingConfiguration.getInstance().getMapping(mappingName);
        for (Transformation transformation : mapping.getTransformations()) {

            Optional<DataType> leftFieldValue = InMemoryDatasetImplementationService.getInstance().getDataItem(leftDataset, transformation.getLeftField(), getExecutionControl().getExecutionRuntime());
            Optional<DataType> rightFieldValue = InMemoryDatasetImplementationService.getInstance().getDataItem(rightDataset, transformation.getRightField(), getExecutionControl().getExecutionRuntime());
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
                this.getActionExecution().getActionControl().logOutput("field.mismatch", MessageFormat.format(
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
            LOGGER.warn(MessageFormat.format(this.getActionExecution().getAction().getType() + " does not accept {0} as type for dataset name",
                    datasetName.getClass()));
            return datasetName.toString();
        }
    }

    private String convertMappingName(DataType mappingName) {
        if (mappingName instanceof Text) {
            return mappingName.toString();
        } else {
            LOGGER.warn(MessageFormat.format(this.getActionExecution().getAction().getType() + " does not accept {0} as type for mapping name",
                    mappingName.getClass()));
            return mappingName.toString();
        }
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