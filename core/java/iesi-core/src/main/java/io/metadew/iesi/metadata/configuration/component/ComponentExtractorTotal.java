package io.metadew.iesi.metadata.configuration.component;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;

import java.sql.ResultSet;
import java.sql.SQLException;

public class ComponentExtractorTotal implements ResultSetExtractor<Integer> {
    @Override
    public Integer extractData(ResultSet rs) throws SQLException, DataAccessException {
        Integer total_environments = null;
        while (rs.next()) {
            total_environments = mapRow(rs);
        }
        return total_environments;
    }

    private Integer mapRow(ResultSet rs) throws SQLException {
        return rs.getInt("total_versions");
    }
}