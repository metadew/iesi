package io.metadew.iesi.server.rest.script.dto.label;

import io.metadew.iesi.metadata.definition.script.ScriptLabel;
import io.metadew.iesi.metadata.definition.script.key.ScriptKey;

public interface IScriptLabelDtoService {

    public ScriptLabel convertToEntity(ScriptLabelDto scriptDto, ScriptKey scriptKey);

    public ScriptLabelDto convertToDto(ScriptLabel scriptLabel);

}
