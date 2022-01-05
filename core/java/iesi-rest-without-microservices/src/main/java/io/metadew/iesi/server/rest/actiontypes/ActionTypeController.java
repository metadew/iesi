package io.metadew.iesi.server.rest.actiontypes;

import io.metadew.iesi.common.configuration.metadata.actiontypes.MetadataActionTypesConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/action-types")
@ConditionalOnWebApplication
public class ActionTypeController {

    private IActionTypeDtoService actionTypeDtoService;

    @Autowired
    ActionTypeController(IActionTypeDtoService actionTypeDtoService) {
        this.actionTypeDtoService = actionTypeDtoService;
    }

    @GetMapping("")
    public List<ActionTypeDto> getAll() {
        return MetadataActionTypesConfiguration.getInstance().getActionTypes()
                .entrySet()
                .stream()
                .map(entry -> actionTypeDtoService.convertToDto(entry.getValue(), entry.getKey()))
                .collect(Collectors.toList());
    }

    @GetMapping("/{name}")
    public ActionTypeDto getByName(@PathVariable String name) {
        return MetadataActionTypesConfiguration.getInstance().getActionType(name)
                .map(actionType -> actionTypeDtoService.convertToDto(actionType, name))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Could not find action type " + name));
    }
}