package io.metadew.iesi.server.rest.script.result.dto;

import io.metadew.iesi.metadata.definition.script.result.ScriptResult;
import io.metadew.iesi.server.rest.script.result.ScriptResultController;
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

        // Add a link to itself
        Link selfLink = linkTo(methodOn(ScriptResultController.class).getByRunIdAndProcessId(scriptResultDto.getRunID(), scriptResultDto.getProcessId()))
                .withRel("script:" + scriptResultDto.getScriptName() + "-" + scriptResultDto.getScriptVersion());
        scriptResultDto.add(selfLink);

        return scriptResultDto;
    }

    private ScriptResultDto convertToDto(ScriptResult scriptResult){
        return new ScriptResultDto(scriptResult);
    }
}
