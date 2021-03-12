package io.metadew.iesi.metadata.service.action;

import io.metadew.iesi.datatypes.DataType;
import io.metadew.iesi.datatypes.array.Array;
import io.metadew.iesi.datatypes.dataset.implementation.inmemory.InMemoryDatasetImplementation;
import io.metadew.iesi.datatypes.text.Text;
import io.metadew.iesi.metadata.configuration.action.trace.ActionParameterTraceConfiguration;
import io.metadew.iesi.metadata.definition.action.trace.ActionParameterTrace;
import io.metadew.iesi.metadata.definition.action.trace.key.ActionParameterTraceKey;
import io.metadew.iesi.metadata.definition.template.Template;
import io.metadew.iesi.script.execution.ActionExecution;
import lombok.extern.log4j.Log4j2;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Log4j2
public class ActionParameterTraceService {

    private static ActionParameterTraceService instance;

    public static ActionParameterTraceService getInstance() {
        if (instance == null) {
            instance = new ActionParameterTraceService();
        }
        return instance;
    }

    private ActionParameterTraceService() {
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
        try {
            ActionParameterTraceConfiguration.getInstance().insert(actionParameterTraces);
        } catch (Exception e) {
            StringWriter stackTrace = new StringWriter();
            e.printStackTrace(new PrintWriter(stackTrace));
            log.warn("unable to trace " + prefix + ":" + actionParameterMap.toString() + " due to " + stackTrace.toString());
        }
    }

    public void trace(ActionExecution actionExecution, String key, DataType value) {
        try {
            List<ActionParameterTrace> actionParameterTraces = getActionParameterTraces(actionExecution, key, value);
            ActionParameterTraceConfiguration.getInstance().insert(actionParameterTraces);
        } catch (Exception e) {
            StringWriter stackTrace = new StringWriter();
            e.printStackTrace(new PrintWriter(stackTrace));
            log.warn("unable to trace " + key + ":" + value + " due to " + stackTrace.toString());
        }
    }

    private List<ActionParameterTrace> getActionParameterTraces(ActionExecution actionExecution, String key, DataType value) {
        List<ActionParameterTrace> actionParameterTraces = new ArrayList<>();
        if (value == null) {
            actionParameterTraces.add(new ActionParameterTrace(new ActionParameterTraceKey(actionExecution.getExecutionControl().getRunId(), actionExecution.getProcessId(), actionExecution.getAction().getMetadataKey().getActionId(), key), "null"));
        } else if (value instanceof Text) {
            actionParameterTraces.add(new ActionParameterTrace(new ActionParameterTraceKey(actionExecution.getExecutionControl().getRunId(), actionExecution.getProcessId(), actionExecution.getAction().getMetadataKey().getActionId(), key), ((Text) value).getString()));
        } else if (value instanceof Array) {
            actionParameterTraces.add(new ActionParameterTrace(new ActionParameterTraceKey(actionExecution.getExecutionControl().getRunId(), actionExecution.getProcessId(), actionExecution.getAction().getMetadataKey().getActionId(), key), value.toString()));
//            int counter = 0;
//            for (DataType element : ((Array) value).getList()) {
//                actionParameterTraces.addAll(getActionParameterTraces(actionExecution, key + counter, element));
//                counter++;
//            }
        } else if (value instanceof InMemoryDatasetImplementation) {
            actionParameterTraces.add(new ActionParameterTrace(new ActionParameterTraceKey(actionExecution.getExecutionControl().getRunId(), actionExecution.getProcessId(), actionExecution.getAction().getMetadataKey().getActionId(), key), value.toString()));
//            for (Map.Entry<String, DataType> datasetItem : DatasetHandler.getInstance().getDataItems((Dataset) value, actionExecution.getExecutionControl().getExecutionRuntime()).entrySet()) {
//                actionParameterTraces.addAll(getActionParameterTraces(actionExecution, key + datasetItem.getKey(), datasetItem.getValue()));
//            }
        } else if (value instanceof Template) {
            actionParameterTraces.add(new ActionParameterTrace(new ActionParameterTraceKey(actionExecution.getExecutionControl().getRunId(), actionExecution.getProcessId(), actionExecution.getAction().getMetadataKey().getActionId(), key), value.toString()));
        } else {
            log.warn(MessageFormat.format("DataType ''{0}'' is unknown to trace", value.getClass()));
        }
        return actionParameterTraces;
    }
}
