package io.metadew.iesi.metadata.service.script;

import io.metadew.iesi.metadata.configuration.script.design.ScriptDesignTraceConfiguration;
import io.metadew.iesi.metadata.configuration.script.design.ScriptLabelDesignTraceConfiguration;
import io.metadew.iesi.metadata.configuration.script.design.ScriptParameterDesignTraceConfiguration;
import io.metadew.iesi.metadata.configuration.script.design.ScriptVersionDesignTraceConfiguration;
import io.metadew.iesi.metadata.definition.script.Script;
import io.metadew.iesi.metadata.definition.script.ScriptLabel;
import io.metadew.iesi.metadata.definition.script.ScriptParameter;
import io.metadew.iesi.metadata.definition.script.ScriptVersion;
import io.metadew.iesi.metadata.definition.script.design.ScriptDesignTrace;
import io.metadew.iesi.metadata.definition.script.design.ScriptLabelDesignTrace;
import io.metadew.iesi.metadata.definition.script.design.ScriptParameterDesignTrace;
import io.metadew.iesi.metadata.definition.script.design.ScriptVersionDesignTrace;
import io.metadew.iesi.metadata.definition.script.design.key.ScriptLabelDesignTraceKey;
import io.metadew.iesi.script.execution.ScriptExecution;
import lombok.extern.log4j.Log4j2;

import java.io.PrintWriter;
import java.io.StringWriter;

@Log4j2
public class ScriptDesignTraceService {

    public ScriptDesignTraceService() {
    }

    public void trace(ScriptExecution scriptExecution) {
        try {
            String runId = scriptExecution.getExecutionControl().getRunId();
            long processId = scriptExecution.getProcessId();
            long parentProcessId = scriptExecution.getParentScriptExecution()
                    .map(ScriptExecution::getProcessId)
                    .orElse(-1L);
            ScriptVersion scriptVersion = scriptExecution.getScriptVersion();

            ScriptDesignTraceConfiguration.getInstance().insert(new ScriptDesignTrace(runId, processId, parentProcessId, scriptVersion.getScript()));
            ScriptVersionDesignTraceConfiguration.getInstance().insert(new ScriptVersionDesignTrace(runId, processId, scriptVersion));

            for (ScriptParameter scriptParameter : scriptVersion.getParameters()) {
                ScriptParameterDesignTraceConfiguration.getInstance().insert(new ScriptParameterDesignTrace(runId, processId, scriptParameter));
            }
            for (ScriptLabel scriptLabel : scriptVersion.getLabels()) {
                ScriptLabelDesignTraceConfiguration.getInstance().insert(new ScriptLabelDesignTrace(
                        new ScriptLabelDesignTraceKey(runId, processId, scriptLabel.getMetadataKey()), scriptLabel.getScriptVersionKey(),
                        scriptLabel.getName(), scriptLabel.getValue()));
            }
        } catch (Exception e) {
            StringWriter stackTrace = new StringWriter();
            e.printStackTrace(new PrintWriter(stackTrace));
            log.warn("unable to trace " + scriptExecution.toString() + " due to " + stackTrace.toString());
        }
    }

}
