package io.metadew.iesi.server.rest.script.dto;


import io.metadew.iesi.metadata.definition.script.Script;
<<<<<<< HEAD
import io.metadew.iesi.server.rest.script.ScriptController;
import org.springframework.hateoas.Link;
=======
import io.metadew.iesi.server.rest.script.ScriptsController;
>>>>>>> master
import org.springframework.hateoas.server.mvc.RepresentationModelAssemblerSupport;
import org.springframework.stereotype.Component;

@Component
public class ScriptPostDtoModelAssembler extends RepresentationModelAssemblerSupport<Script, ScriptPostDto> {

    private IScriptPostDtoService iScriptPostDtoService;

    public ScriptPostDtoModelAssembler(IScriptPostDtoService iScriptPostDtoService) {
        super(ScriptsController.class, ScriptPostDto.class);
        this.iScriptPostDtoService = iScriptPostDtoService;
    }

    @Override
    public ScriptPostDto toModel(Script script) {
        ScriptPostDto scriptPostDto = iScriptPostDtoService.convertToDto(script);
        return scriptPostDto;
    }

}