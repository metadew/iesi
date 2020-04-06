package io.metadew.iesi.server.rest.resource.script.dto;

import io.metadew.iesi.metadata.definition.script.ScriptVersion;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.hateoas.ResourceSupport;

@Data
@EqualsAndHashCode(callSuper = false)
@AllArgsConstructor
@NoArgsConstructor
public class ScriptVersionDto extends ResourceSupport {

    private long number;
    private String description;

    public ScriptVersion convertToEntity(String scriptId) {
        return new ScriptVersion(scriptId, number, description);
    }

    public static ScriptVersionDto convertToDto(ScriptVersion scriptVersion) {
        return new ScriptVersionDto(scriptVersion.getNumber(), scriptVersion.getDescription());
    }

}