package io.metadew.iesi.connection.database.bigquery;

import io.metadew.iesi.connection.database.bigquery.BigqueryDatabaseConnection;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class DefaultBigqueryDatabaseConnection extends BigqueryDatabaseConnection {

    public DefaultBigqueryDatabaseConnection(String hostName, int portNumber, String project) {
        super(getConnectionUrl(hostName, portNumber, project));
    }

    private static String getConnectionUrl(String hostName, int portNumber, String project) {
        StringBuilder connectionUrl = new StringBuilder();
        connectionUrl.append("jdbc:bigquery://");
        connectionUrl.append(hostName);
        if (portNumber > 0) {
            connectionUrl.append(":");
            connectionUrl.append(portNumber);
        }
        connectionUrl.append(";ProjectId=");
        connectionUrl.append(project);
        connectionUrl.append(";OAuthType=");
        connectionUrl.append("3");
        connectionUrl.append(";");

        return connectionUrl.toString();
    }

}

