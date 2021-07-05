package io.metadew.iesi.server.rest.script.dto.label;

import io.metadew.iesi.metadata.definition.script.ScriptLabel;
import io.metadew.iesi.metadata.definition.script.ScriptVersion;
import io.metadew.iesi.metadata.definition.script.key.ScriptKey;
import io.metadew.iesi.metadata.definition.script.key.ScriptLabelKey;
import io.metadew.iesi.metadata.definition.script.key.ScriptVersionKey;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class ScriptLabelDtoService implements IScriptLabelDtoService {

    public ScriptLabel convertToEntity(ScriptLabelDto scriptLabelDto, ScriptVersionKey scriptVersionKey) {
        return new ScriptLabel(new ScriptLabelKey(UUID.randomUUID().toString()),
                scriptVersionKey, scriptLabelDto.getName(), scriptLabelDto.getValue());
    }

    public ScriptLabelDto convertToDto(ScriptLabel scriptLabel) {
        return new ScriptLabelDto(scriptLabel.getName(), scriptLabel.getValue());
    }

}
