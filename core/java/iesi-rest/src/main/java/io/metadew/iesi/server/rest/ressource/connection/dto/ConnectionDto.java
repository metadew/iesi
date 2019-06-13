package io.metadew.iesi.server.rest.ressource.connection.dto;


import io.metadew.iesi.metadata.definition.Connection;
import io.metadew.iesi.metadata.definition.ConnectionParameter;
import lombok.Getter;
import lombok.Setter;
import org.springframework.hateoas.ResourceSupport;

import java.util.List;

public class ConnectionDto extends ResourceSupport {

    @Setter private String name;
    @Getter @Setter private String type;
    @Getter @Setter private String description;
    @Setter private String environment;
    @Getter @Setter private List<ConnectionParameter> parameters;

    public ConnectionDto() {}

    public ConnectionDto(String name, String type, String description, String environment, List<ConnectionParameter> parameters) {
        this.name = name;
        this.type = type;
        this.description = description;
        this.environment = environment;
        this.parameters = parameters;
    }

    public Connection convertToEntity() {
        return new Connection(name, type, description, environment, parameters);
    }

    public static ConnectionDto convertToDto(Connection connection) {
        return new ConnectionDto(connection.getName(), connection.getType(), connection.getDescription(), connection.getEnvironment(), connection.getParameters());
    }

    public String getName() {
        return name;
    }

    public String getEnvironment() {
        return environment;
    }

    public void setName(String name) {
        this.name = name;
    }

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

    public void setEnvironment(String environment) {
        this.environment = environment;
    }

    public List<ConnectionParameter> getParameters() {
        return parameters;
    }

    public void setParameters(List<ConnectionParameter> parameters) {
        this.parameters = parameters;
    }
}
