package io.metadew.iesi.connection.database.bigquery;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class UserBigqueryDatabaseConnection extends BigqueryDatabaseConnection {

    public UserBigqueryDatabaseConnection(String hostName, int portNumber, String project) {
        super(getConnectionUrl(hostName, portNumber, project));
    }

    private static String getConnectionUrl(String hostName, Integer portNumber, String project) {
        StringBuilder connectionUrl = new StringBuilder();
        connectionUrl.append("jdbc:bigquery://");
        connectionUrl.append(hostName);
        connectionUrl.append(":");
        connectionUrl.append(portNumber);
        connectionUrl.append(";ProjectId=");
        connectionUrl.append(project);
        connectionUrl.append(";OAuthType=");
        connectionUrl.append("1");
        connectionUrl.append(";");

        return connectionUrl.toString();
    }

}

