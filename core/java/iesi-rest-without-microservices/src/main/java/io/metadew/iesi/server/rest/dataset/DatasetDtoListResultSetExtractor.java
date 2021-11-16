package io.metadew.iesi.server.rest.dataset;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;
import lombok.extern.log4j.Log4j2;

import javax.sql.rowset.CachedRowSet;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

@Log4j2
public class DatasetDtoListResultSetExtractor {

    public List<DatasetDto> extractData(CachedRowSet rs) throws SQLException {
        Map<UUID, DatasetDtoBuilder> datasetBuilderMap = new LinkedHashMap<>();
        DatasetDtoBuilder datasetDtoBuilder;
        while (rs.next()) {
            datasetDtoBuilder = datasetBuilderMap.get(UUID.fromString(rs.getString("dataset_id")));

            if (datasetDtoBuilder == null) {
                datasetDtoBuilder = mapDatasetDtoBuilder(rs);
                datasetBuilderMap.put(UUID.fromString(rs.getString("dataset_id")), datasetDtoBuilder);
            }
            addImplementationUUID(datasetDtoBuilder, rs);
        }
        return datasetBuilderMap.values().stream().map(DatasetDtoBuilder::build).collect(Collectors.toList());
    }


    private void addImplementationUUID(DatasetDtoBuilder datasetDtoBuilder, CachedRowSet rs) throws SQLException {
        if (rs.getString("dataset_impl_id") == null) {
            return;
        }
        UUID datasetImplementationId = UUID.fromString(rs.getString("dataset_impl_id"));
        datasetDtoBuilder.getDatasetImplementationBuilders().add(datasetImplementationId);

    }

    @AllArgsConstructor
    @Getter
    @ToString
    private static class DatasetDtoBuilder {
        private final UUID uuid;
        private final String name;
        private final Set<UUID> datasetImplementationBuilders;

        public DatasetDto build() {
            return new DatasetDto(uuid, name, datasetImplementationBuilders);
        }
    }


    private DatasetDtoBuilder mapDatasetDtoBuilder(CachedRowSet rs) throws SQLException {
        return new DatasetDtoBuilder(
                UUID.fromString(rs.getString("dataset_id")),
                rs.getString("dataset_name"),
                new HashSet<>()
        );
    }


}
