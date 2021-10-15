package io.metadew.iesi.server.rest.script.dto.action;

import io.metadew.iesi.metadata.definition.action.Action;
import io.metadew.iesi.metadata.definition.action.ActionParameter;
import io.metadew.iesi.metadata.definition.action.key.ActionKey;
import io.metadew.iesi.metadata.definition.script.key.ScriptKey;
import io.metadew.iesi.metadata.tools.IdentifierTools;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;

@Service
@ConditionalOnWebApplication
public class ScriptActionDtoService implements IScriptActionDtoService {

    public Action convertToEntity(ActionDto actionDto, String scriptId, long version) {
        return new Action(new ActionKey(new ScriptKey(scriptId, version), IdentifierTools.getActionIdentifier(actionDto.getName())),
                actionDto.getNumber(), actionDto.getType(), actionDto.getName(), actionDto.getDescription(), actionDto.getComponent(),
                actionDto.getCondition(), actionDto.getIteration(), actionDto.isErrorExpected() ? "y" : "n",
                actionDto.isErrorStop() ? "y" : "n", Integer.toString(actionDto.getRetries()), actionDto.getParameters().stream()
                .map(parameter -> parameter.convertToEntity(scriptId, version, IdentifierTools.getActionIdentifier(actionDto.getName())))
                .collect(Collectors.toList()));
    }

    public ActionDto convertToDto(Action action) {
        return new ActionDto(action.getNumber(), action.getName(), action.getType(), action.getDescription(), action.getComponent(),
                action.getCondition(), action.getIteration(), action.getErrorExpected(), action.getErrorStop(), action.getRetries(),
                action.getParameters().stream().map(this::convertToDto).collect(Collectors.toSet()));
    }

    private ActionParameterDto convertToDto(ActionParameter actionParameter) {
        return new ActionParameterDto(actionParameter.getMetadataKey().getParameterName(), actionParameter.getValue());
    }

}
