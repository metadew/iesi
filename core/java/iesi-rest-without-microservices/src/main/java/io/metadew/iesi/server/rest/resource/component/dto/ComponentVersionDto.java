package io.metadew.iesi.server.rest.resource.component.dto;

import io.metadew.iesi.metadata.definition.component.ComponentVersion;
import io.metadew.iesi.metadata.definition.component.key.ComponentVersionKey;
import org.springframework.hateoas.RepresentationModel;

public class ComponentVersionDto extends RepresentationModel<ComponentVersionDto> {

    private long number;
    private String description;

    public ComponentVersionDto() {
    }

    public ComponentVersionDto(long number, String description) {
        super();
        this.number = number;
        this.description = description;
    }

    public ComponentVersion convertToEntity(String name) {
        return new ComponentVersion(new ComponentVersionKey(name, number), description);
    }

    public long getNumber() {
        return number;
    }

    public void setNumber(long number) {
        this.number = number;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }


}