package io.metadew.iesi.server.rest.ressource.component;


import io.metadew.iesi.metadata.definition.ComponentVersion;
import org.springframework.hateoas.ResourceSupport;

public class ComponentVersionDto extends ResourceSupport {

    private long number;
    private String description;

    public ComponentVersionDto() {}


    public ComponentVersionDto(long number, String description) {
        super();
        this.number = number;
        this.description = description;
    }

    public ComponentVersion convertToEntity() {
        return new ComponentVersion(
                number, description);
    }

    public static ComponentVersionDto convertToDto(ComponentVersion componentVersion) {
        return new ComponentVersionDto(componentVersion.getNumber(), componentVersion.getDescription());
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