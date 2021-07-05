package io.metadew.iesi.server.rest.script.dto.action;

import io.metadew.iesi.metadata.definition.action.Action;
import io.metadew.iesi.metadata.definition.script.key.ScriptVersionKey;

public interface IScriptActionDtoService {

    public Action convertToEntity(ActionDto actionDto, ScriptVersionKey scriptVersionKey);

    public ActionDto convertToDto(Action action);

}
