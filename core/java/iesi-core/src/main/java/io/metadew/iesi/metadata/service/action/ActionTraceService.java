package io.metadew.iesi.metadata.service.action;

import io.metadew.iesi.datatypes.DataType;
import io.metadew.iesi.metadata.configuration.action.trace.ActionTraceConfiguration;
import io.metadew.iesi.metadata.definition.action.trace.ActionTrace;
import io.metadew.iesi.script.execution.ActionExecution;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Map;

public class ActionTraceService {

    private final ActionParameterTraceService actionParameterTraceService;
    private static final Logger LOGGER = LogManager.getLogger();

    public ActionTraceService() {
        this.actionParameterTraceService = new ActionParameterTraceService();
    }

    public void trace(ActionExecution actionExecution, Map<String, DataType> actionParameterMap) {
        ActionTraceConfiguration.getInstance().insert(new ActionTrace(actionExecution.getExecutionControl().getRunId(), actionExecution.getProcessId(), actionExecution.getAction()));
        actionParameterTraceService.trace(actionExecution, actionParameterMap);

    }


}
