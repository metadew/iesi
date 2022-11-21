package io.metadew.iesi.metadata.service.action;

import io.metadew.iesi.datatypes.DataType;
import io.metadew.iesi.metadata.configuration.action.trace.ActionTraceConfiguration;
import io.metadew.iesi.metadata.definition.action.trace.ActionTrace;
import io.metadew.iesi.script.execution.ActionExecution;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Map;

@Log4j2
@Service
public class ActionTraceService {

    private final ActionTraceConfiguration actionTraceConfiguration;
    private final ActionParameterTraceService actionParameterTraceService;

    public ActionTraceService(ActionTraceConfiguration actionTraceConfiguration, ActionParameterTraceService actionParameterTraceService) {
        this.actionTraceConfiguration = actionTraceConfiguration;
        this.actionParameterTraceService = actionParameterTraceService;
    }

    public void trace(ActionExecution actionExecution, Map<String, DataType> actionParameterMap) {
        try {
            actionTraceConfiguration.insert(new ActionTrace(actionExecution.getExecutionControl().getRunId(), actionExecution.getProcessId(), actionExecution.getAction()));
            actionParameterTraceService.trace(actionExecution, actionParameterMap);
        } catch (Exception e) {
            StringWriter stackTrace = new StringWriter();
            e.printStackTrace(new PrintWriter(stackTrace));
            log.warn("unable to trace " + actionParameterMap.toString() + " due to " + stackTrace.toString());
        }
    }


}
