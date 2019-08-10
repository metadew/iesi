package io.metadew.iesi.server.rest.resource.script.dto;

import io.metadew.iesi.metadata.definition.script.ScriptVersion;
import org.springframework.hateoas.ResourceSupport;

public class ScriptVersionDto extends ResourceSupport {

    private long number;
    private String description;

    public ScriptVersionDto() {}


    public ScriptVersionDto(long number, String description) {
        super();
        this.number = number;
        this.description = description;
    }

    public ScriptVersion convertToEntity() {
        //TODO: Why is script id passed
        return new ScriptVersion(
                null, number, description);
    }

    public static ScriptVersionDto convertToDto(ScriptVersion scriptVersion) {
        return new ScriptVersionDto(scriptVersion.getNumber(), scriptVersion.getDescription());
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