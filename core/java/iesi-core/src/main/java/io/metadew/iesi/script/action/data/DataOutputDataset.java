package io.metadew.iesi.script.action.data;

import io.metadew.iesi.datatypes.DataType;
import io.metadew.iesi.datatypes.DataTypeHandler;
import io.metadew.iesi.datatypes.array.Array;
import io.metadew.iesi.datatypes.text.Text;
import io.metadew.iesi.metadata.definition.action.ActionParameter;
import io.metadew.iesi.metadata.definition.dataset.InMemoryDatasetImplementation;
import io.metadew.iesi.metadata.definition.dataset.InMemoryDatasetImplementationService;
import io.metadew.iesi.script.action.ActionTypeExecution;
import io.metadew.iesi.script.execution.ActionExecution;
import io.metadew.iesi.script.execution.ExecutionControl;
import io.metadew.iesi.script.execution.ExecutionRuntime;
import io.metadew.iesi.script.execution.ScriptExecution;
import io.metadew.iesi.script.operation.ActionParameterOperation;
import lombok.extern.log4j.Log4j2;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * This action prints a dataset for logging and debugging purposes
 */
@Log4j2
public class DataOutputDataset extends ActionTypeExecution {

    // Parameters
    private ActionParameterOperation datasetName;
    private ActionParameterOperation datasetLabels;
    private ActionParameterOperation onScreen;

    public DataOutputDataset(ExecutionControl executionControl,
                             ScriptExecution scriptExecution, ActionExecution actionExecution) {
        super(executionControl, scriptExecution, actionExecution);
    }

    public void prepare() {
        // Reset Parameters
        datasetName = new ActionParameterOperation(getExecutionControl(), getActionExecution(), getActionExecution().getAction().getType(), "name");
        datasetLabels = new ActionParameterOperation(getExecutionControl(), getActionExecution(), getActionExecution().getAction().getType(), "labels");
        onScreen = new ActionParameterOperation(getExecutionControl(), getActionExecution(), getActionExecution().getAction().getType(), "onScreen");

        // Get Parameters
        for (ActionParameter actionParameter : getActionExecution().getAction().getParameters()) {
            if (actionParameter.getMetadataKey().getParameterName().equalsIgnoreCase("name")) {
                this.getDatasetName().setInputValue(actionParameter.getValue(), getExecutionControl().getExecutionRuntime());
            } else if (actionParameter.getMetadataKey().getParameterName().equalsIgnoreCase("labels")) {
                this.getDatasetLabels().setInputValue(actionParameter.getValue(), getExecutionControl().getExecutionRuntime());
            } else if (actionParameter.getMetadataKey().getParameterName().equalsIgnoreCase("onScreen")) {
                this.getOnScreen().setInputValue(actionParameter.getValue(), getExecutionControl().getExecutionRuntime());
            }
        }

        // Create parameter list
        getActionParameterOperationMap().put("name", this.getDatasetName());
        getActionParameterOperationMap().put("labels", this.getDatasetLabels());
        getActionParameterOperationMap().put("onScreen", this.getOnScreen());
    }

    protected boolean executeAction() throws InterruptedException, IOException {
        InMemoryDatasetImplementation dataset = InMemoryDatasetImplementationService.getInstance().getDatasetImplementation(convertDatasetName(getDatasetName().getValue()), convertDatasetLabels(getDatasetLabels().getValue(), getExecutionControl().getExecutionRuntime()))
                .orElseThrow(() -> new RuntimeException("Could not find dataset with " + convertDatasetName(getDatasetName().getValue()) + " " + convertDatasetLabels(getDatasetLabels().getValue(), getExecutionControl().getExecutionRuntime())));
        boolean onScreen = convertOnScreen(getOnScreen().getValue());
        InMemoryDatasetImplementationService.getInstance().getDataItems(dataset, getExecutionControl().getExecutionRuntime())
                .forEach((key, value) -> log.info(MessageFormat.format("{0}:{1}", key, value)));

        getActionExecution().getActionControl().increaseSuccessCount();
        return true;
    }


    private List<String> convertDatasetLabels(DataType datasetLabels, ExecutionRuntime executionRuntime) {
        List<String> labels = new ArrayList<>();
        if (datasetLabels instanceof Text) {
            Arrays.stream(datasetLabels.toString().split(","))
                    .forEach(datasetLabel -> labels.add(convertDatasetLabel(DataTypeHandler.getInstance().resolve(datasetLabel.trim(), executionRuntime), executionRuntime)));
            return labels;
        } else if (datasetLabels instanceof Array) {
            ((Array) datasetLabels).getList()
                    .forEach(datasetLabel -> labels.add(convertDatasetLabel(datasetLabel, executionRuntime)));
            return labels;
        } else {
            log.warn(MessageFormat.format("dataset does not accept {0} as type for datasetDatabase labels",
                    datasetLabels.getClass()));
            return labels;
        }
    }


    private String convertDatasetLabel(DataType datasetLabel, ExecutionRuntime executionRuntime) {
        if (datasetLabel instanceof Text) {
            return executionRuntime.resolveVariables(((Text) datasetLabel).getString());
        } else {
            log.warn(MessageFormat.format("dataset does not accept {0} as type for a datasetDatabase label",
                    datasetLabel.getClass()));
            return executionRuntime.resolveVariables(datasetLabel.toString());
        }
    }

    private String convertDatasetName(DataType datasetName) {
        if (datasetName instanceof Text) {
            return ((Text) datasetName).getString();
        } else {
            log.warn(MessageFormat.format(getActionExecution().getAction().getType() + " does not accept {0} as type for datasetName",
                    datasetName.getClass()));
            return datasetName.toString();
        }
    }


    private boolean convertOnScreen(DataType onScreen) {
        if (onScreen == null) {
            return false;
        } else if (onScreen instanceof Text) {
            return onScreen.toString().equalsIgnoreCase("y");
        } else {
            log.warn(MessageFormat.format(getActionExecution().getAction().getType() + " does not accept {0} as type for onScreen",
                    onScreen.getClass()));
            return false;
        }
    }


    public ActionParameterOperation getOnScreen() {
        return onScreen;
    }

    public void setOnScreen(ActionParameterOperation onScreen) {
        this.onScreen = onScreen;
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

}