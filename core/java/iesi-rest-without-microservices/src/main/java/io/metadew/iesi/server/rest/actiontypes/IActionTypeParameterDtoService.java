package io.metadew.iesi.server.rest.actiontypes;

import io.metadew.iesi.metadata.definition.action.type.ActionTypeParameter;

public interface IActionTypeParameterDtoService {

    public ActionTypeParameterDto convertToDto(ActionTypeParameter actionTypeParameter, String name);

}
