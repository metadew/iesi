package io.metadew.iesi.server.rest.resource.script.resource;


import io.metadew.iesi.metadata.definition.action.Action;
import io.metadew.iesi.metadata.definition.action.ActionParameter;
import io.metadew.iesi.metadata.definition.script.Script;
import io.metadew.iesi.metadata.definition.script.ScriptLabel;
import io.metadew.iesi.metadata.definition.script.ScriptParameter;
import io.metadew.iesi.server.rest.controller.ScriptController;
import io.metadew.iesi.server.rest.resource.script.dto.*;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.mvc.ResourceAssemblerSupport;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

@Component
public class ScriptDtoResourceAssembler extends ResourceAssemblerSupport<Script, ScriptDto> {

    public ScriptDtoResourceAssembler() {
        super(ScriptController.class, ScriptDto.class);
    }

    @Override
    public ScriptDto toResource(Script script) {
        ScriptDto scriptByNameDto = convertToDto(script);
        Link selfLink = linkTo(methodOn(ScriptController.class).get(script.getName(), script.getVersion().getNumber()))
                .withRel("script:" + scriptByNameDto.getName() + "-" + scriptByNameDto.getVersion().getNumber());
        scriptByNameDto.add(selfLink);
//        Link versionLink = linkTo(methodOn(ScriptController.class).executeScript(null, scriptByNameDto.getName(), scriptByNameDto.getVersion().getNumber()))
//                .withRel("script");
//        scriptByNameDto.add(versionLink);
        return scriptByNameDto;
    }

    private ScriptDto convertToDto(Script script) {
        return new ScriptDto(script.getName(), script.getDescription(),
                ScriptVersionDto.convertToDto(script.getVersion()),
                script.getParameters().stream().map(this::convertToDto).collect(Collectors.toList()),
                script.getActions().stream().map(this::convertToDto).collect(Collectors.toList()),
                script.getLabels().stream().map(this::convertToDto).collect(Collectors.toList()));
    }


    private ActionDto convertToDto(Action action) {
        return new ActionDto(action.getNumber(), action.getName(), action.getType(), action.getDescription(), action.getComponent(),
                action.getCondition(), action.getIteration(), action.getErrorExpected(), action.getErrorStop(), action.getRetries(),
                action.getParameters().stream().map(this::convertToDto).collect(Collectors.toList()));
    }

    private ScriptParameterDto convertToDto(ScriptParameter scriptParameter) {
        return new ScriptParameterDto(scriptParameter.getMetadataKey().getParameterName(), scriptParameter.getValue());
    }

    private ScriptLabelDto convertToDto(ScriptLabel scriptLabel) {
        return new ScriptLabelDto(scriptLabel.getName(), scriptLabel.getValue());
    }

    private ActionParameterDto convertToDto(ActionParameter actionParameter) {
        return new ActionParameterDto(actionParameter.getMetadataKey().getParameterName(), actionParameter.getValue());
    }

}