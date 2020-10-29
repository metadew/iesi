package io.metadew.iesi.metadata.configuration.script;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;

import java.sql.ResultSet;
import java.sql.SQLException;

public class ScriptConfigurationExtractorTotal implements ResultSetExtractor<Long> {
    @Override
    public Long extractData(ResultSet rs) throws SQLException, DataAccessException {
        Long maxVersionNumber = null;
        while (rs.next()) {
            maxVersionNumber = mapRow(rs);
        }
        return maxVersionNumber;
    }

    private Long mapRow(ResultSet rs) throws SQLException {
        return rs.getLong("total_versions");
    }
}
