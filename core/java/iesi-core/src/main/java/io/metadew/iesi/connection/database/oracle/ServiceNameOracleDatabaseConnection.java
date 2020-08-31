package io.metadew.iesi.connection.database.oracle;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class ServiceNameOracleDatabaseConnection extends OracleDatabaseConnection {

    public ServiceNameOracleDatabaseConnection(String hostName, int portNumber, String serviceName, String userName, String userPassword, String initSql) {
        super("jdbc:oracle:thin:@//" + hostName + ":" + portNumber + "/" + serviceName, userName, userPassword, initSql);
    }

    public ServiceNameOracleDatabaseConnection(String hostName, int portNumber, String serviceName, String userName, String userPassword, String initSql, String schema) {
        super("jdbc:oracle:thin:@//" + hostName + ":" + portNumber + "/" + serviceName, userName, userPassword, initSql, schema);
    }
}

