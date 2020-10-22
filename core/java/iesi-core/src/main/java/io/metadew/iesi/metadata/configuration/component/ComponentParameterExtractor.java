package io.metadew.iesi.metadata.configuration.component;

import io.metadew.iesi.common.configuration.metadata.tables.MetadataTablesConfiguration;
import io.metadew.iesi.metadata.definition.component.ComponentParameter;
import io.metadew.iesi.metadata.definition.component.key.ComponentParameterKey;
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

public class ComponentParameterExtractor implements ResultSetExtractor<List<ComponentParameter>> {
    @Override
    public List<ComponentParameter> extractData(ResultSet rs) throws SQLException, DataAccessException {
        Map<String, ComponentParameter> componentParameterMap = new HashMap<>();
        ComponentParameter componentParameter;
        List<ComponentParameter> componentParameters = new ArrayList<>();
        while (rs.next()) {
            String name = rs.getString("COMP_ID");
            componentParameter = componentParameterMap.get(name);
            if (componentParameter == null) {
                componentParameter = mapRow(rs);
                componentParameterMap.put(name, componentParameter);
            }
            componentParameter = mapRow(rs);
            componentParameters.add(componentParameter);
        }
        return new ArrayList<>(componentParameters);
    }

    private ComponentParameter mapRow(ResultSet rs) throws SQLException {
        ComponentParameterKey componentParameterKey = ComponentParameterKey.builder()
                .componentKey(ComponentKey.builder().id(rs.getString("COMP_ID")).versionNumber(rs.getLong("COMP_VRS_NB")).build())
                .parameterName(rs.getString("COMP_PAR_NM")).build();
        return ComponentParameter.builder().componentParameterKey(componentParameterKey)
                .value(rs.getString("COMP_PAR_VAL")).build();
    }
}
