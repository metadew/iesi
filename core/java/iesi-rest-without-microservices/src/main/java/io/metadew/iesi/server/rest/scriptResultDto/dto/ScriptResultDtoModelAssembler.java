package io.metadew.iesi.server.rest.scriptResultDto.dto;

import io.metadew.iesi.metadata.definition.script.result.ScriptResult;
import io.metadew.iesi.server.rest.scriptResultDto.ScriptResultController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.server.mvc.RepresentationModelAssemblerSupport;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class ScriptResultDtoModelAssembler extends RepresentationModelAssemblerSupport<ScriptResult, ScriptResultDto> {

    @Autowired
    public ScriptResultDtoModelAssembler() {
        super(ScriptResultController.class, ScriptResultDto.class);
    }

    @Override
    public ScriptResultDto toModel(ScriptResult scriptResult) {
        ScriptResultDto scriptResultDto = convertToDto(scriptResult);
        Link selfLink = linkTo(
                methodOn(ScriptResultController.class)
                        .getByRunIdAndProcessId(scriptResultDto.getRunID(), scriptResultDto.getProcessId()))
                .withSelfRel();
        Link linkToAll = linkTo(
                methodOn(ScriptResultController.class).getAll()).withRel("script-results");
        scriptResultDto.add(selfLink, linkToAll);
        return scriptResultDto;
    }

    private ScriptResultDto convertToDto(ScriptResult scriptResult) {
        return new ScriptResultDto(scriptResult);
    }
}
