package io.metadew.iesi.script.action.data;

import io.metadew.iesi.datatypes.DataType;
import io.metadew.iesi.datatypes.dataset.Dataset;
import io.metadew.iesi.datatypes.dataset.DatasetHandler;
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
import java.util.HashMap;

/**
 * This action prints a dataset for logging and debugging purposes
 */
public class DataOutputDataset extends ActionTypeExecution {

    // Parameters
    private ActionParameterOperation datasetName;
    private ActionParameterOperation datasetLabels;
    private ActionParameterOperation onScreen;
    private static final Logger LOGGER = LogManager.getLogger();

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
        Dataset dataset = DatasetHandler.getInstance().getByNameAndLabels(getDatasetName().getValue(), getDatasetLabels().getValue(), getExecutionControl().getExecutionRuntime());
        boolean onScreen = convertOnScreen(getOnScreen().getValue());
        // TODO: loop over all dataset item and print them
        DatasetHandler.getInstance().getDataItems(dataset, getExecutionControl().getExecutionRuntime())
                .forEach((key, value) -> LOGGER.info(MessageFormat.format("{0}:{1}", key, value)));

        getActionExecution().getActionControl().increaseSuccessCount();
        return true;
    }


    private boolean convertOnScreen(DataType onScreen) {
        if (onScreen == null) {
            return false;
        } else if (onScreen instanceof Text) {
            return onScreen.toString().equalsIgnoreCase("y");
        } else {
            LOGGER.warn(MessageFormat.format(getActionExecution().getAction().getType() + " does not accept {0} as type for onScreen",
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