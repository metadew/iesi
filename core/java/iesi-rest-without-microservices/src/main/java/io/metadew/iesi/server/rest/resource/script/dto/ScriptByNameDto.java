package io.metadew.iesi.server.rest.resource.script.dto;

import org.springframework.hateoas.ResourceSupport;

import java.util.List;

public class ScriptByNameDto extends ResourceSupport {

    private String name;
    private String description;
    private List<Long> versions;

    public ScriptByNameDto() {}

    public ScriptByNameDto(String name, String description, List<Long> versions) {
        this.name = name;
        this.description = description;
        this.versions = versions;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<Long> getVersions() {
        return versions;
    }

    public void setVersions(List<Long> versions) {
        this.versions = versions;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
