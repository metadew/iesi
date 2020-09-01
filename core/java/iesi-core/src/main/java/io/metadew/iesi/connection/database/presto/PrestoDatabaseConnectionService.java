package io.metadew.iesi.connection.database.presto;

import io.metadew.iesi.connection.database.connection.ISchemaDatabaseConnectionService;
import io.metadew.iesi.connection.database.connection.SchemaDatabaseConnectionService;
import lombok.extern.log4j.Log4j2;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Log4j2
public class PrestoDatabaseConnectionService extends SchemaDatabaseConnectionService<PrestoDatabaseConnection> implements ISchemaDatabaseConnectionService<PrestoDatabaseConnection> {

    private static PrestoDatabaseConnectionService INSTANCE;

    public synchronized static PrestoDatabaseConnectionService getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new PrestoDatabaseConnectionService();
        }
        return INSTANCE;
    }

    private PrestoDatabaseConnectionService() {}
    
    @Override
    public String getDriver(PrestoDatabaseConnection databaseConnection) {
        return "io.prestosql.jdbc.PrestoDriver";
    }

    @Override
    public Class<PrestoDatabaseConnection> appliesTo() {
        return PrestoDatabaseConnection.class;
    }

    public String refactorLimitAndOffset(String query) {

        Pattern pattern = Pattern.compile("(?i)limit\\s+(?<limit>\\d+)\\s+(?i)offset\\s+(?<offset>\\d+)");
        Matcher matcher = pattern.matcher(query);

        StringBuilder stringBuilder = new StringBuilder(query);
        while (matcher.find()) {
            int limit = Integer.parseInt(matcher.group("limit"));
            int offset = Integer.parseInt(matcher.group("offset"));
            int matchStartIndex = matcher.start();
            int matchEndIndex = matcher.end();
            stringBuilder.replace(matchStartIndex, matchEndIndex, " OFFSET " + offset + " LIMIT " + limit + " ");
            matcher.reset(stringBuilder);
        }
        return stringBuilder.toString();
    }
}