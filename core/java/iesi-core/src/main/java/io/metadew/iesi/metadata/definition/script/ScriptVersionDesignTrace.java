package io.metadew.iesi.metadata.definition.script;

import io.metadew.iesi.metadata.definition.Metadata;
import io.metadew.iesi.metadata.definition.script.key.ScriptVersionDesignTraceKey;

public class ScriptVersionDesignTrace extends Metadata<ScriptVersionDesignTraceKey> {

    private Long scriptVersionNumber;
    private String scriptVersionDescription;

    public ScriptVersionDesignTrace(ScriptVersionDesignTraceKey scriptVersionDesignTraceKey, Long scriptVersionNumber, String scriptVersionDescription) {
        super(scriptVersionDesignTraceKey);
        this.scriptVersionNumber = scriptVersionNumber;
        this.scriptVersionDescription = scriptVersionDescription;
    }

    public ScriptVersionDesignTrace(String runId, Long processId, ScriptVersion scriptVersion) {
        this(new ScriptVersionDesignTraceKey(runId, processId), scriptVersion.getNumber(), scriptVersion.getDescription());
    }

    public Long getScriptVersionNumber() {
        return scriptVersionNumber;
    }

    public String getScriptVersionDescription() {
        return scriptVersionDescription;
    }
}