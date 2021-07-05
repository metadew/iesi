package io.metadew.iesi.server.rest.script.dto.label;

import io.metadew.iesi.metadata.definition.script.ScriptLabel;
import io.metadew.iesi.metadata.definition.script.key.ScriptKey;
import io.metadew.iesi.metadata.definition.script.key.ScriptVersionKey;

public interface IScriptLabelDtoService {

    public ScriptLabel convertToEntity(ScriptLabelDto scriptDto, ScriptVersionKey scriptVersionKey);

    public ScriptLabelDto convertToDto(ScriptLabel scriptLabel);

}
