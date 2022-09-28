package io.metadew.iesi.metadata.service.action;

import io.metadew.iesi.metadata.configuration.action.design.ActionDesignTraceConfiguration;
import io.metadew.iesi.metadata.configuration.action.design.ActionParameterDesignTraceConfiguration;
import io.metadew.iesi.metadata.definition.action.Action;
import io.metadew.iesi.metadata.definition.action.ActionParameter;
import io.metadew.iesi.metadata.definition.action.design.ActionDesignTrace;
import io.metadew.iesi.metadata.definition.action.design.ActionParameterDesignTrace;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

@Log4j2
@Service
public class ActionDesignTraceService {

    private final ActionDesignTraceConfiguration actionDesignTraceConfiguration;
    private final ActionParameterDesignTraceConfiguration actionParameterDesignTraceConfiguration;

    public ActionDesignTraceService(ActionDesignTraceConfiguration actionDesignTraceConfiguration, ActionParameterDesignTraceConfiguration actionParameterDesignTraceConfiguration) {
        this.actionDesignTraceConfiguration = actionDesignTraceConfiguration;
        this.actionParameterDesignTraceConfiguration = actionParameterDesignTraceConfiguration;
    }


    public void trace(String runId, Long processId, Action action) {
        actionDesignTraceConfiguration.insert(new ActionDesignTrace(runId, processId, action));
        List<ActionParameterDesignTrace> actionParameterDesignTraces = new ArrayList<>();
        for (ActionParameter actionParameter : action.getParameters()) {
            actionParameterDesignTraces.add(new ActionParameterDesignTrace(runId, processId, action.getMetadataKey().getActionId(), actionParameter));
        }
        try {
            actionParameterDesignTraceConfiguration.insert(actionParameterDesignTraces);
        } catch (Exception e) {
            StringWriter stackTrace = new StringWriter();
            e.printStackTrace(new PrintWriter(stackTrace));
            log.warn("unable to trace " + processId + ":" + action.toString() + " due to " + stackTrace.toString());
        }
    }
}
