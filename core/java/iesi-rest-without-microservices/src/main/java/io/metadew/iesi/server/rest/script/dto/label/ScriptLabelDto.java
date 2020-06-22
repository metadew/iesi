package io.metadew.iesi.server.rest.script.dto.label;

import io.metadew.iesi.metadata.definition.script.ScriptLabel;
import io.metadew.iesi.metadata.definition.script.key.ScriptKey;
import io.metadew.iesi.metadata.definition.script.key.ScriptLabelKey;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.commons.codec.digest.DigestUtils;

@Data
public class ScriptLabelDto {

    private final String name;
    private final String value;

    public ScriptLabel convertToEntity(ScriptKey scriptKey) {
        return new ScriptLabel(new ScriptLabelKey(DigestUtils.sha256Hex(scriptKey.getScriptId()+scriptKey.getScriptVersion()+name)), scriptKey, name, value);
    }

}
