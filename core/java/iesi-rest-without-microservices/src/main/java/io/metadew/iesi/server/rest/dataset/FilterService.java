package io.metadew.iesi.server.rest.dataset;

import io.metadew.iesi.connection.tools.SQLTools;
import org.springframework.stereotype.Service;

@Service
public class FilterService {

    public String getStringCondition(String columnName, Filter filter) {
        return " " + columnName + " " +
                (filter.isExactMatch() ? "=" : "LIKE") + " " +
                SQLTools.getStringForSQL((
                        filter.isExactMatch() ? "" : "%") +
                        filter.getValue() +
                        (filter.isExactMatch() ? "" : "%"))
                + " ";
    }

    public String getLongCondition(String columnName, Filter filter) {
        return " " + columnName + " " +
                " = " + SQLTools.getStringForSQL(Long.parseLong(filter.getValue())) + " ";
    }

    public String getKeyValueCondition(String keyColumnName, String valueColumnName, Filter filter) {
        return " " + keyColumnName + " = " + SQLTools.getStringForSQL(filter.getValue().split(":")[0]) +
                " and " + valueColumnName + " "  +
                (filter.isExactMatch() ? "=" : "LIKE") + " " +
                SQLTools.getStringForSQL(
                        (filter.isExactMatch() ? "" : "%") +
                                filter.getValue().split(":")[1] +
                                (filter.isExactMatch() ? "" : "%")) +
                " ";

    }

}
