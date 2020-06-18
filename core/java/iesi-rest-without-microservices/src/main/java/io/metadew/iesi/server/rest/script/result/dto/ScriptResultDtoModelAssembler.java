package io.metadew.iesi.server.rest.script.result.dto;

import io.metadew.iesi.server.rest.script.result.ScriptResultController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.server.mvc.RepresentationModelAssemblerSupport;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class ScriptResultDtoModelAssembler extends RepresentationModelAssemblerSupport<ScriptResultDto, ScriptResultDto> {

    @Autowired
    public ScriptResultDtoModelAssembler() {
        super(ScriptResultController.class, ScriptResultDto.class);
    }

    @Override
    public ScriptResultDto toModel(ScriptResultDto scriptResultDto) {
        // Add a link to itself
        Link selfLink = linkTo(methodOn(ScriptResultController.class).getByRunIdAndProcessId(scriptResultDto.getRunID(), scriptResultDto.getProcessId()))
                .withRel("script:" + scriptResultDto.getScriptName() + "-" + scriptResultDto.getScriptVersion())
                .withRel("runId:" + scriptResultDto.getRunID())
                .withRel("processID:" + scriptResultDto.getProcessId());
        scriptResultDto.add(selfLink);

        // add a link to the runId endPoint
        Link processIdLink = linkTo(methodOn(ScriptResultController.class).getByRunId(scriptResultDto.getRunID()))
                .withRel("scriptResult for this runID");
        scriptResultDto.add(processIdLink);

        return scriptResultDto;
    }
}
