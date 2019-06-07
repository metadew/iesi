package io.metadew.iesi.server.rest.ressource.script;

import io.metadew.iesi.metadata.definition.ScriptVersion;
import org.springframework.hateoas.ResourceSupport;

import java.util.List;

public class ScriptGetByNameDto extends ResourceSupport {

    private String type;
    private String description;
    private List<ScriptVersion> versions;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<ScriptVersion> getVersions() {
        return versions;
    }

    public void setVersions(List<ScriptVersion> versions) {
        this.versions = versions;
    }
}
