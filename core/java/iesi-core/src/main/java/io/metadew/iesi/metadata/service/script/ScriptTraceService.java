package io.metadew.iesi.metadata.service.script;

import io.metadew.iesi.metadata.configuration.exception.MetadataAlreadyExistsException;
import io.metadew.iesi.metadata.configuration.script.ScriptParameterTraceConfiguration;
import io.metadew.iesi.metadata.configuration.script.ScriptTraceConfiguration;
import io.metadew.iesi.metadata.configuration.script.ScriptVersionTraceConfiguration;
import io.metadew.iesi.metadata.definition.script.ScriptParameter;
import io.metadew.iesi.metadata.definition.script.ScriptParameterTrace;
import io.metadew.iesi.metadata.definition.script.ScriptTrace;
import io.metadew.iesi.metadata.definition.script.ScriptVersionTrace;
import io.metadew.iesi.script.execution.ScriptExecution;

import java.sql.SQLException;

public class ScriptTraceService {

    private ScriptTraceConfiguration scriptTraceConfiguration;
    private ScriptVersionTraceConfiguration scriptVersionTraceConfiguration;
    private ScriptParameterTraceConfiguration scriptParameterTraceConfiguration;


    public ScriptTraceService() {
        this.scriptTraceConfiguration = new ScriptTraceConfiguration();
        this.scriptVersionTraceConfiguration = new ScriptVersionTraceConfiguration();
        this.scriptParameterTraceConfiguration = new ScriptParameterTraceConfiguration();
    }

    public void trace(ScriptExecution scriptExecution) {
        try {
            String runId = scriptExecution.getExecutionControl().getRunId();
            Long processId = scriptExecution.getProcessId();
            scriptTraceConfiguration.insert(new ScriptTrace(runId, processId,
                    scriptExecution.getParentScriptExecution().map(ScriptExecution::getProcessId).orElse(0L),
                    scriptExecution.getScript()));
            scriptVersionTraceConfiguration.insert(new ScriptVersionTrace(runId, processId, scriptExecution.getScript().getVersion()));
            for (ScriptParameter scriptParameter : scriptExecution.getScript().getParameters()) {
                scriptParameterTraceConfiguration.insert(new ScriptParameterTrace(runId, processId, scriptParameter));
            }

        } catch (MetadataAlreadyExistsException | SQLException e) {
            e.printStackTrace();
        }
    }

}
