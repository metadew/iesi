package io.metadew.iesi.metadata.service.action;

import io.metadew.iesi.datatypes.DataType;
import io.metadew.iesi.metadata.configuration.action.trace.ActionTraceConfiguration;
import io.metadew.iesi.metadata.definition.action.trace.ActionTrace;
import io.metadew.iesi.script.execution.ActionExecution;
import lombok.extern.log4j.Log4j2;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Map;
@Log4j2
public class ActionTraceService {

    private static ActionTraceService instance;

    public static ActionTraceService getInstance() {
        if (instance == null) {
            instance = new ActionTraceService();
        }
        return instance;
    }

    private ActionTraceService() {
    }

    public void trace(ActionExecution actionExecution, Map<String, DataType> actionParameterMap) {
        try {
            ActionTraceConfiguration.getInstance().insert(new ActionTrace(actionExecution.getExecutionControl().getRunId(), actionExecution.getProcessId(), actionExecution.getAction()));
            ActionParameterTraceService.getInstance().trace(actionExecution, actionParameterMap);
        } catch (Exception e) {
            StringWriter stackTrace = new StringWriter();
            e.printStackTrace(new PrintWriter(stackTrace));
            log.warn("unable to trace " + actionParameterMap.toString() + " due to " + stackTrace.toString());
        }
    }


}
