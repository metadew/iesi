package io.metadew.iesi.metadata.configuration.component;

import io.metadew.iesi.metadata.definition.component.ComponentVersion;
import io.metadew.iesi.metadata.definition.component.key.ComponentKey;
import io.metadew.iesi.metadata.definition.component.key.ComponentVersionKey;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ComponentVersionExtractor implements ResultSetExtractor<List<ComponentVersion>> {
    @Override
    public List<ComponentVersion> extractData(ResultSet rs) throws SQLException, DataAccessException {
        Map<String, ComponentVersion> componentVersionMap = new HashMap<>();
        ComponentVersion componentVersion;
        List<ComponentVersion> componentVersions = new ArrayList<>();
        while (rs.next()) {
            String name = rs.getString("COMP_ID");
            componentVersion = componentVersionMap.get(name);
            if (componentVersion == null) {
                componentVersion = mapRow(rs);
                componentVersionMap.put(name, componentVersion);
            }
            componentVersion = mapRow(rs);
            componentVersions.add(componentVersion);
        }
        return componentVersions;
    }

    private ComponentVersion mapRow(ResultSet rs) throws SQLException {
        ComponentVersionKey componentVersionKey = ComponentVersionKey.builder()
                .componentKey(ComponentKey.builder().id(rs.getString("COMP_ID")).versionNumber(rs.getLong("COMP_VRS_NB")).build())
                .build();
        return ComponentVersion.builder().componentVersionKey(componentVersionKey)
                .description(rs.getString("COMP_VRS_DSC")).build();
    }
}
