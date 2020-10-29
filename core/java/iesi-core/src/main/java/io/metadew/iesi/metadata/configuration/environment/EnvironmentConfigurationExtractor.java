package io.metadew.iesi.metadata.configuration.environment;

import io.metadew.iesi.metadata.definition.environment.Environment;
import io.metadew.iesi.metadata.definition.environment.EnvironmentParameter;
import io.metadew.iesi.metadata.definition.environment.key.EnvironmentKey;
import io.metadew.iesi.metadata.definition.environment.key.EnvironmentParameterKey;
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
        while (resultSet.next()) {
            String name = resultSet.getString("Environments_ENV_NM");
            environment = environmentMap.get(name);
            if (environment == null) {
                environment = mapRow(resultSet);
                environmentMap.put(name, environment);
            }
            addMapping(environment, resultSet);
        }
        return new ArrayList<>(environmentMap.values());
    }

    private Environment mapRow(ResultSet rs) throws SQLException {

        EnvironmentKey environmentKey = EnvironmentKey.builder().name(rs.getString("Environments_ENV_NM")).build();
        return Environment.builder().environmentKey(
                environmentKey)
                .description(rs.getString("Environments_ENV_DSC"))
                .parameters(new ArrayList<>())
                .build();
    }

    private void addMapping(Environment environment, ResultSet rs) throws SQLException {
        EnvironmentKey environmentKey = EnvironmentKey.builder().name(rs.getString("Environments_ENV_NM")).build();
        EnvironmentParameter environmentParameter = EnvironmentParameter.builder()
                .environmentParameterKey(
                        EnvironmentParameterKey.builder()
                                .environmentKey(environmentKey)
                                .parameterName(rs.getString("EnvironmentParameters_ENV_PAR_NM"))
                                .build())
                .value(rs.getString("EnvironmentParameters_ENV_PAR_VAL")).build();
        environment.addParameters(environmentParameter);
    }
}