package io.metadew.iesi.server.rest.script.dto.version;

import io.metadew.iesi.metadata.definition.script.ScriptVersion;
import org.springframework.stereotype.Service;

@Service
public class ScriptVersionDtoService implements IScriptVersionDtoService {

    public ScriptVersion convertToEntity(ScriptVersionDto scriptVersionDto, String scriptId) {
        return new ScriptVersion(scriptId, scriptVersionDto.getNumber(), scriptVersionDto.getDescription());

    }

    public ScriptVersionDto convertToDto(ScriptVersion scriptVersion) {
        return new ScriptVersionDto(scriptVersion.getNumber(), scriptVersion.getDescription());
    }

}
