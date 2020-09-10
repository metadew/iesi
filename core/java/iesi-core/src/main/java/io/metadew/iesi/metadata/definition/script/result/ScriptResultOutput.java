package io.metadew.iesi.metadata.definition.script.result;


import io.metadew.iesi.metadata.definition.Metadata;
import io.metadew.iesi.metadata.definition.script.result.key.ScriptResultOutputKey;

public class ScriptResultOutput extends Metadata<ScriptResultOutputKey> {

    private final String scriptId;
    private final String value;

    public ScriptResultOutput(ScriptResultOutputKey scriptResultOutputKey, String scriptId, String value) {
        super(scriptResultOutputKey);
        this.scriptId = scriptId;
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public String getScriptId() {
        return scriptId;
    }
}