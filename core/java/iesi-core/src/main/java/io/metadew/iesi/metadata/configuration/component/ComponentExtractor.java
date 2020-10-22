package io.metadew.iesi.metadata.configuration.component;

import io.metadew.iesi.metadata.definition.component.Component;
import io.metadew.iesi.metadata.definition.component.key.ComponentKey;
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
        List<Component> components = new ArrayList<>();
        while (rs.next()) {
            String name = rs.getString("COMP_ID");
            component = componentMap.get(name);
            if (component == null) {
                component = mapRow(rs);
                componentMap.put(name, component);
            }
            component = mapRow(rs);
            components.add(component);
        }
        return components;
    }

    private Component mapRow(ResultSet rs) throws SQLException {
        ComponentKey componentKey = ComponentKey.builder().id(rs.getString("COMP_ID")).build();
        return Component.builder().componentKey(componentKey)
                .type(rs.getString("COMP_TYP_NM"))
                .name(rs.getString("COMP_NM"))
                .description(rs.getString("COMP_DSC"))
                .build();
    }
}
