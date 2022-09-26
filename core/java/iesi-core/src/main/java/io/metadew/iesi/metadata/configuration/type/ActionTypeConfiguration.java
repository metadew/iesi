package io.metadew.iesi.metadata.configuration.type;

import io.metadew.iesi.common.configuration.metadata.actiontypes.MetadataActionTypesConfiguration;
import io.metadew.iesi.metadata.definition.action.type.ActionType;
import org.springframework.stereotype.Component;

@Component
public class ActionTypeConfiguration {

    private final MetadataActionTypesConfiguration metadataActionTypesConfiguration;

    public ActionTypeConfiguration(MetadataActionTypesConfiguration metadataActionTypesConfiguration) {
        this.metadataActionTypesConfiguration = metadataActionTypesConfiguration;
    }

    public ActionType getActionType(String actionTypeName) {
        return metadataActionTypesConfiguration.getActionType(actionTypeName)
                .orElseThrow(() -> new RuntimeException("action type " + actionTypeName + " not found"));
    }

}