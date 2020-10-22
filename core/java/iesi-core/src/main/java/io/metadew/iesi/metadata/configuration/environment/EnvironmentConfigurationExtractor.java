package io.metadew.iesi.metadata.configuration.environment;

import io.metadew.iesi.metadata.definition.environment.Environment;
import io.metadew.iesi.metadata.definition.environment.key.EnvironmentKey;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EnvironmentConfigurationExtractor implements ResultSetExtractor<List<Environment>> {

    @Override
    public List<Environment> extractData(ResultSet resultSet) throws SQLException, DataAccessException {
        Map<String, Environment> environmentMap = new HashMap<>();
        Environment environment;
        List<Environment> environments = new ArrayList<>();
        while (resultSet.next()) {
            String name = resultSet.getString("ENV_NM");
            environment = environmentMap.get(name);
            if (environment == null) {
                environment = mapRow(resultSet);
                environmentMap.put(name, environment);
            }
            environment = mapRow(resultSet);
            environments.add(environment);
        }
        return new ArrayList<>(environments);
    }

    private Environment mapRow(ResultSet rs) throws SQLException {
        EnvironmentKey environmentKey = EnvironmentKey.builder().name(rs.getString("ENV_NM")).build();
        return Environment.builder().environmentKey(
                EnvironmentKey.builder()
                        .name(rs.getString("ENV_NM"))
                        .build())
                .description(rs.getString("ENV_DSC"))
//                .parameters(null)
                .build();
    }
}