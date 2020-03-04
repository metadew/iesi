package io.metadew.iesi.connection.database.connection.oracle;

public class ServiceNameOracleDatabaseConnection extends OracleDatabaseConnection {

    public ServiceNameOracleDatabaseConnection(String hostName, int portNumber, String serviceName, String userName, String userPassword) {
        super("jdbc:oracle:thin:@//" + hostName + ":" + portNumber + "/" + serviceName, userName, userPassword);
    }

    public ServiceNameOracleDatabaseConnection(String hostName, int portNumber, String serviceName, String userName, String userPassword, String schema) {
        super("jdbc:oracle:thin:@//" + hostName + ":" + portNumber + "/" + serviceName, userName, userPassword, schema);
    }
}
