package io.metadew.iesi.metadata.service.script;

import io.metadew.iesi.metadata.configuration.exception.MetadataAlreadyExistsException;
import io.metadew.iesi.metadata.configuration.script.design.ScriptDesignTraceConfiguration;
import io.metadew.iesi.metadata.configuration.script.design.ScriptParameterDesignTraceConfiguration;
import io.metadew.iesi.metadata.configuration.script.design.ScriptVersionDesignTraceConfiguration;
import io.metadew.iesi.metadata.definition.action.Action;
import io.metadew.iesi.metadata.definition.script.Script;
import io.metadew.iesi.metadata.definition.script.ScriptParameter;
import io.metadew.iesi.metadata.definition.script.design.ScriptDesignTrace;
import io.metadew.iesi.metadata.definition.script.design.ScriptParameterDesignTrace;
import io.metadew.iesi.metadata.definition.script.design.ScriptVersionDesignTrace;
import io.metadew.iesi.metadata.service.action.ActionDesignTraceService;
import io.metadew.iesi.script.execution.ScriptExecution;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.PrintWriter;
import java.io.StringWriter;

public class ScriptDesignTraceService {
    private static final Logger LOGGER = LogManager.getLogger();

    private ActionDesignTraceService actionDesignTraceService;

    public ScriptDesignTraceService() {
        this.actionDesignTraceService = new ActionDesignTraceService();
    }

    public void trace(ScriptExecution scriptExecution) {

        String runId = scriptExecution.getExecutionControl().getRunId();
        long processId = scriptExecution.getProcessId();
        long parentProcessId = scriptExecution.getParentScriptExecution().map(ScriptExecution::getProcessId).orElse(-1L);
        Script script = scriptExecution.getScript();

        try {
            ScriptDesignTraceConfiguration.getInstance().insert(new ScriptDesignTrace(runId, processId, parentProcessId, script));
            ScriptVersionDesignTraceConfiguration.getInstance().insert(new ScriptVersionDesignTrace(runId, processId, script.getVersion()));

            for (ScriptParameter scriptParameter : script.getParameters()) {
                ScriptParameterDesignTraceConfiguration.getInstance().insert(new ScriptParameterDesignTrace(runId, processId, scriptParameter));
            }

        } catch (MetadataAlreadyExistsException e) {
            StringWriter StackTrace = new StringWriter();
            e.printStackTrace(new PrintWriter(StackTrace));

            LOGGER.warn("exception=" + e.getMessage());
            LOGGER.info("stacktrace" + StackTrace.toString());
        }

    }

}
