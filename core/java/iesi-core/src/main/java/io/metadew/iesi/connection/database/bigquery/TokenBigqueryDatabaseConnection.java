package io.metadew.iesi.connection.database.bigquery;

import io.metadew.iesi.connection.database.bigquery.BigqueryDatabaseConnection;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class TokenBigqueryDatabaseConnection extends BigqueryDatabaseConnection {

    public TokenBigqueryDatabaseConnection(String hostName, int portNumber, String project, String accessToken, String refreshToken, String clientId, String clientSecret) {
        super(getConnectionUrl(hostName, portNumber, project,accessToken, refreshToken, clientId, clientSecret));
    }

    private static String getConnectionUrl(String hostName, int portNumber, String project, String accessToken, String refreshToken, String clientId, String clientSecret) {
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
        connectionUrl.append("2");
        if (!accessToken.isEmpty()) {
            connectionUrl.append(";OAuthAccessToken=");
            connectionUrl.append(accessToken);
        }
        if (!refreshToken.isEmpty()) {
            connectionUrl.append(";OAuthRefreshToken=");
            connectionUrl.append(refreshToken);
        }
        if (!clientId.isEmpty()) {
            connectionUrl.append(";OAuthClientId=");
            connectionUrl.append(clientId);
        }
        if (!clientSecret.isEmpty()) {
            connectionUrl.append(";OAuthClientSecret=");
            connectionUrl.append(clientSecret);
        }
        connectionUrl.append(";");

        return connectionUrl.toString();
    }


}

