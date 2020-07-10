package io.metadew.iesi.metadata.configuration.type;

import io.metadew.iesi.common.configuration.metadata.actiontypes.MetadataActionTypesConfiguration;
import io.metadew.iesi.metadata.definition.action.type.ActionType;
import io.metadew.iesi.metadata.operation.TypeConfigurationOperation;

public class ActionTypeConfiguration {

    private String dataObjectType = "ActionType";

    private static ActionTypeConfiguration INSTANCE;

    public synchronized static ActionTypeConfiguration getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new ActionTypeConfiguration();
        }
        return INSTANCE;
    }

    private ActionTypeConfiguration() {

    }
    public ActionType getActionType(String actionTypeName) {
        return MetadataActionTypesConfiguration.getInstance().getActionType(actionTypeName)
                .orElseThrow(() -> new RuntimeException("action type " + actionTypeName + " not found"));
    }

}