package io.metadew.iesi.server.rest.resource.script.resource;

import io.metadew.iesi.metadata.definition.script.Script;
import io.metadew.iesi.server.rest.controller.ScriptController;
import io.metadew.iesi.server.rest.resource.script.dto.ScriptByNameDto;
import org.springframework.hateoas.mvc.ResourceAssemblerSupport;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

@Component
public class ScriptByNameDtoAssembler extends ResourceAssemblerSupport<List<Script>, ScriptByNameDto> {


    public ScriptByNameDtoAssembler() {
        super(ScriptController.class, ScriptByNameDto.class);
    }

    @Override
    public ScriptByNameDto toResource(List<Script> scripts) {
        if (scripts.isEmpty()) {
            return null;
        } else {
            ScriptByNameDto scriptDto = convertToDto(scripts);
            scriptDto.add(linkTo(methodOn(ScriptController.class)
                    .getByName(scriptDto.getName()))
                    .withSelfRel());
            scriptDto.getVersions().forEach(
                    version -> scriptDto.add(linkTo(methodOn(ScriptController.class)
                            .get(scriptDto.getName(), version))
                            .withRel("version:" + version))
            );
            return scriptDto;
        }
    }

    private ScriptByNameDto convertToDto(List<Script> scripts) {
        return new ScriptByNameDto(scripts.get(0).getName(), scripts.get(0).getDescription(),
                scripts.stream().map(script -> script.getVersion().getNumber()).collect(Collectors.toList()));
    }
}