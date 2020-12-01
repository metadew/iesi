//package io.metadew.iesi.datatypes.dataset.implementation;
//
//import io.metadew.iesi.connection.tools.SQLTools;
//import io.metadew.iesi.datatypes.dataset.DatasetKey;
//import io.metadew.iesi.datatypes.dataset.implementation.inmemory.InMemoryDatasetImplementation;
//import io.metadew.iesi.datatypes.dataset.implementation.inmemory.InMemoryDatasetImplementationKeyValue;
//import io.metadew.iesi.datatypes.dataset.implementation.inmemory.InMemoryDatasetImplementationKeyValueKey;
//import io.metadew.iesi.datatypes.dataset.implementation.label.DatasetImplementationLabel;
//import io.metadew.iesi.datatypes.dataset.implementation.label.DatasetImplementationLabelKey;
//import lombok.AllArgsConstructor;
//import lombok.Getter;
//import lombok.ToString;
//import lombok.extern.log4j.Log4j2;
//import org.springframework.dao.DataAccessException;
//import org.springframework.jdbc.core.ResultSetExtractor;
//
//import java.sql.ResultSet;
//import java.sql.SQLException;
//import java.util.*;
//import java.util.stream.Collectors;
//
//@Log4j2
//public class DatasetImplementationListResultSetExtractor implements ResultSetExtractor<List<DatasetImplementation>> {
//
//    @Override
//    public List<DatasetImplementation> extractData(ResultSet resultSet) throws SQLException, DataAccessException {
//        Map<String, DatasetImplementationBuilder> datasetImplementationBuilderMap = new LinkedHashMap<>();
//        while (resultSet.next()) {
//            mapRow(resultSet, datasetImplementationBuilderMap);
//        }
//        return datasetImplementationBuilderMap.values().stream()
//                .map(DatasetImplementationBuilder::build)
//                .collect(Collectors.toList());
//    }
//    public void mapRow(ResultSet resultSet, Map<String, DatasetImplementationBuilder> datasetImplementationBuilderMap) throws SQLException {
//        String datasetImplementationId = resultSet.getString("dataset_impl_id");
//        DatasetImplementationBuilder datasetImplementationBuilder = datasetImplementationBuilderMap.get(datasetImplementationId);
//        if (datasetImplementationBuilder == null) {
//            datasetImplementationBuilder = extractDatasetImplementationBuilderMapRow(resultSet);
//            datasetImplementationBuilderMap.put(datasetImplementationId, datasetImplementationBuilder);
//        }
//        String type = mapType(resultSet);
//        if (type.equalsIgnoreCase("in_memory")) {
//            mapInMemoryDatasetImplementation(resultSet, (InMemoryDatasetImplementationBuilder) datasetImplementationBuilder);
//        } else {
//            log.warn("no type found for dataset implementation");
//        }
//        mapDatasetImplementationLabel(resultSet, datasetImplementationBuilder);
//    }
//
//    private void mapInMemoryDatasetImplementation(ResultSet resultSet, InMemoryDatasetImplementationBuilder inMemoryDatasetImplementationBuilder) throws SQLException {
//        String inMemoryKeyValueId = resultSet.getString("dataset_in_mem_impl_kv_id");
//        if (inMemoryKeyValueId != null && inMemoryDatasetImplementationBuilder.getKeyValues().get(inMemoryKeyValueId) == null) {
//            inMemoryDatasetImplementationBuilder.getKeyValues().put(inMemoryKeyValueId,
//                    new InMemoryDatasetImplementationKeyValue(
//                            new InMemoryDatasetImplementationKeyValueKey(UUID.fromString(inMemoryKeyValueId)),
//                            new DatasetImplementationKey(UUID.fromString(resultSet.getString("dataset_in_mem_impl_kv_impl_id"))),
//                            resultSet.getString("dataset_in_mem_impl_kvs_key"),
//                            SQLTools.getStringFromSQLClob(resultSet.getClob("dataset_in_mem_impl_kvs_value")))
//            );
//        }
//    }
//
//    private DatasetImplementationBuilder extractDatasetImplementationBuilderMapRow(ResultSet resultSet) throws SQLException {
//        // "dataset_impls.ID as dataset_impl_id, dataset_impls.DATASET_ID as dataset_impl_dataset_id, " +
//        String type = mapType(resultSet);
//        if (type.equalsIgnoreCase("in_memory")) {
//            return extractInMemoryDatasetImplementation(resultSet);
//        } else {
//            throw new RuntimeException("cannot create dataset implementation for type " + type);
//        }
//    }
//
//    private String mapType(ResultSet resultSet) throws SQLException {
//        // "dataset_in_mem_impls.ID as dataset_in_mem_impl_id, " +
//        if (resultSet.getString("dataset_in_mem_impl_id") != null) {
//            return "in_memory";
//        } else {
//            throw new RuntimeException("cannot determine the type of dataset_implementation");
//        }
//    }
//
//    private void mapDatasetImplementationLabel(ResultSet resultSet, DatasetImplementationBuilder datasetImplementationBuilder) throws SQLException {
//        // "dataset_impl_labels.ID as dataset_impl_label_id, dataset_impl_labels.VALUE as dataset_impl_label_value, "
//        String datasetImplementationLabelId = resultSet.getString("dataset_impl_label_id");
//        if (datasetImplementationLabelId != null && datasetImplementationBuilder.getDatasetImplementationLabels().get(datasetImplementationLabelId) == null) {
//            datasetImplementationBuilder.getDatasetImplementationLabels().put(
//                    datasetImplementationLabelId,
//                    new DatasetImplementationLabel(
//                            new DatasetImplementationLabelKey(UUID.fromString(datasetImplementationLabelId)),
//                            new DatasetImplementationKey(UUID.fromString(resultSet.getString("dataset_impl_label_impl_id"))),
//                            resultSet.getString("dataset_impl_label_value"))
//            );
//        }
//    }
//
//    private DatasetImplementationBuilder extractInMemoryDatasetImplementation(ResultSet resultSet) throws SQLException {
//        // "dataset_impls.ID as dataset_impl_id, dataset_impls.DATASET_ID as dataset_impl_dataset_id, " +
//        // "dataset_in_mem_impls.ID as dataset_in_mem_impl_id, dataset_impls.DATASET_ID as dataset_impl_dataset_id, " +
//        // "dataset_in_mem_impl_kvs.ID as dataset_in_mem_impl_kv_id, dataset_in_mem_impl_kvs.KEY as dataset_in_mem_impl_kvs_key, dataset_in_mem_impl_kvs.VALUE as dataset_in_mem_impl_kvs_value " +
//        return new InMemoryDatasetImplementationBuilder(
//                new DatasetImplementationKey(UUID.fromString(resultSet.getString("dataset_impl_id"))),
//                new DatasetKey(UUID.fromString(resultSet.getString("dataset_id"))),
//                resultSet.getString("dataset_name"),
//                new HashMap<>(),
//                new HashMap<>()
//        );
//    }
//
//
//    @AllArgsConstructor
//    @Getter
//    @ToString
//    public static abstract class DatasetImplementationBuilder {
//        private DatasetImplementationKey datasetImplementationKey;
//        private DatasetKey datasetKey;
//        private String name;
//        private Map<String, DatasetImplementationLabel> datasetImplementationLabels;
//
//        public abstract DatasetImplementation build();
//
//    }
//
//
//    @Getter
//    @ToString(callSuper = true)
//    private static class InMemoryDatasetImplementationBuilder extends DatasetImplementationBuilder {
//
//        private Map<String, InMemoryDatasetImplementationKeyValue> keyValues;
//
//        public InMemoryDatasetImplementationBuilder(DatasetImplementationKey datasetImplementationKey, DatasetKey datasetKey, String name, Map<String, DatasetImplementationLabel> executionRequestLabels, Map<String, InMemoryDatasetImplementationKeyValue> keyValues) {
//            super(datasetImplementationKey, datasetKey, name, executionRequestLabels);
//            this.keyValues = keyValues;
//        }
//
//        @Override
//        public DatasetImplementation build() {
//            return new InMemoryDatasetImplementation(
//                    getDatasetImplementationKey(),
//                    getDatasetKey(),
//                    getName(),
//                    new HashSet<>(getDatasetImplementationLabels().values()),
//                    new HashSet<>(getKeyValues().values()));
//        }
//    }
//}
