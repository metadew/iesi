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
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ScriptTraceService {

    public ScriptTraceService() {
    }

    public void trace(ScriptExecution scriptExecution) {
        String runId = scriptExecution.getExecutionControl().getRunId();
        Long processId = scriptExecution.getProcessId();
        ScriptTraceConfiguration.getInstance().insert(new ScriptTrace(runId, processId,
                scriptExecution.getParentScriptExecution().map(ScriptExecution::getProcessId).orElse(0L),
                scriptExecution.getScript()));
        ScriptVersionTraceConfiguration.getInstance().insert(new ScriptVersionTrace(runId, processId, scriptExecution.getScript().getVersion()));
        for (ScriptParameter scriptParameter : scriptExecution.getScript().getParameters()) {
            ScriptParameterTraceConfiguration.getInstance().insert(new ScriptParameterTrace(runId, processId, scriptParameter));
        }
        for (ScriptLabel scriptLabel : scriptExecution.getScript().getLabels()) {
            ScriptLabelTraceConfiguration.getInstance().insert(new ScriptLabelTrace(
                    new ScriptLabelTraceKey(runId, processId, scriptLabel.getMetadataKey()),
                    scriptLabel.getScriptKey(), scriptLabel.getName(), scriptLabel.getValue()));
        }

    }

}
