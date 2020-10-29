package io.metadew.iesi.metadata.configuration.component;

import io.metadew.iesi.metadata.definition.component.Component;
import io.metadew.iesi.metadata.definition.component.ComponentAttribute;
import io.metadew.iesi.metadata.definition.component.ComponentParameter;
import io.metadew.iesi.metadata.definition.component.ComponentVersion;
import io.metadew.iesi.metadata.definition.component.key.ComponentAttributeKey;
import io.metadew.iesi.metadata.definition.component.key.ComponentKey;
import io.metadew.iesi.metadata.definition.component.key.ComponentParameterKey;
import io.metadew.iesi.metadata.definition.component.key.ComponentVersionKey;
import io.metadew.iesi.metadata.definition.environment.key.EnvironmentKey;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ComponentExtractor implements ResultSetExtractor<List<Component>> {
    @Override
    public List<Component> extractData(ResultSet rs) throws SQLException, DataAccessException {
        Map<String, Component> componentMap = new HashMap<>();
        Component component;
        while (rs.next()) {
            String name = rs.getString("Components_COMP_ID");
            component = componentMap.get(name);
            if (component == null) {
                component = mapRow(rs);
                componentMap.put(name, component);
            }
            addMapping(component, rs);
        }
        return new ArrayList<>(componentMap.values());
    }

    private Component mapRow(ResultSet rs) throws SQLException {
        ComponentKey componentKey = ComponentKey.builder().id(rs.getString("Components_COMP_ID")).versionNumber(rs.getLong("ComponentAttributes_COMP_VRS_NB")).build();
        return Component.builder().componentKey(componentKey)
                .type(rs.getString("Components_COMP_TYP_NM"))
                .name(rs.getString("Components_COMP_NM"))
                .description(rs.getString("Components_COMP_DSC"))
                .version(ComponentVersion.builder()
                        .componentVersionKey(ComponentVersionKey.builder()
                                .componentKey(ComponentKey.builder().id(rs.getString("ComponentVersions_COMP_ID")).versionNumber(rs.getLong("ComponentVersions_COMP_VRS_NB")).build())
                                .build())
                        .description(rs.getString("ComponentVersions_COMP_VRS_DSC")).build())
                .parameters(new ArrayList<>())
                .attributes(new ArrayList<>())
                .build();
    }

    private void addMapping(Component component, ResultSet rs) throws SQLException {
        ComponentAttributeKey componentAttributeKey = ComponentAttributeKey.builder()
                .componentKey(ComponentKey.builder().id(rs.getString("ComponentAttributes_COMP_ID")).versionNumber(rs.getLong("ComponentAttributes_COMP_VRS_NB")).build())
                .environmentKey(new EnvironmentKey(rs.getString("ComponentAttributes_ENV_NM")))
                .componentAttributeName(rs.getString("ComponentAttributes_COMP_ATT_NM")).build();
        ComponentAttribute componentAttribute = ComponentAttribute.builder().componentAttributeKey(componentAttributeKey)
                .value(rs.getString("ComponentAttributes_COMP_ATT_VAL")).build();
        component.addAttributes(componentAttribute);

        ComponentParameterKey componentParameterKey = ComponentParameterKey.builder()
                .componentKey(ComponentKey.builder().id(rs.getString("ComponentParameters_COMP_ID")).versionNumber(rs.getLong("ComponentParameters_COMP_VRS_NB")).build())
                .parameterName(rs.getString("ComponentParameters_COMP_PAR_NM")).build();
        ComponentParameter componentParameter = ComponentParameter.builder().componentParameterKey(componentParameterKey)
                .value(rs.getString("ComponentParameters_COMP_PAR_VAL")).build();
        component.addParameters(componentParameter);
    }
}
