package io.metadew.iesi.script.action.data;

import io.metadew.iesi.SpringContext;
import io.metadew.iesi.datatypes.DataType;
import io.metadew.iesi.datatypes.DataTypeHandler;
import io.metadew.iesi.datatypes._null.Null;
import io.metadew.iesi.datatypes.array.Array;
import io.metadew.iesi.datatypes.dataset.implementation.database.DatabaseDatasetImplementation;
import io.metadew.iesi.datatypes.dataset.implementation.database.DatabaseDatasetImplementationService;
import io.metadew.iesi.datatypes.dataset.implementation.in.memory.InMemoryDatasetImplementation;
import io.metadew.iesi.datatypes.dataset.implementation.in.memory.InMemoryDatasetImplementationService;
import io.metadew.iesi.datatypes.text.Text;
import io.metadew.iesi.script.action.ActionTypeExecution;
import io.metadew.iesi.script.execution.ActionExecution;
import io.metadew.iesi.script.execution.ExecutionControl;
import io.metadew.iesi.script.execution.ExecutionRuntime;
import io.metadew.iesi.script.execution.ScriptExecution;
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

    private static final String DATASET_NAME_KEY = "name";
    private static final String DATASET_LABELS_KEY = "labels";
    private static final String DATASET_ON_SCREEN_KEY = "onScreen";

    // Parameters

    public DataOutputDataset(ExecutionControl executionControl,
                             ScriptExecution scriptExecution, ActionExecution actionExecution) {
        super(executionControl, scriptExecution, actionExecution);
    }

    public void prepareAction() {
    }

    protected boolean executeAction() throws InterruptedException, IOException {
        DatabaseDatasetImplementation dataset = DatabaseDatasetImplementationService.getInstance()
                .getDatasetImplementation(
                        convertDatasetName(getParameterResolvedValue(DATASET_NAME_KEY)),
                        convertDatasetLabels(getParameterResolvedValue(DATASET_LABELS_KEY),
                                getExecutionControl().getExecutionRuntime()))
                .orElseThrow(() -> new RuntimeException("Could not find dataset with " + convertDatasetName(getParameterResolvedValue(DATASET_NAME_KEY)) + " " + convertDatasetLabels(getParameterResolvedValue(DATASET_LABELS_KEY), getExecutionControl().getExecutionRuntime())));
        boolean onScreen = convertOnScreen(getParameterResolvedValue(DATASET_ON_SCREEN_KEY));
        DatabaseDatasetImplementationService.getInstance().getDataItems(dataset, getExecutionControl().getExecutionRuntime())
                .forEach((key, value) -> log.info(MessageFormat.format("{0}:{1}", key, value)));

        getActionExecution().getActionControl().increaseSuccessCount();
        return true;
    }

    @Override
    protected String getKeyword() {
        return "data.outputDataset";
    }


    private List<String> convertDatasetLabels(DataType datasetLabels, ExecutionRuntime executionRuntime) {
        List<String> labels = new ArrayList<>();
        if (datasetLabels instanceof Text) {
            Arrays.stream(datasetLabels.toString().split(","))
                    .forEach(datasetLabel -> labels.add(convertDatasetLabel(SpringContext.getBean(DataTypeHandler.class).resolve(datasetLabel.trim(), executionRuntime), executionRuntime)));
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
        if (onScreen == null || onScreen instanceof Null) {
            return false;
        } else if (onScreen instanceof Text) {
            return onScreen.toString().equalsIgnoreCase("y");
        } else {
            log.warn(MessageFormat.format(getActionExecution().getAction().getType() + " does not accept {0} as type for onScreen",
                    onScreen.getClass()));
            return false;
        }
    }

}