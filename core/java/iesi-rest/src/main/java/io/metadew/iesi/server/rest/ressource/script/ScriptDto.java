package io.metadew.iesi.server.rest.ressource.script;


import io.metadew.iesi.metadata.definition.Script;
import lombok.Getter;
import lombok.Setter;
import org.springframework.hateoas.ResourceSupport;


public class ScriptDto extends ResourceSupport {

    @Setter private String name;
    @Getter @Setter private String description;
    public ScriptDto() {}

    public ScriptDto(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public Script convertToEntity() {
        return new Script(name,  description, description, null, null, null);
    }

    public static ScriptDto convertToDto(Script connection) {
        return new ScriptDto(connection.getName(), connection.getDescription());
    }

    public String getName() {
        return name;
    }

}