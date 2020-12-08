package io.metadew.iesi.connection.database.oracle;

import io.metadew.iesi.connection.database.connection.SchemaDatabaseConnection;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;


@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class OracleDatabaseConnection extends SchemaDatabaseConnection {

    private static String type = "oracle";

    public OracleDatabaseConnection(String connectionURL, String userName, String userPassword, String initSql) {
        super(type, connectionURL, userName, userPassword, initSql);
        System.getProperties().setProperty("oracle.jdbc.J2EE13Compliant", "true");
    }

    public OracleDatabaseConnection(String connectionURL, String userName, String userPassword, String initSql, String schema) {
        super(type, connectionURL, userName, userPassword, initSql, schema);
        System.getProperties().setProperty("oracle.jdbc.J2EE13Compliant", "true");
    }

    public static String getConnectionUrl(String hostName, int portNumber, String serviceName, String tnsAlias) {
        StringBuilder connectionUrl = new StringBuilder();
        connectionUrl.append("jdbc:oracle:thin:");
        if (!serviceName.isEmpty()) {
            connectionUrl.append(":@//");
            connectionUrl.append(hostName);

            if (portNumber > 0) {
                connectionUrl.append(":");
                connectionUrl.append(portNumber);
            }

            connectionUrl.append("/");
            connectionUrl.append(serviceName);
        } else if (!tnsAlias.isEmpty()) {
            connectionUrl.append(hostName);

            if (portNumber > 0) {
                connectionUrl.append(":");
                connectionUrl.append(portNumber);
            }

            connectionUrl.append(":");
            connectionUrl.append(tnsAlias);
        } else {
            throw new RuntimeException("Unable to build connection url");
        }

        return connectionUrl.toString();
    }

}
