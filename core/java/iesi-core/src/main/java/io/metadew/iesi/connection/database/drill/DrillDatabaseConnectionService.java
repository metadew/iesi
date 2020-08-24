package io.metadew.iesi.connection.database.drill;

import io.metadew.iesi.connection.database.connection.ISchemaDatabaseConnectionService;
import io.metadew.iesi.connection.database.connection.SchemaDatabaseConnectionService;
import lombok.extern.log4j.Log4j2;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Log4j2
public class DrillDatabaseConnectionService extends SchemaDatabaseConnectionService<DrillDatabaseConnection> implements ISchemaDatabaseConnectionService<DrillDatabaseConnection> {

    private static DrillDatabaseConnectionService INSTANCE;

    public synchronized static DrillDatabaseConnectionService getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new DrillDatabaseConnectionService();
        }
        return INSTANCE;
    }

    private DrillDatabaseConnectionService() {}
    
    @Override
    public String getDriver(DrillDatabaseConnection databaseConnection) {
        return "org.apache.drill.jdbc.Driver";
    }

    @Override
    public Class<DrillDatabaseConnection> appliesTo() {
        return DrillDatabaseConnection.class;
    }

    public String refactorLimitAndOffset(String query) {

        Pattern pattern = Pattern.compile("(?i)limit\\s+(?<limit>[0-9]*+)\\s+(?i)offset\\s+(?<offset>[0-9]*+(?!\\s+(?i)rows))");
        Matcher matcher = pattern.matcher(query);

        StringBuilder stringBuilder = new StringBuilder(query);
        while (matcher.find()) {
            int matchEndIndex = matcher.end();
            stringBuilder.insert(matchEndIndex, " ROWS ");
            matcher.reset(stringBuilder);
        }
        matcher.reset(stringBuilder);
        return stringBuilder.toString();
    }
}