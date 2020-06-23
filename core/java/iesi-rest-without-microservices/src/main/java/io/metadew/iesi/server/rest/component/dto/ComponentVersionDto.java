package io.metadew.iesi.server.rest.component.dto;

import io.metadew.iesi.metadata.definition.component.ComponentVersion;
import io.metadew.iesi.metadata.definition.component.key.ComponentVersionKey;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.hateoas.RepresentationModel;

@Data
@EqualsAndHashCode(callSuper = false)
@AllArgsConstructor
@NoArgsConstructor
public class ComponentVersionDto extends RepresentationModel<ComponentVersionDto> {

    private long number;
    private String description;

    public ComponentVersion convertToEntity(String componentId) {
        return new ComponentVersion(new ComponentVersionKey(componentId, number), description);
    }

}