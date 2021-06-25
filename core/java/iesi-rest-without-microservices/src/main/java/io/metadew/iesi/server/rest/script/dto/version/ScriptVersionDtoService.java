package io.metadew.iesi.server.rest.script.dto.version;

import io.metadew.iesi.metadata.definition.script.ScriptVersion;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class ScriptVersionDtoService implements IScriptVersionDtoService {

    public ScriptVersion convertToEntity(ScriptVersionDto scriptVersionDto, String scriptId) {
        return new ScriptVersion(scriptId,
                scriptVersionDto.getNumber(),
                scriptVersionDto.getDescription(),
                SecurityContextHolder.getContext().getAuthentication().getName(),
                LocalDateTime.now().toString(),
                SecurityContextHolder.getContext().getAuthentication().getName(),
                LocalDateTime.now().toString(),
                scriptVersionDto.getDeletedAt());
    }

    public ScriptVersionDto convertToDto(ScriptVersion scriptVersion) {
        return new ScriptVersionDto(scriptVersion.getNumber(),
                scriptVersion.getDescription(),
                scriptVersion.getMetadataKey().getScriptKey().getDeletedAt());
    }

}
