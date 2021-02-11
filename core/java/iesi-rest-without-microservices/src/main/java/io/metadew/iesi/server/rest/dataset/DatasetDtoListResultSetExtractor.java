package io.metadew.iesi.server.rest.dataset;

import io.metadew.iesi.connection.tools.SQLTools;

import io.metadew.iesi.server.rest.dataset.implementation.DatasetImplementationDto;
import io.metadew.iesi.server.rest.dataset.implementation.DatasetImplementationLabelDto;
import io.metadew.iesi.server.rest.dataset.implementation.inmemory.InMemoryDatasetImplementationDto;
import io.metadew.iesi.server.rest.dataset.implementation.inmemory.InMemoryDatasetImplementationKeyValueDto;
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

    private static final String IN_MEMORY_DATASET_IMPLEMENTATION_TYPE = "in_memory";


    public List<DatasetDto> extractData(CachedRowSet rs) throws SQLException {
        Map<UUID,DatasetDtoBuilder> datasetBuilderMap = new LinkedHashMap<>();
        DatasetDtoBuilder datasetDtoBuilder;
        while (rs.next()) {
            datasetDtoBuilder = datasetBuilderMap.get(UUID.fromString(rs.getString("dataset_id")));

            if (datasetDtoBuilder == null) {
                datasetDtoBuilder = mapDatasetDtoBuilder(rs);
                datasetBuilderMap.put(UUID.fromString(rs.getString("dataset_id")),datasetDtoBuilder);
            }

            addImplementation(datasetDtoBuilder,rs);

        }
        return datasetBuilderMap.values().stream().map(DatasetDtoBuilder::build).collect(Collectors.toList());
    }

    private void addImplementation(DatasetDtoBuilder datasetDtoBuilder, CachedRowSet rs) throws SQLException {
        if (rs.getString("dataset_impl_id") == null) {
            return;
        }
        UUID datasetImplementationId = UUID.fromString(rs.getString("dataset_impl_id"));
        datasetDtoBuilder.getDatasetImplementationBuilders().add(datasetImplementationId);

    }

    private DatasetDtoBuilder mapDatasetDtoBuilder(CachedRowSet rs) throws SQLException {
        return new DatasetDtoBuilder(
                UUID.fromString(rs.getString("dataset_id")),
                rs.getString("dataset_name"),
                new HashSet<>()
        );
    }


    @AllArgsConstructor
    @Getter
    @ToString
    private static class DatasetDtoBuilder {
        private final UUID uuid;
        private final String name;
        private final Set<UUID> datasetImplementationBuilders;

        public DatasetDto build() {
            return new DatasetDto(uuid, name,datasetImplementationBuilders);
        }
    }


    @AllArgsConstructor
    @Getter
    @ToString
    public abstract static class DatasetImplementationDtoBuilder {
        private final UUID uuid;
        private final Map<UUID, DatasetImplementationLabelDto> datasetImplementationLabels;

        public abstract DatasetImplementationDto build();

    }

    @Getter
    @ToString(callSuper = true)
    private static class InMemoryDatasetImplementationDtoBuilder extends DatasetImplementationDtoBuilder {

        private final Map<UUID, InMemoryDatasetImplementationKeyValueDto> keyValues;

        public InMemoryDatasetImplementationDtoBuilder(UUID uuid, Map<UUID, DatasetImplementationLabelDto> datasetImplementationLabels, Map<UUID, InMemoryDatasetImplementationKeyValueDto> keyValues) {
            super(uuid, datasetImplementationLabels);
            this.keyValues = keyValues;
        }

        @Override
        public DatasetImplementationDto build() {
            return new InMemoryDatasetImplementationDto(
                    getUuid(),
                    new HashSet<>(getDatasetImplementationLabels().values()),
                    new HashSet<>(getKeyValues().values()));
        }
    }

}
