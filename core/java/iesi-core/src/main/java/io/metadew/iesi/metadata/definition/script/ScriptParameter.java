package io.metadew.iesi.metadata.definition.script;


import io.metadew.iesi.metadata.definition.Metadata;
import io.metadew.iesi.metadata.definition.script.key.ScriptParameterKey;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class ScriptParameter extends Metadata<ScriptParameterKey> {

    private String value;

    @Builder
    public ScriptParameter(ScriptParameterKey scriptParameterKey, String value) {
        super(scriptParameterKey);
        this.value = value;
    }

}