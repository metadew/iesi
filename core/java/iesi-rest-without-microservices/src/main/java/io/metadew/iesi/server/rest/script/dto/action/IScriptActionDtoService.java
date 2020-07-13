package io.metadew.iesi.server.rest.script.dto.action;

import io.metadew.iesi.metadata.definition.action.Action;

public interface IScriptActionDtoService {

    public Action convertToEntity(ActionDto actionDto, String scriptId, long version);

    public ActionDto convertToDto(Action action);

}
