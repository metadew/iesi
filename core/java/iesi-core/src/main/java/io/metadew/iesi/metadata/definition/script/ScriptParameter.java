package io.metadew.iesi.metadata.definition.script;


import io.metadew.iesi.metadata.definition.Metadata;
import io.metadew.iesi.metadata.definition.script.key.ScriptParameterKey;

public class ScriptParameter extends Metadata<ScriptParameterKey> {

    private String value;

    public ScriptParameter(ScriptParameterKey scriptParameterKey, String value) {
        super(scriptParameterKey);
        this.value = value;
    }

    //Getters and Setters
    public String getName() {
        return getMetadataKey().getParameterName();
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

}