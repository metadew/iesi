package io.metadew.iesi.metadata.service.action;

import io.metadew.iesi.datatypes.DataType;
import io.metadew.iesi.datatypes.array.Array;
import io.metadew.iesi.datatypes.dataset.Dataset;
import io.metadew.iesi.datatypes.text.Text;
import io.metadew.iesi.metadata.configuration.action.trace.ActionParameterTraceConfiguration;
import io.metadew.iesi.metadata.definition.action.trace.ActionParameterTrace;
import io.metadew.iesi.metadata.definition.action.trace.key.ActionParameterTraceKey;
import io.metadew.iesi.script.execution.ActionExecution;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ActionParameterTraceService {
    private static final Logger LOGGER = LogManager.getLogger();

    public ActionParameterTraceService() {
    }

    public void trace(ActionExecution actionExecution, Map<String, DataType> actionParameterMap) {
        trace(actionExecution, "", actionParameterMap);
    }

    public void trace(ActionExecution actionExecution, String prefix, Map<String, DataType> actionParameterMap) {
        List<ActionParameterTrace> actionParameterTraces = new ArrayList<>();
        for (Map.Entry<String, DataType> actionParameterEntry : actionParameterMap.entrySet()) {
            actionParameterTraces.addAll(getActionParameterTraces(actionExecution, prefix + actionParameterEntry.getKey(), actionParameterEntry.getValue()));
            // trace(actionExecution, prefix + actionParameterEntry.getKey(), actionParameterEntry.getValue());
        }
        ActionParameterTraceConfiguration.getInstance().insert(actionParameterTraces);
    }

    public void trace(ActionExecution actionExecution, String key, DataType value) {
        List<ActionParameterTrace> actionParameterTraces = getActionParameterTraces(actionExecution, key, value);
        ActionParameterTraceConfiguration.getInstance().insert(actionParameterTraces);
    }

    private List<ActionParameterTrace> getActionParameterTraces(ActionExecution actionExecution, String key, DataType value) {
        List<ActionParameterTrace> actionParameterTraces = new ArrayList<>();
        if (value == null) {
            actionParameterTraces.add(new ActionParameterTrace(new ActionParameterTraceKey(actionExecution.getExecutionControl().getRunId(), actionExecution.getProcessId(), actionExecution.getAction().getId(), key), "null"));
        } else if (value instanceof Text) {
            actionParameterTraces.add(new ActionParameterTrace(new ActionParameterTraceKey(actionExecution.getExecutionControl().getRunId(), actionExecution.getProcessId(), actionExecution.getAction().getId(), key), ((Text) value).getString()));
        } else if (value instanceof Array) {
            int counter = 0;
            for (DataType element : ((Array) value).getList()) {
                actionParameterTraces.addAll(getActionParameterTraces(actionExecution, key + counter, element));
                counter++;
            }
        } else if (value instanceof Dataset) {
            for (Map.Entry<String, DataType> datasetItem : ((Dataset) value).getDataItems(actionExecution.getExecutionControl().getExecutionRuntime()).entrySet()) {
                actionParameterTraces.addAll(getActionParameterTraces(actionExecution, key + datasetItem.getKey(), datasetItem.getValue()));
            }
        } else {
            LOGGER.warn(MessageFormat.format("DataType ''{0}'' is unknown to trace", value.getClass()));
        }
        return actionParameterTraces;
    }
}
