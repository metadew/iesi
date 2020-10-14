package io.metadew.iesi.metadata.configuration.connection;

import io.metadew.iesi.metadata.definition.connection.ConnectionParameter;
import io.metadew.iesi.metadata.definition.connection.key.ConnectionKey;
import io.metadew.iesi.metadata.definition.connection.key.ConnectionParameterKey;
import io.metadew.iesi.metadata.definition.environment.key.EnvironmentKey;
import io.metadew.iesi.metadata.definition.template.Template;
import io.metadew.iesi.metadata.definition.template.TemplateKey;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class ConnectionParameterExtractor implements ResultSetExtractor<List<ConnectionParameter>> {
    @Override
    public List<ConnectionParameter> extractData(ResultSet rs) throws SQLException, DataAccessException {
        Map<String, ConnectionParameter> connectionParameterMap = new HashMap<>();
        ConnectionParameter connectionParameter;
        List<ConnectionParameter> connectionParameterList = new ArrayList<>();
        while (rs.next()) {
            String name = rs.getString("CONN_NM");
            connectionParameter = connectionParameterMap.get(name);
            if (connectionParameter == null) {
                connectionParameter = mapRow(rs);
                connectionParameterMap.put(name, connectionParameter);
            }
            connectionParameterList.add(connectionParameter);
        }
        return new ArrayList<>(connectionParameterList);
    }

    private ConnectionParameter mapRow(ResultSet rs) throws SQLException {
        ConnectionKey connectionKey = ConnectionKey.builder().name(rs.getString("CONN_NM")).environmentKey(new EnvironmentKey(rs.getString("ENV_NM"))).build();
        return ConnectionParameter.builder()
                .connectionParameterKey(ConnectionParameterKey.builder()
                        .connectionKey(connectionKey)
                        .parameterName(rs.getString("CONN_PAR_NM"))
                        .build())
                .value(rs.getString("CONN_PAR_VAL"))
                .build();
    }
}
