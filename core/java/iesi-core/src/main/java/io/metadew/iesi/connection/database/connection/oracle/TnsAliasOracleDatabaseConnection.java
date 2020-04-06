package io.metadew.iesi.connection.database.connection.oracle;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class TnsAliasOracleDatabaseConnection extends OracleDatabaseConnection {

    public TnsAliasOracleDatabaseConnection(String hostName, int portNumber, String tnsAlias, String userName, String userPassword) {
        super("jdbc:oracle:thin:@" + hostName + ":" + portNumber + ":" + tnsAlias, userName, userPassword);
    }

    public TnsAliasOracleDatabaseConnection(String hostName, int portNumber, String tnsAlias, String userName, String userPassword, String schema) {
        super("jdbc:oracle:thin:@" + hostName + ":" + portNumber + ":" + tnsAlias, userName, userPassword, schema);
    }
}
