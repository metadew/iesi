package io.metadew.iesi.server.rest.dataset.implementation;


import io.metadew.iesi.connection.tools.SQLTools;
import io.metadew.iesi.server.rest.dataset.implementation.inmemory.InMemoryDatasetImplementationDto;
import io.metadew.iesi.server.rest.dataset.implementation.inmemory.InMemoryDatasetImplementationKeyValueDto;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.extern.log4j.Log4j2;

import javax.sql.rowset.CachedRowSet;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

@Log4j2
public class DatasetImplementationDtoListResultSetExtractor {

    private static final String IN_MEMORY_DATASET_IMPLEMENTATION_TYPE = "in_memory";

    public List<DatasetImplementationDto> extractData(CachedRowSet rs) throws SQLException {
        Map<UUID, DatasetImplementationDtoBuilder> datasetBuilderMap = new LinkedHashMap<>();
        while (rs.next()) {
            UUID datasetImplementationId = UUID.fromString(rs.getString("dataset_impl_id"));
            DatasetImplementationDtoBuilder datasetImplementationBuilder = datasetBuilderMap.get(datasetImplementationId);
            if (datasetImplementationBuilder == null) {
                datasetImplementationBuilder = extractDatasetImplementationBuilderMapRow(rs);
                datasetBuilderMap.put(datasetImplementationId, datasetImplementationBuilder);
            }
            String type = mapType(rs);
            if (type.equalsIgnoreCase(IN_MEMORY_DATASET_IMPLEMENTATION_TYPE)) {
                mapInMemoryDatasetImplementationDto(rs, (InMemoryDatasetImplementationDtoBuilder) datasetImplementationBuilder);
            } else {
                log.warn("no type found for dataset implementation");
            }
            mapDatasetImplementationLabel(rs, datasetImplementationBuilder);
        }
        return datasetBuilderMap.values().stream()
                .map(DatasetImplementationDtoBuilder::build)
                .collect(Collectors.toList());
    }

    private void mapDatasetImplementationLabel(CachedRowSet rs, DatasetImplementationDtoBuilder datasetImplementationBuilder) throws SQLException {
        String datasetImplementationLabelId = rs.getString("dataset_impl_label_id");
        if (datasetImplementationLabelId != null && datasetImplementationBuilder.getDatasetImplementationLabels().get(UUID.fromString(datasetImplementationLabelId)) == null) {
            datasetImplementationBuilder.getDatasetImplementationLabels().put(
                    UUID.fromString(datasetImplementationLabelId),
                    new DatasetImplementationLabelDto(
                            UUID.fromString(datasetImplementationLabelId),
                            rs.getString("dataset_impl_label_value"))
            );
        }
    }

    private void mapInMemoryDatasetImplementationDto(CachedRowSet rs, InMemoryDatasetImplementationDtoBuilder datasetImplementationBuilder) throws SQLException {
        String inMemoryKeyValueId = rs.getString("dataset_in_mem_impl_kv_id");
        if (inMemoryKeyValueId != null && datasetImplementationBuilder.getKeyValues().get(UUID.fromString(inMemoryKeyValueId)) == null) {
            String key = SQLTools.getStringForSQL("dataset_in_mem_impl_kvs_key");
            String clobValue = SQLTools.getStringFromSQLClob(rs, "dataset_in_mem_impl_kvs_value");
            datasetImplementationBuilder.getKeyValues().put(UUID.fromString(inMemoryKeyValueId),
                    new InMemoryDatasetImplementationKeyValueDto(
                            UUID.fromString(inMemoryKeyValueId),
                            key,
                            clobValue)
            );
        }
    }

    private DatasetImplementationDtoBuilder extractDatasetImplementationBuilderMapRow(CachedRowSet rs) throws SQLException {
        String type = mapType(rs);
        if (type.equalsIgnoreCase(IN_MEMORY_DATASET_IMPLEMENTATION_TYPE)) {
            return extractInMemoryDatasetImplementation(rs);
        } else {
            throw new RuntimeException("cannot create dataset implementation for type " + type);
        }
    }

    private DatasetImplementationDtoBuilder extractInMemoryDatasetImplementation(CachedRowSet rs) throws SQLException {
        return new InMemoryDatasetImplementationDtoBuilder(
                UUID.fromString(rs.getString("dataset_impl_id")),
                new HashMap<>(),
                new HashMap<>()
        );
    }

    private String mapType(CachedRowSet rs) throws SQLException {
        // "dataset_in_mem_impls.ID as dataset_in_mem_impl_id, " +
        if (rs.getString("dataset_in_mem_impl_id") != null) {
            return IN_MEMORY_DATASET_IMPLEMENTATION_TYPE;
        } else {
            throw new RuntimeException("cannot determine the type of dataset_implementation");
        }
    }

    @AllArgsConstructor
    @Getter
    @EqualsAndHashCode
    @ToString
    public abstract static class DatasetImplementationDtoBuilder {
        private final UUID uuid;
        private final Map<UUID, DatasetImplementationLabelDto> datasetImplementationLabels;

        public abstract DatasetImplementationDto build();

    }

    @Getter
    @EqualsAndHashCode(callSuper = true)
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
