package io.metadew.iesi.metadata.configuration.component;

import io.metadew.iesi.metadata.definition.component.ComponentAttribute;
import io.metadew.iesi.metadata.definition.component.key.ComponentAttributeKey;
import io.metadew.iesi.metadata.definition.component.key.ComponentKey;
import io.metadew.iesi.metadata.definition.environment.key.EnvironmentKey;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ComponentAttributeExtractor implements ResultSetExtractor<List<ComponentAttribute>> {
    @Override
    public List<ComponentAttribute> extractData(ResultSet rs) throws SQLException, DataAccessException {
        Map<String, ComponentAttribute> componentAttributeMap = new HashMap<>();
        ComponentAttribute componentAttribute;
        List<ComponentAttribute> componentAttributes = new ArrayList<>();
        while (rs.next()) {
            String name = rs.getString("COMP_ID");
            componentAttribute = componentAttributeMap.get(name);
            if (componentAttribute == null) {
                componentAttribute = mapRow(rs);
                componentAttributeMap.put(name, componentAttribute);
            }
            componentAttribute = mapRow(rs);
            componentAttributes.add(componentAttribute);
        }
        return componentAttributes;
    }

    private ComponentAttribute mapRow(ResultSet rs) throws SQLException {
        ComponentAttributeKey componentAttributeKey = ComponentAttributeKey.builder()
                .componentKey(ComponentKey.builder().id(rs.getString("COMP_ID")).versionNumber(rs.getLong("COMP_VRS_NB")).build())
                .environmentKey(new EnvironmentKey(rs.getString("ENV_NM")))
                .componentAttributeName(rs.getString("COMP_ATT_NM")).build();
        return ComponentAttribute.builder().componentAttributeKey(componentAttributeKey)
                .value(rs.getString("COMP_ATT_VAL")).build();
    }
}
