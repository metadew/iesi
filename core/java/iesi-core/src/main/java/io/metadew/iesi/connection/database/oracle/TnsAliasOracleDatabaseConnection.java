package io.metadew.iesi.connection.database.oracle;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class TnsAliasOracleDatabaseConnection extends OracleDatabaseConnection {

    public TnsAliasOracleDatabaseConnection(String hostName, int portNumber, String tnsAlias, String userName, String userPassword, String initSql) {
        super("jdbc:oracle:thin:@" + hostName + ":" + portNumber + ":" + tnsAlias, userName, userPassword, initSql);
    }

    public TnsAliasOracleDatabaseConnection(String hostName, int portNumber, String tnsAlias, String userName, String userPassword, String initSql, String schema) {
        super("jdbc:oracle:thin:@" + hostName + ":" + portNumber + ":" + tnsAlias, userName, userPassword, initSql, schema);
    }
}
