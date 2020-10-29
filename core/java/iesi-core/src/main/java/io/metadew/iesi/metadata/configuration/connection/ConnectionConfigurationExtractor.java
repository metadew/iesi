package io.metadew.iesi.metadata.configuration.connection;

import io.metadew.iesi.metadata.definition.connection.Connection;
import io.metadew.iesi.metadata.definition.connection.ConnectionParameter;
import io.metadew.iesi.metadata.definition.connection.key.ConnectionKey;
import io.metadew.iesi.metadata.definition.connection.key.ConnectionParameterKey;
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

public class ConnectionConfigurationExtractor implements ResultSetExtractor<List<Connection>> {
    @Override
    public List<Connection> extractData(ResultSet rs) throws SQLException, DataAccessException {
        Map<String, Connection> connectionMap = new HashMap<>();
        Connection connection;
        while (rs.next()) {
            String name = rs.getString("Connections_CONN_NM");
            connection = connectionMap.get(name);
            if (connection == null) {
                connection = mapRow(rs);
                connectionMap.put(name, connection);
            }
            addMapping(connection,rs);
        }
        return new ArrayList<>(connectionMap.values());
    }

    private Connection mapRow(ResultSet rs) throws SQLException {
        ConnectionKey connectionKey = ConnectionKey.builder().name(rs.getString("Connections_CONN_NM"))
                .environmentKey(new EnvironmentKey(rs.getString("ConnectionParameters_ENV_NM"))).build();
        return Connection.builder().connectionKey(connectionKey)
                .type(rs.getString("Connections_CONN_TYP_NM"))
                .description(rs.getString("Connections_CONN_DSC"))
                .parameters(new ArrayList<>())
                .build();
    }

    private void addMapping(Connection connection, ResultSet rs) throws SQLException {
        ConnectionKey connectionKey = ConnectionKey.builder().name(rs.getString("ConnectionParameters_CONN_NM")).environmentKey(new EnvironmentKey(rs.getString("ConnectionParameters_ENV_NM"))).build();
        ConnectionParameter connectionParameter =  ConnectionParameter.builder()
                .connectionParameterKey(ConnectionParameterKey.builder()
                        .connectionKey(connectionKey)
                        .parameterName(rs.getString("ConnectionParameters_CONN_PAR_NM"))
                        .build())
                .value(rs.getString("ConnectionParameters_CONN_PAR_VAL"))
                .build();
        connection.addParameters(connectionParameter);
    }
}
