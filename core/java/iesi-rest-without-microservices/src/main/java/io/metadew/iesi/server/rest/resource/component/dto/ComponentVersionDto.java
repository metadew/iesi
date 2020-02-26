package io.metadew.iesi.server.rest.resource.component.dto;

import io.metadew.iesi.metadata.definition.component.ComponentVersion;
import io.metadew.iesi.metadata.definition.component.key.ComponentVersionKey;
import io.metadew.iesi.server.rest.resource.Dto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
@AllArgsConstructor
public class ComponentVersionDto extends Dto {

    private long number;
    private String description;

    public ComponentVersion convertToEntity(String componentId) {
        return new ComponentVersion(new ComponentVersionKey(componentId, number), description);
    }

}