//package io.metadew.iesi.server.rest.ressource.script;
//
//
//import io.metadew.iesi.metadata.definition.Script;
//import io.metadew.iesi.server.rest.controller.ScriptController;
//import org.modelmapper.ModelMapper;
//import org.springframework.hateoas.mvc.ResourceAssemblerSupport;
//import org.springframework.stereotype.Component;
//
//import java.text.MessageFormat;
//import java.util.List;
//
//import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
//import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;
//
//@Component
//public class ScriptByNameDtoResourceAssembler extends ResourceAssemblerSupport<List<Script>, ScriptByNameDto> {
//
//    private final ModelMapper modelMapper;
//
//    public ScriptByNameDtoResourceAssembler() {
//        super(ScriptsController.class, ScriptByNameDto.class);
//        this.modelMapper = new ModelMapper();
//    }
//
//    @Override
//    public ScriptByNameDto toResource(List<Script> scripts) {
//        ScriptByNameDto scriptByNameDto = convertToDto(scripts);
//
//            scriptByNameDto.add(linkTo(methodOn(ScriptController.class).getByNameScript(scriptByNameDto.getName()))
//                    .withRel("script:"+scriptByNameDto.getName()));
//
//        return scriptByNameDto;
//    }
//
//    private ScriptByNameDto convertToDto(List<Script> scripts) {
//        if (scripts.isEmpty()) {
//            throw new IllegalArgumentException("Scripts have to be non empty");
//        }
//        if (!scripts.stream().allMatch(script -> script.getName().equals(scripts.get(0).getName()))) {
//            throw new IllegalArgumentException(MessageFormat.format("Scripts ''{0}'' do not define the same name ''{1}''", scripts, scripts.get(0).getName()));
//        }
//        if (!scripts.stream().allMatch(script -> script.getType().equals(scripts.get(0).getType()))) {
//            throw new IllegalArgumentException(MessageFormat.format("Scripts ''{0}'' do not define the same type ''{1}''", scripts, scripts.get(0).getName()));
//        }
//
//        ScriptByNameDto scriptByNameDto = modelMapper.map(scripts.get(0), ScriptByNameDto.class);
////        scriptByNameDto.setEnvironments(scripts.stream()
////                .map(Script::getEnvironment)
////                .collect(Collectors.toList()));
//        return scriptByNameDto;
//    }
//}
//
