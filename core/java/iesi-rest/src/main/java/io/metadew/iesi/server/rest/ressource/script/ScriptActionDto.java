package io.metadew.iesi.server.rest.ressource.script;

import io.metadew.iesi.metadata.definition.Action;
import io.metadew.iesi.metadata.definition.ActionParameter;
import io.metadew.iesi.metadata.definition.Script;
import io.metadew.iesi.metadata.definition.ScriptParameter;
import org.springframework.hateoas.ResourceSupport;

import java.util.Collections;
import java.util.List;

public class ScriptActionDto extends ResourceSupport {

    private String id;
    private long number;
    private String name;
    private String description;
    private String component;
    private String errorExpected;
    private String errorStop;
    private String type;
    private List<ActionParameter>  parameters;

    public ScriptActionDto(String id, long number, String name, String description, String component, String errorExpected, String errorStop, String type, List<ActionParameter> parameters) {
        this.id = id;
        this.number = number;
        this.name = name;
        this.description = description;
        this.component = component;
        this.errorExpected = errorExpected;
        this.errorStop = errorStop;
        this.type = type;
        this.parameters = parameters;
    }

    public List<Action> convertToEntity(){
        return Collections.singletonList(new Action(id, number, name, description, null, errorExpected, errorStop, type, null, null, null, parameters));

    }

    public static ScriptActionDto convertToDto(List<Action> action){
        return new ScriptActionDto(action.get(0).getId(),action.get(0).getNumber(),action.get(0).getName(),action.get(0).getDescription(),action.get(0).getComponent(),action.get(0).getErrorExpected(),action.get(0).getErrorStop(),action.get(0).getType(),action.get(0).getParameters());
    }


//    @Override
//    public String getId() {
//        return id;
//    }
//
//    public void setId(String id) {
//        this.id = id;
//    }

    public long getNumber() {
        return number;
    }

    public void setNumber(long number) {
        this.number = number;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getComponent() {
        return component;
    }

    public void setComponent(String component) {
        this.component = component;
    }

    public String getErrorExpected() {
        return errorExpected;
    }

    public void setErrorExpected(String errorExpected) {
        this.errorExpected = errorExpected;
    }

    public String getErrorStop() {
        return errorStop;
    }

    public void setErrorStop(String errorStop) {
        this.errorStop = errorStop;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<ActionParameter> getParameters() {
        return parameters;
    }

    public void setParameters(List<ActionParameter> parameters) {
        this.parameters = parameters;
    }
}
