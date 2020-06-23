package io.metadew.iesi.server.rest.actiontypes;

import io.metadew.iesi.metadata.definition.action.type.ActionType;

public interface IActionTypeDtoService {

    public ActionTypeDto convertToDto(ActionType actionType, String name);

}
