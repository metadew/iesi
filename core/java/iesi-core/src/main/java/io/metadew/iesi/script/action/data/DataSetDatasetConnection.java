package io.metadew.iesi.script.action.data;

import io.metadew.iesi.datatypes.DataType;
import io.metadew.iesi.datatypes.DataTypeHandler;
import io.metadew.iesi.datatypes.array.Array;
import io.metadew.iesi.datatypes.text.Text;
import io.metadew.iesi.metadata.definition.action.ActionParameter;
import io.metadew.iesi.script.action.ActionTypeExecution;
import io.metadew.iesi.script.execution.ActionExecution;
import io.metadew.iesi.script.execution.ExecutionControl;
import io.metadew.iesi.script.execution.ScriptExecution;
import io.metadew.iesi.script.operation.ActionParameterOperation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;


public class DataSetDatasetConnection extends ActionTypeExecution {

    // Parameters
    private ActionParameterOperation referenceName;
    private ActionParameterOperation datasetType;
    private ActionParameterOperation datasetName;
    private ActionParameterOperation datasetLabels;
    private static final Logger LOGGER = LogManager.getLogger();

    public DataSetDatasetConnection(ExecutionControl executionControl,
                                    ScriptExecution scriptExecution, ActionExecution actionExecution) {
        super(executionControl, scriptExecution, actionExecution);
    }

    public void prepare() {
        // Reset Parameters
        this.setReferenceName(new ActionParameterOperation(this.getExecutionControl(),
                this.getActionExecution(), this.getActionExecution().getAction().getType(), "name"));
        this.setDatasetType(new ActionParameterOperation(this.getExecutionControl(),
                this.getActionExecution(), this.getActionExecution().getAction().getType(), "type"));
        this.setDatasetName(new ActionParameterOperation(this.getExecutionControl(),
                this.getActionExecution(), this.getActionExecution().getAction().getType(), "dataset"));
        this.setDatasetLabels(new ActionParameterOperation(this.getExecutionControl(),
                this.getActionExecution(), this.getActionExecution().getAction().getType(), "labels"));

        // Get Parameters
        for (ActionParameter actionParameter : this.getActionExecution().getAction().getParameters()) {
            if (actionParameter.getMetadataKey().getParameterName().equalsIgnoreCase("name")) {
                this.getReferenceName().setInputValue(actionParameter.getValue(), getExecutionControl().getExecutionRuntime());
            } else if (actionParameter.getMetadataKey().getParameterName().equalsIgnoreCase("type")) {
                this.getDatasetType().setInputValue(actionParameter.getValue(), getExecutionControl().getExecutionRuntime());
            } else if (actionParameter.getMetadataKey().getParameterName().equalsIgnoreCase("dataset")) {
                this.getDatasetName().setInputValue(actionParameter.getValue(), getExecutionControl().getExecutionRuntime());
            } else if (actionParameter.getMetadataKey().getParameterName().equalsIgnoreCase("labels")) {
                this.getDatasetLabels().setInputValue(actionParameter.getValue(), getExecutionControl().getExecutionRuntime());
            }
        }

        // Default values
        if (this.getDatasetLabels().getValue() == null)
            this.getDatasetLabels().setInputValue("", getExecutionControl().getExecutionRuntime());

        // Create parameter list
        this.getActionParameterOperationMap().put("name", this.getReferenceName());
        this.getActionParameterOperationMap().put("type", this.getDatasetType());
        this.getActionParameterOperationMap().put("dataset", this.getDatasetName());
        this.getActionParameterOperationMap().put("labels", this.getDatasetLabels());
    }

    protected boolean executeAction() throws IOException {
        String referenceName = convertDatasetReferenceName(getReferenceName().getValue());
        String datasetName = convertDatasetName(getDatasetName().getValue());
        String datasetType = convertDatasetType(getDatasetType().getValue());
        List<String> labels = convertDatasetLabels(getDatasetLabels().getValue());
        getExecutionControl().getExecutionRuntime().setKeyValueDataset(referenceName, datasetName, labels);
        return true;
    }

    private String convertDatasetReferenceName(DataType referenceName) {
        if (referenceName instanceof Text) {
            return referenceName.toString();
        } else {
            LOGGER.warn(MessageFormat.format(this.getActionExecution().getAction().getType() + " does not accept {0} as type for reference name",
                    referenceName.getClass()));
            return referenceName.toString();
        }
    }


    private List<String> convertDatasetLabels(DataType datasetLabels) {
        List<String> labels = new ArrayList<>();
        if (datasetLabels instanceof Text) {
            Arrays.stream(datasetLabels.toString().split(","))
                    .forEach(datasetLabel -> labels.add(convertDatasetLabel(DataTypeHandler.getInstance().resolve(datasetLabel.trim(), getExecutionControl().getExecutionRuntime()))));
            return labels;
        } else if (datasetLabels instanceof Array) {
            ((Array) datasetLabels).getList()
                    .forEach(datasetLabel -> labels.add(convertDatasetLabel(datasetLabel)));
            return labels;
        } else {
            LOGGER.warn(MessageFormat.format(this.getActionExecution().getAction().getType() + " does not accept {0} as type for dataset labels",
                    datasetLabels.getClass()));
            return labels;
        }
    }

    private String convertDatasetType(DataType datasetType) {
        if (datasetType == null) {
            return "";
        }
        if (datasetType instanceof Text) {
            return datasetType.toString();
        } else {
            LOGGER.warn(MessageFormat.format(this.getActionExecution().getAction().getType() + " does not accept {0} as type for dataset type",
                    datasetType.getClass()));
            return datasetType.toString();
        }
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

    private String convertDatasetLabel(DataType datasetLabel) {
        if (datasetLabel instanceof Text) {
            return datasetLabel.toString();
        } else {
            LOGGER.warn(MessageFormat.format(this.getActionExecution().getAction().getType() + " does not accept {0} as type for a dataset label",
                    datasetLabel.getClass()));
            return datasetLabel.toString();
        }
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

    public ActionParameterOperation getDatasetType() {
        return datasetType;
    }

    public void setDatasetType(ActionParameterOperation datasetType) {
        this.datasetType = datasetType;
    }

}