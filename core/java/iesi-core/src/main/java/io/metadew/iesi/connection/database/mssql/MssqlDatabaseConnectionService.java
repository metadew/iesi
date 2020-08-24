package io.metadew.iesi.connection.database.mssql;

import io.metadew.iesi.connection.database.connection.ISchemaDatabaseConnectionService;
import io.metadew.iesi.connection.database.connection.SchemaDatabaseConnectionService;
import lombok.extern.log4j.Log4j2;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Log4j2
public class MssqlDatabaseConnectionService extends SchemaDatabaseConnectionService<MssqlDatabaseConnection> implements ISchemaDatabaseConnectionService<MssqlDatabaseConnection> {

    private static MssqlDatabaseConnectionService INSTANCE;

    public synchronized static MssqlDatabaseConnectionService getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new MssqlDatabaseConnectionService();
        }
        return INSTANCE;
    }

    private MssqlDatabaseConnectionService() {
    }

    @Override
    public String getDriver(MssqlDatabaseConnection databaseConnection) {
        return "com.microsoft.sqlserver.jdbc.SQLServerDriver";
    }

    @Override
    public Class<MssqlDatabaseConnection> appliesTo() {
        return MssqlDatabaseConnection.class;
    }

    public String refactorLimitAndOffset(String query) {

        Pattern pattern = Pattern.compile("(?i)limit\\s+(?<limit>[0-9]*+)\\s+(?i)offset\\s+(?<offset>[0-9]*+)");
        Matcher matcher = pattern.matcher(query);

        StringBuilder stringBuilder = new StringBuilder(query);
        while (matcher.find()) {
            int limit = Integer.parseInt(matcher.group("limit"));
            int offset = Integer.parseInt(matcher.group("offset"));
            int matchStartIndex = matcher.start();
            int matchEndIndex = matcher.end();
            stringBuilder.replace(matchStartIndex, matchEndIndex, " OFFSET " + offset + " ROWS FETCH NEXT " + limit + " ROWS ONLY ");
            matcher.reset(stringBuilder);
        }
        return stringBuilder.toString();
    }

}