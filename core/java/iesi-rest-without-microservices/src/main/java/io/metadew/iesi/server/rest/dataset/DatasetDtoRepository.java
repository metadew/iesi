package io.metadew.iesi.server.rest.dataset;

import io.metadew.iesi.common.configuration.metadata.repository.MetadataRepositoryConfiguration;
import io.metadew.iesi.common.configuration.metadata.tables.MetadataTablesConfiguration;
import io.metadew.iesi.connection.tools.SQLTools;
import io.metadew.iesi.datatypes.dataset.Dataset;
import io.metadew.iesi.datatypes.dataset.DatasetConfiguration;
import io.metadew.iesi.datatypes.dataset.DatasetKey;
import io.metadew.iesi.server.rest.helper.PaginatedRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import javax.sql.rowset.CachedRowSet;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.*;
import java.util.stream.Collectors;

@Repository
public class DatasetDtoRepository extends PaginatedRepository implements IDatasetDtoRepository {


    private final MetadataRepositoryConfiguration metadataRepositoryConfiguration;
    private final FilterService filterService;

    @Autowired
    public DatasetDtoRepository(MetadataRepositoryConfiguration metadataRepositoryConfiguration, FilterService filterService) {
        this.metadataRepositoryConfiguration = metadataRepositoryConfiguration;
        this.filterService = filterService;
    }

    public Page<DatasetDto> fetchAll(Pageable pageable, Set<DatasetFilter> datasetFilters) {
        try {
            CachedRowSet cachedRowSet = metadataRepositoryConfiguration.getControlMetadataRepository().executeQuery(
                    getFetchAllQuery(pageable, datasetFilters),
                    "reader");
            return new PageImpl<>(new DatasetDtoListResultSetExtractor().extractData(cachedRowSet),
                    pageable,
                    getRowSize(datasetFilters));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public Optional<Dataset> get(DatasetKey metadataKey) {
        try {
            CachedRowSet cachedRowSet = metadataRepositoryConfiguration.getControlMetadataRepository().executeQuery(
                    MessageFormat.format(getFetchByIdQuery(),
                            SQLTools.getStringForSQL(metadataKey.getUuid())), "reader");

            while(cachedRowSet.next()) {
                this.mapRow(cachedRowSet, datasetBuilderMap);
            }

            return datasetBuilderMap.values().stream().findFirst().map(DatasetConfiguration.DatasetBuilder::build);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private String getFetchAllQuery(Pageable pageable, Set<DatasetFilter> datasetFilters) {
        return "SELECT " +
                "dataset_impls.ID as dataset_impl_id, " +
                "datasets.NAME as dataset_name, datasets.ID as dataset_id " +
                "from (" + getBaseQuery(pageable, datasetFilters) + ") base_datasets " + //base table
                "inner join " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("Datasets").getName() + " datasets " +
                "on base_datasets.ID=datasets.ID " +
                "left outer join " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("DatasetImplementations").getName() + " dataset_impls " +
                "on dataset_impls.DATASET_ID=datasets.ID ;";
    }

    private String getFetchByIdQuery() {
        return "SELECT " +
                "dataset_impls.ID as dataset_impl_id, " +
                "datasets.NAME as dataset_name, datasets.ID as dataset_id, " +
                "dataset_impl_labels.ID as dataset_impl_label_id, dataset_impl_labels.DATASET_IMPL_ID as dataset_impl_label_impl_id, dataset_impl_labels.VALUE as dataset_impl_label_value, " +
                "dataset_in_mem_impls.ID as dataset_in_mem_impl_id, dataset_in_mem_impl_kvs.ID as dataset_in_mem_impl_kv_id, dataset_in_mem_impl_kvs.IMPL_MEM_ID as dataset_in_mem_impl_kv_impl_id, dataset_in_mem_impl_kvs.KEY as dataset_in_mem_impl_kvs_key, dataset_in_mem_impl_kvs.VALUE as dataset_in_mem_impl_kvs_value " +
                "FROM " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("Datasets").getName() + " datasets " +
                "left outer join " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("DatasetImplementations").getName() + " dataset_impls " +
                "on dataset_impls.DATASET_ID=datasets.ID " +
                "left outer join " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("DatasetInMemoryImplementations").getName() + " dataset_in_mem_impls " +
                "on dataset_impls.ID = dataset_in_mem_impls.ID " +
                "left outer join " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("DatasetInMemoryImplementationKeyValues").getName() + " dataset_in_mem_impl_kvs " +
                "on dataset_in_mem_impls.ID = dataset_in_mem_impl_kvs.IMPL_MEM_ID " +
                "left outer join " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("DatasetImplementationLabels").getName() + " dataset_impl_labels " +
                "on dataset_impls.ID = dataset_impl_labels.DATASET_IMPL_ID where datasets.ID={0};";
    }
    private String getBaseQuery(Pageable pageable, Set<DatasetFilter> datasetFilters) {
        return "select datasets.ID " +
                "FROM " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("Datasets").getName() + " datasets " +
                getWhereClause(datasetFilters) +
                getOrderByClause(pageable) +
                getLimitAndOffsetClause(pageable);
    }

    private String getWhereClause(Set<DatasetFilter> datasetFilters) {
        String filterStatements = datasetFilters.stream()
                .map(datasetFilter -> {
                    if (datasetFilter.getFilterOption().equals(DatasetFilterOption.NAME)) {
                        return filterService.getStringCondition("datasets.NAME", datasetFilter);
                    } else {
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .collect(Collectors.joining(" and "));
        return filterStatements.isEmpty() ? "" : " WHERE " + filterStatements;
    }

    private String getOrderByClause(Pageable pageable) {
        if (pageable.isUnpaged()) {
            return " ";
        }
        if (pageable.getSort().isUnsorted()) {
            // set default ordering for pagination to last loaded
            return " ORDER BY datasets.LOAD_TMS ASC ";
        } else {
            return " ";
        }
    }

    private long getRowSize(Set<DatasetFilter> datasetFilters) throws SQLException {
        String query = "select count(*) as row_count from " +
                MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("Datasets").getName() + " datasets " +
                getWhereClause(datasetFilters) + ";";
        CachedRowSet cachedRowSet = metadataRepositoryConfiguration.getDesignMetadataRepository().executeQuery(query, "reader");
        cachedRowSet.next();
        return cachedRowSet.getLong("row_count");
    }
}
