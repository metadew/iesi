package io.metadew.iesi.metadata.service.action;

import io.metadew.iesi.metadata.configuration.action.design.ActionDesignTraceConfiguration;
import io.metadew.iesi.metadata.configuration.action.design.ActionParameterDesignTraceConfiguration;
import io.metadew.iesi.metadata.configuration.exception.MetadataAlreadyExistsException;
import io.metadew.iesi.metadata.definition.action.Action;
import io.metadew.iesi.metadata.definition.action.ActionParameter;
import io.metadew.iesi.metadata.definition.action.design.ActionDesignTrace;
import io.metadew.iesi.metadata.definition.action.design.ActionParameterDesignTrace;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ActionDesignTraceService {


    private ActionDesignTraceConfiguration actionDesignTraceConfiguration;
    private ActionParameterDesignTraceConfiguration actionParameterDesignTraceConfiguration;
    private static final Logger LOGGER = LogManager.getLogger();


    public ActionDesignTraceService() {
        this.actionDesignTraceConfiguration = new ActionDesignTraceConfiguration();
        this.actionParameterDesignTraceConfiguration = new ActionParameterDesignTraceConfiguration();
    }

    public void trace(String runId, Long processId, Action action) {
        try {
            actionDesignTraceConfiguration.insert(new ActionDesignTrace(runId, processId, action));
            List<ActionParameterDesignTrace> actionParameterDesignTraces = new ArrayList<>();
            for (ActionParameter actionParameter : action.getParameters()) {
                actionParameterDesignTraces.add(new ActionParameterDesignTrace(runId, processId, action.getId(), actionParameter));
            }
            actionParameterDesignTraceConfiguration.insert(actionParameterDesignTraces);
        } catch (MetadataAlreadyExistsException | SQLException e) {
            StringWriter StackTrace = new StringWriter();
            e.printStackTrace(new PrintWriter(StackTrace));

            LOGGER.warn("exception=" + e.getMessage());
            LOGGER.info("stacktrace" + StackTrace.toString());
        }
    }
}
