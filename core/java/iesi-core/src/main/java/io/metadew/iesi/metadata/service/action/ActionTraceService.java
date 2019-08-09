package io.metadew.iesi.metadata.service.action;

import io.metadew.iesi.datatypes.DataType;
import io.metadew.iesi.datatypes.array.Array;
import io.metadew.iesi.datatypes.dataset.Dataset;
import io.metadew.iesi.datatypes.text.Text;
import io.metadew.iesi.metadata.configuration.action.ActionParameterTraceConfiguration;
import io.metadew.iesi.metadata.configuration.action.ActionTraceConfiguration;
import io.metadew.iesi.metadata.configuration.exception.MetadataAlreadyExistsException;
import io.metadew.iesi.metadata.definition.action.ActionParameterTrace;
import io.metadew.iesi.metadata.definition.action.ActionTrace;
import io.metadew.iesi.metadata.definition.action.key.ActionParameterTraceKey;
import io.metadew.iesi.metadata.execution.MetadataControl;
import io.metadew.iesi.script.execution.ActionExecution;
import io.metadew.iesi.script.operation.ActionParameterOperation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.Map;

public class ActionTraceService {

    private ActionTraceConfiguration actionTraceConfiguration;
    private ActionParameterTraceConfiguration actionParameterTraceConfiguration;
    private static final Logger logger = LogManager.getLogger();

    public ActionTraceService() {
        this.actionTraceConfiguration = new ActionTraceConfiguration();
        this.actionParameterTraceConfiguration = new ActionParameterTraceConfiguration();
    }

    public void trace(ActionExecution actionExecution, Map<String, ActionParameterOperation> actionParameterOperationMap) {
        try {
            actionTraceConfiguration.insert(new ActionTrace(actionExecution.getExecutionControl().getRunId(), actionExecution.getProcessId(), actionExecution.getAction()));
            for (Map.Entry<String, ActionParameterOperation> actionParameterOperationEntry : actionParameterOperationMap.entrySet()) {
                if (actionParameterOperationEntry.getValue() == null) continue;
                trace(actionExecution, actionParameterOperationEntry.getKey(), actionParameterOperationEntry.getValue().getValue());
            }

        } catch (MetadataAlreadyExistsException | SQLException e) {
            e.printStackTrace();
        }
    }

    public void trace(ActionExecution actionExecution, String key, DataType value) {
        try {
            if (value == null) {
                actionParameterTraceConfiguration.insert(new ActionParameterTrace(new ActionParameterTraceKey(actionExecution.getExecutionControl().getRunId(), actionExecution.getProcessId(), actionExecution.getAction().getId(), key), "null"));

            } else if (value instanceof Text) {
                actionParameterTraceConfiguration.insert(new ActionParameterTrace(new ActionParameterTraceKey(actionExecution.getExecutionControl().getRunId(), actionExecution.getProcessId(), actionExecution.getAction().getId(), key), ((Text) value).getString()));
            } else if (value instanceof Array) {
                int counter = 0;
                for (DataType element : ((Array) value).getList()) {
                    trace(actionExecution, key + counter, element);
                    counter++;
                }
            } else if (value instanceof Dataset) {
                for (Map.Entry<String, DataType> datasetItem : ((Dataset) value).getDataItems().entrySet()) {
                    trace(actionExecution, key + datasetItem.getKey(), datasetItem.getValue());
                }
            } else {
                logger.warn(MessageFormat.format("DataType ''{0}'' is unknown to trace", value.getClass()));
            }

        } catch (MetadataAlreadyExistsException | SQLException e) {
            e.printStackTrace();
        }
    }
}
