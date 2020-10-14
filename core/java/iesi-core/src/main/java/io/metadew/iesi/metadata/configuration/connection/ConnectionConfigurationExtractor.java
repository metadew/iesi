package io.metadew.iesi.metadata.configuration.connection;

import io.metadew.iesi.metadata.definition.connection.Connection;
import io.metadew.iesi.metadata.definition.connection.ConnectionParameter;
import io.metadew.iesi.metadata.definition.connection.key.ConnectionKey;
import io.metadew.iesi.metadata.definition.environment.key.EnvironmentKey;
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
        List<Connection> connectionList = new ArrayList<>();
        while (rs.next()) {
            String name = rs.getString("CONN_NM");
            connection = connectionMap.get(name);
            if (connection == null) {
                connection = mapRow(rs);
                connectionMap.put(name, connection);
            }
            connectionList.add(connection);
        }
        return new ArrayList<>(connectionList);
    }

    private Connection mapRow(ResultSet rs) throws SQLException {
        ConnectionKey connectionKey = ConnectionKey.builder().name(rs.getString("CONN_NM")).environmentKey(new EnvironmentKey(rs.getString("CONN_NM"))).build();
        List<ConnectionParameter> connectionParameters = ConnectionParameterConfiguration.getInstance().getByConnection(connectionKey);
        return Connection.builder().connectionKey(connectionKey)
                .description(rs.getString("CONN_DSC"))
                .parameters(connectionParameters)
                .build();
    }
}
