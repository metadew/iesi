package io.metadew.iesi.script.action.data;

import io.metadew.iesi.datatypes.DataType;
import io.metadew.iesi.datatypes.dataset.Dataset;
import io.metadew.iesi.datatypes.dataset.DatasetHandler;
import io.metadew.iesi.datatypes.text.Text;
import io.metadew.iesi.metadata.definition.action.ActionParameter;
import io.metadew.iesi.script.execution.ActionExecution;
import io.metadew.iesi.script.execution.ExecutionControl;
import io.metadew.iesi.script.execution.ScriptExecution;
import io.metadew.iesi.script.operation.ActionParameterOperation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.MessageFormat;
import java.util.HashMap;

/**
 * This action prints a dataset for logging and debugging purposes
 *
 */
public class DataOutputDataset {

    private ActionExecution actionExecution;
    private ExecutionControl executionControl;

    // Parameters
    private ActionParameterOperation datasetName;
    private ActionParameterOperation datasetLabels;
    private ActionParameterOperation onScreen;
    private HashMap<String, ActionParameterOperation> actionParameterOperationMap;
    private static final Logger LOGGER = LogManager.getLogger();

    public DataOutputDataset(ExecutionControl executionControl,
                             ScriptExecution scriptExecution, ActionExecution actionExecution) {
        this.init(executionControl, scriptExecution, actionExecution);
    }

    public void init(ExecutionControl executionControl,
                     ScriptExecution scriptExecution, ActionExecution actionExecution) {
        this.executionControl = executionControl;
        this.actionExecution = actionExecution;
        this.actionParameterOperationMap =  new HashMap<>();
    }

    public void prepare() {
        // Reset Parameters
        datasetName = new ActionParameterOperation(executionControl, actionExecution, actionExecution.getAction().getType(), "name");
        datasetLabels = new ActionParameterOperation(executionControl, actionExecution, actionExecution.getAction().getType(), "labels");
        onScreen = new ActionParameterOperation(executionControl, actionExecution, actionExecution.getAction().getType(), "onScreen");

        // Get Parameters
        for (ActionParameter actionParameter : actionExecution.getAction().getParameters()) {
            if (actionParameter.getMetadataKey().getParameterName().equalsIgnoreCase("name")) {
                this.getDatasetName().setInputValue(actionParameter.getValue(), executionControl.getExecutionRuntime());
            } else if (actionParameter.getMetadataKey().getParameterName().equalsIgnoreCase("labels")) {
                this.getDatasetLabels().setInputValue(actionParameter.getValue(), executionControl.getExecutionRuntime());
            } else if (actionParameter.getMetadataKey().getParameterName().equalsIgnoreCase("onScreen")) {
                this.getOnScreen().setInputValue(actionParameter.getValue(), executionControl.getExecutionRuntime());
            }
        }

        // Create parameter list
        actionParameterOperationMap.put("name", this.getDatasetName());
        actionParameterOperationMap.put("labels", this.getDatasetLabels());
        actionParameterOperationMap.put("onScreen", this.getOnScreen());
    }

    public boolean execute() throws InterruptedException {
        try {
            Dataset dataset =  DatasetHandler.getInstance().getByNameAndLabels(getDatasetName().getValue(), getDatasetLabels().getValue(), executionControl.getExecutionRuntime());
            boolean onScreen = convertOnScreen(getOnScreen().getValue());
            return outputDataset(dataset, onScreen);
        } catch (InterruptedException e) {
            throw (e);
        } catch (Exception e) {
            StringWriter StackTrace = new StringWriter();
            e.printStackTrace(new PrintWriter(StackTrace));

            actionExecution.getActionControl().increaseErrorCount();

            actionExecution.getActionControl().logOutput("exception", e.getMessage());
            actionExecution.getActionControl().logOutput("stacktrace", StackTrace.toString());

            return false;
        }

    }

    private boolean outputDataset(Dataset dataset, boolean onScreen) throws InterruptedException {
        // TODO: loop over all dataset item and print them
        DatasetHandler.getInstance().getDataItems(dataset, executionControl.getExecutionRuntime())
                .forEach((key, value) -> LOGGER.info(MessageFormat.format("{0}:{1}", key, value)));

        actionExecution.getActionControl().increaseSuccessCount();
        return true;
    }


    private boolean convertOnScreen(DataType onScreen) {
        if (onScreen == null) {
            return false;
        } else if (onScreen instanceof Text) {
            return onScreen.toString().equalsIgnoreCase("y");
        } else {
            LOGGER.warn(MessageFormat.format(actionExecution.getAction().getType() +  " does not accept {0} as type for onScreen",
                    onScreen.getClass()));
            return false;
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