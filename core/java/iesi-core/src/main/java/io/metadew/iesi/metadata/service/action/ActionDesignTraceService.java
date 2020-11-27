package io.metadew.iesi.metadata.service.action;

import io.metadew.iesi.metadata.configuration.action.design.ActionDesignTraceConfiguration;
import io.metadew.iesi.metadata.configuration.action.design.ActionParameterDesignTraceConfiguration;
import io.metadew.iesi.metadata.definition.action.Action;
import io.metadew.iesi.metadata.definition.action.ActionParameter;
import io.metadew.iesi.metadata.definition.action.design.ActionDesignTrace;
import io.metadew.iesi.metadata.definition.action.design.ActionParameterDesignTrace;
import io.metadew.iesi.metadata.service.script.ScriptDesignTraceService;
import lombok.extern.log4j.Log4j2;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

@Log4j2
public class ActionDesignTraceService {

    private static ActionDesignTraceService instance;

    public static synchronized ActionDesignTraceService getInstance() {
        if (instance == null) {
            instance = new ActionDesignTraceService();
        }
        return instance;
    }

    private ActionDesignTraceService() {
    }

    public void trace(String runId, Long processId, Action action) {
        ActionDesignTraceConfiguration.getInstance().insert(new ActionDesignTrace(runId, processId, action));
        List<ActionParameterDesignTrace> actionParameterDesignTraces = new ArrayList<>();
        for (ActionParameter actionParameter : action.getParameters()) {
            actionParameterDesignTraces.add(new ActionParameterDesignTrace(runId, processId, action.getMetadataKey().getActionId(), actionParameter));
        }
        try {
            ActionParameterDesignTraceConfiguration.getInstance().insert(actionParameterDesignTraces);
        } catch (Exception e) {
            StringWriter stackTrace = new StringWriter();
            e.printStackTrace(new PrintWriter(stackTrace));
            log.warn("unable to trace " + processId + ":" + action.toString() + " due to " + stackTrace.toString());
        }
    }
}
