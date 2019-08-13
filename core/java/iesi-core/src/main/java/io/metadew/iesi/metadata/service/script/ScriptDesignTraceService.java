package io.metadew.iesi.metadata.service.script;

import io.metadew.iesi.metadata.configuration.exception.MetadataAlreadyExistsException;
import io.metadew.iesi.metadata.configuration.script.ScriptDesignTraceConfiguration;
import io.metadew.iesi.metadata.configuration.script.ScriptParameterDesignTraceConfiguration;
import io.metadew.iesi.metadata.configuration.script.ScriptVersionDesignTraceConfiguration;
import io.metadew.iesi.metadata.definition.action.Action;
import io.metadew.iesi.metadata.definition.script.*;
import io.metadew.iesi.metadata.service.action.ActionDesignTraceService;
import io.metadew.iesi.script.execution.ScriptExecution;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.SQLException;

public class ScriptDesignTraceService {
    private static final Logger LOGGER = LogManager.getLogger();

    private ScriptDesignTraceConfiguration scriptDesignTraceConfiguration;
    private ScriptVersionDesignTraceConfiguration scriptVersionDesignTraceConfiguration;
    private ScriptParameterDesignTraceConfiguration scriptParameterDesignTraceConfiguration;
    private ActionDesignTraceService actionDesignTraceService;

    public ScriptDesignTraceService() {
        this.scriptDesignTraceConfiguration = new ScriptDesignTraceConfiguration();
        this.scriptVersionDesignTraceConfiguration = new ScriptVersionDesignTraceConfiguration();
        this.scriptParameterDesignTraceConfiguration = new ScriptParameterDesignTraceConfiguration();
        this.actionDesignTraceService = new ActionDesignTraceService();
    }

    public void trace(ScriptExecution scriptExecution) {

        String runId = scriptExecution.getExecutionControl().getRunId();
        long processId = scriptExecution.getProcessId();
        long parentProcessId = scriptExecution.getParentScriptExecution().map(ScriptExecution::getProcessId).orElse(0L);
        Script script = scriptExecution.getScript();

        try {
            scriptDesignTraceConfiguration.insert(new ScriptDesignTrace(runId, processId, parentProcessId, script));

            scriptVersionDesignTraceConfiguration.insert(new ScriptVersionDesignTrace(runId, processId, script.getVersion()));

            for (ScriptParameter scriptParameter : script.getParameters()) {
                scriptParameterDesignTraceConfiguration.insert(new ScriptParameterDesignTrace(runId, processId, scriptParameter));
            }

            for (Action action : script.getActions()) {
                actionDesignTraceService.trace(runId, processId, action);
            }
        } catch (MetadataAlreadyExistsException | SQLException e) {
            StringWriter StackTrace = new StringWriter();
            e.printStackTrace(new PrintWriter(StackTrace));

            LOGGER.warn("exception=" + e.getMessage());
            LOGGER.info("stacktrace" + StackTrace.toString());
        }

    }

}
