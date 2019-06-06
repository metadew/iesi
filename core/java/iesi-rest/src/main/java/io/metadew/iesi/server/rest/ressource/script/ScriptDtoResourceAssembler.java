//package io.metadew.iesi.server.rest.ressource.script;
//
//import io.metadew.iesi.metadata.definition.Script;
//import io.metadew.iesi.server.rest.controller.ScriptController;
//import org.springframework.stereotype.Component;
//import org.modelmapper.ModelMapper;
//import org.springframework.hateoas.Link;
//
//import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
//import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;
//@Component
//public class ScriptDtoResourceAssembler extends ResourceAssemblerSupport<Script, ScriptDto> {
//
//    private ModelMapper modelMapper;
//
//    public ScriptDtoResourceAssembler() {
//        super(ScriptController.class, ScriptDto.class);
//        this.modelMapper = new ModelMapper();
//    }
//
//    @Override
//    public ScriptDto toResource(Script script) {
//        ScriptDto scriptDto = convertToDto(script);
//        Link selfLink = linkTo(methodOn(ScriptController.class).getByNameandDescription(script.getName(), script.getDescription()))
//                .withSelfRel();
//        scriptDto.add(selfLink);
//        Link environmentLink = linkTo(methodOn(ScriptController.class).getByName(script.getDescription()))
//                .withRel("environment");
//        scriptDto.add(environmentLink);
//        return scriptDto;
//    }
//
//    private ScriptDto convertToDto(Script script) {
//        return modelMapper.map(script, ScriptDto.class);
//    }
//}
