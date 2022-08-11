package io.metadew.iesi.metadata.service.script;

import io.metadew.iesi.metadata.configuration.script.trace.ScriptLabelTraceConfiguration;
import io.metadew.iesi.metadata.configuration.script.trace.ScriptParameterTraceConfiguration;
import io.metadew.iesi.metadata.configuration.script.trace.ScriptTraceConfiguration;
import io.metadew.iesi.metadata.configuration.script.trace.ScriptVersionTraceConfiguration;
import io.metadew.iesi.metadata.definition.script.ScriptLabel;
import io.metadew.iesi.metadata.definition.script.ScriptParameter;
import io.metadew.iesi.metadata.definition.script.trace.ScriptLabelTrace;
import io.metadew.iesi.metadata.definition.script.trace.ScriptParameterTrace;
import io.metadew.iesi.metadata.definition.script.trace.ScriptTrace;
import io.metadew.iesi.metadata.definition.script.trace.ScriptVersionTrace;
import io.metadew.iesi.metadata.definition.script.trace.key.ScriptLabelTraceKey;
import io.metadew.iesi.script.execution.ScriptExecution;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.io.PrintWriter;
import java.io.StringWriter;

@Log4j2
@Service
public class ScriptTraceService {

    private static ScriptTraceService instance;
    private final ScriptTraceConfiguration scriptTraceConfiguration;
    private final ScriptVersionTraceConfiguration scriptVersionTraceConfigurations;
    private final ScriptParameterTraceConfiguration scriptParameterTraceConfiguration;
    private final ScriptLabelTraceConfiguration scriptLabelTraceConfiguration;

    public ScriptTraceService(ScriptTraceConfiguration scriptTraceConfiguration,
                              ScriptVersionTraceConfiguration scriptVersionTraceConfigurations,
                              ScriptParameterTraceConfiguration scriptParameterTraceConfiguration,
                              ScriptLabelTraceConfiguration scriptLabelTraceConfiguration) {
        this.scriptTraceConfiguration = scriptTraceConfiguration;
        this.scriptVersionTraceConfigurations = scriptVersionTraceConfigurations;
        this.scriptParameterTraceConfiguration = scriptParameterTraceConfiguration;
        this.scriptLabelTraceConfiguration = scriptLabelTraceConfiguration;
    }

    public void trace(ScriptExecution scriptExecution) {
        try {
            String runId = scriptExecution.getExecutionControl().getRunId();
            Long processId = scriptExecution.getProcessId();
            scriptTraceConfiguration.insert(new ScriptTrace(runId, processId,
                    scriptExecution.getParentScriptExecution().map(ScriptExecution::getProcessId).orElse(0L),
                    scriptExecution.getScript()));
            scriptVersionTraceConfigurations.insert(new ScriptVersionTrace(runId, processId, scriptExecution.getScript().getVersion()));
            for (ScriptParameter scriptParameter : scriptExecution.getScript().getParameters()) {
                scriptParameterTraceConfiguration.insert(new ScriptParameterTrace(runId, processId, scriptParameter));
            }
            for (ScriptLabel scriptLabel : scriptExecution.getScript().getLabels()) {
                scriptLabelTraceConfiguration.insert(new ScriptLabelTrace(
                        new ScriptLabelTraceKey(runId, processId, scriptLabel.getMetadataKey()),
                        scriptLabel.getScriptKey(), scriptLabel.getName(), scriptLabel.getValue()));
            }
        } catch (Exception e) {
            StringWriter stackTrace = new StringWriter();
            e.printStackTrace(new PrintWriter(stackTrace));
            log.warn("unable to trace " + scriptExecution.toString() + " due to " + stackTrace.toString());
        }

    }

}
