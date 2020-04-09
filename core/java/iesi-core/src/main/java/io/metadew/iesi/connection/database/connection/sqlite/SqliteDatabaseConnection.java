package io.metadew.iesi.connection.database.connection.sqlite;

import com.zaxxer.hikari.HikariConfig;
import io.metadew.iesi.connection.database.connection.DatabaseConnection;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Connection object for SQLite databases. This class extends the default database connection object.
 *
 * @author peter.billen
 */
public class SqliteDatabaseConnection extends DatabaseConnection {

    private final Pattern caseWhenPattern = Pattern.compile("WHEN\\s(?<condition>[\\s_\\w\\.]+?)\\sTHEN\\s(?<result>[\\w\\.]+)\\s");
    private final Pattern conditionPattern = Pattern.compile("(?<left>[\\w\\._]+)\\s(?<right>.+)");

    private static String type = "sqlite";

    public SqliteDatabaseConnection(String connectionURL, String userName, String userPassword) {
        super(type, connectionURL, userName, userPassword, null);
    }

    public SqliteDatabaseConnection(String fileName) {
        this(getConnectionUrl(fileName), "", "");
    }

    public static String getConnectionUrl(String fileName) {
        return "jdbc:sqlite:" + fileName;
    }

    public HikariConfig configure(HikariConfig hikariConfig) {
        super.configure(hikariConfig);
        hikariConfig.setConnectionTestQuery("select 1");
        hikariConfig.setDriverClassName(getDriver());
        return hikariConfig;
    }


    @Override
    public String getDriver() {
        return "org.sqlite.JDBC";
    }

    public String prepareQuery(String query) {
        // TODO: Move to Spring to get rid of these problems. CASE in CASE is not resolved at the moment
        // CASE with sqlite does not work
        // query = super.prepareQuery(query);

        int caseIndex = query.indexOf(" CASE ");
        if (caseIndex > 0) {
            // FOUND CASE STATEMENT
            int caseEndIndex = query.indexOf(" END ", caseIndex);
            if (caseEndIndex > 0) {
                String caseReplacement = replaceCaseStatement(query.substring(caseIndex, caseEndIndex));
                query = query.substring(0, caseIndex) + caseReplacement + query.substring(caseEndIndex + 5);
                System.out.println(query);
            }
        }

        return query;
    }

    private String replaceCaseStatement(String caseStatement) {
        System.out.println(caseStatement);
        int caseIndex = caseStatement.indexOf("CASE ");
        Matcher matcher = caseWhenPattern.matcher(caseStatement);
        boolean first = true;
        int endIndex = 0;
        while (matcher.find(endIndex)) {
            System.out.println("match:" + matcher.group("condition"));
            Matcher conditionMatcher = conditionPattern.matcher(matcher.group("condition"));
            if (!conditionMatcher.find()) {
                endIndex = matcher.end();
                continue;
            }

            System.out.println("match2: " + conditionMatcher.group("left") + " " +conditionMatcher.group("right"));
            int startIndex = matcher.start();
            endIndex = matcher.end();

            if (first) {
                first = false;
                caseStatement = caseStatement.substring(0, caseIndex + 5) + conditionMatcher.group("left") + " " + caseStatement.substring(caseIndex + 5);
                System.out.println(caseStatement);
                String newWhenStatement = "WHEN " + conditionMatcher.group("right") + " THEN " + matcher.group("result") + " ";
                caseStatement = caseStatement.substring(0, startIndex + conditionMatcher.group("left").length() + 1) + newWhenStatement + caseStatement.substring(endIndex + conditionMatcher.group("left").length() + 1);
                endIndex = startIndex + newWhenStatement.length() + conditionMatcher.group("left").length();
            } else {
                String newWhenStatement = "WHEN " + conditionMatcher.group("right") + " THEN " + matcher.group("result") + " ";
                caseStatement = caseStatement.substring(0, startIndex) + newWhenStatement + caseStatement.substring(endIndex);
                endIndex = startIndex + newWhenStatement.length();
            }
            System.out.println(caseStatement);
            matcher = caseWhenPattern.matcher(caseStatement);
        }
        System.out.println(caseStatement);
        return caseStatement;
    }
}
