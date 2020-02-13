package io.metadew.iesi.metadata.service.action;

import io.metadew.iesi.metadata.configuration.action.design.ActionDesignTraceConfiguration;
import io.metadew.iesi.metadata.configuration.action.design.ActionParameterDesignTraceConfiguration;
import io.metadew.iesi.metadata.definition.action.Action;
import io.metadew.iesi.metadata.definition.action.ActionParameter;
import io.metadew.iesi.metadata.definition.action.design.ActionDesignTrace;
import io.metadew.iesi.metadata.definition.action.design.ActionParameterDesignTrace;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

public class ActionDesignTraceService {


    private static final Logger LOGGER = LogManager.getLogger();


    public ActionDesignTraceService() {
    }

    public void trace(String runId, Long processId, Action action) {
            ActionDesignTraceConfiguration.getInstance().insert(new ActionDesignTrace(runId, processId, action));
            List<ActionParameterDesignTrace> actionParameterDesignTraces = new ArrayList<>();
            for (ActionParameter actionParameter : action.getParameters()) {
                actionParameterDesignTraces.add(new ActionParameterDesignTrace(runId, processId, action.getMetadataKey().getActionId(), actionParameter));
            }
            ActionParameterDesignTraceConfiguration.getInstance().insert(actionParameterDesignTraces);
    }
}
