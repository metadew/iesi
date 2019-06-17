package io.metadew.iesi.server.rest.resource.component.dto;

import org.springframework.hateoas.ResourceSupport;

import java.util.List;

public class ComponentByNameDto extends ResourceSupport {

    private String type;
    private String name;
    private String description;
    private List<Long> versions;

    public ComponentByNameDto() {}

    public ComponentByNameDto(String name, String type, String description, List<Long> versions) {
        this.name = name;
        this.type = type;
        this.description = description;
        this.versions = versions;
    }

    public String getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setVersions(List<Long> versions) {
        this.versions = versions;
    }

    public List<Long> getVersions() {
        return versions;
    }



}
