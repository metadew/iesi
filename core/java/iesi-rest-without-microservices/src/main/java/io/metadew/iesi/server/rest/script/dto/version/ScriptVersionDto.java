package io.metadew.iesi.server.rest.script.dto.version;

import io.metadew.iesi.metadata.definition.script.ScriptVersion;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.hateoas.RepresentationModel;

@Data
@EqualsAndHashCode(callSuper = false)
@AllArgsConstructor
@NoArgsConstructor
public class ScriptVersionDto extends RepresentationModel<ScriptVersionDto> {

    private long number;
    private String description;

}