package io.metadew.iesi.server.rest.dataset.dto;

import io.metadew.iesi.common.configuration.metadata.repository.MetadataRepositoryConfiguration;
import io.metadew.iesi.common.configuration.metadata.tables.MetadataTablesConfiguration;
import io.metadew.iesi.connection.tools.SQLTools;
import io.metadew.iesi.server.rest.configuration.security.IESIGrantedAuthority;
import io.metadew.iesi.server.rest.dataset.DatasetFilter;
import io.metadew.iesi.server.rest.dataset.DatasetFilterOption;
import io.metadew.iesi.server.rest.dataset.FilterService;
import io.metadew.iesi.server.rest.dataset.implementation.DatasetImplementationDto;
import io.metadew.iesi.server.rest.dataset.implementation.DatasetImplementationDtoListResultSetExtractor;
import io.metadew.iesi.server.rest.helper.PaginatedRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Repository;

import javax.sql.rowset.CachedRowSet;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.*;
import java.util.stream.Collectors;

@Repository
@ConditionalOnWebApplication
public class DatasetDtoRepository extends PaginatedRepository implements IDatasetDtoRepository {


    private final MetadataRepositoryConfiguration metadataRepositoryConfiguration;
    private final FilterService filterService;
    private final MetadataTablesConfiguration metadataTablesConfiguration;

    @Autowired
    public DatasetDtoRepository(MetadataRepositoryConfiguration metadataRepositoryConfiguration,
                                FilterService filterService,
                                MetadataTablesConfiguration metadataTablesConfiguration) {
        this.metadataRepositoryConfiguration = metadataRepositoryConfiguration;
        this.filterService = filterService;
        this.metadataTablesConfiguration = metadataTablesConfiguration;
    }

    public Page<DatasetDto> fetchAll(Authentication authentication, Pageable pageable, Set<DatasetFilter> datasetFilters) {
        try {
            CachedRowSet cachedRowSet = metadataRepositoryConfiguration.getDataMetadataRepository().executeQuery(
                    getFetchAllQuery(authentication, pageable, datasetFilters),
                    "reader");
            return new PageImpl<>(new DatasetDtoListResultSetExtractor().extractData(cachedRowSet),
                    pageable,
                    getRowSize(authentication, datasetFilters));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public List<DatasetImplementationDto> fetchImplementationsByDatasetUuid(UUID datasetUuid) {
        try {
            CachedRowSet cachedRowSet = metadataRepositoryConfiguration.getDataMetadataRepository().executeQuery(
                    MessageFormat.format(getFetchImplementationsByDatasetIdQuery(),
                            SQLTools.getStringForSQL(datasetUuid)),
                    "reader");

            return new DatasetImplementationDtoListResultSetExtractor().extractData(cachedRowSet);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public Optional<DatasetImplementationDto> fetchImplementationByUuid(UUID uuid) {
        try {
            CachedRowSet cachedRowSet = metadataRepositoryConfiguration.getDataMetadataRepository().executeQuery(
                    MessageFormat.format(getFetchImplementationByIdQuery(),
                            SQLTools.getStringForSQL(uuid)),
                    "reader");
            return new DatasetImplementationDtoListResultSetExtractor().extractData(cachedRowSet).stream()
                    .findFirst();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private String getFetchAllQuery(Authentication authentication, Pageable pageable, Set<DatasetFilter> datasetFilters) {
        return "SELECT " +
                "dataset_impls.ID as dataset_impl_id, " +
                "datasets.SECURITY_GROUP_NM as dataset_security_group_name, " +
                "datasets.NAME as dataset_name, datasets.ID as dataset_id " +
                "from (" + getBaseQuery(authentication, pageable, datasetFilters) + ") base_datasets " + //base table
                "inner join " + metadataTablesConfiguration.getMetadataTableNameByLabel("Datasets").getName() + " datasets " +
                "on base_datasets.ID=datasets.ID " +
                "left outer join " + metadataTablesConfiguration.getMetadataTableNameByLabel("DatasetImplementations").getName() + " dataset_impls " +
                "on dataset_impls.DATASET_ID=datasets.ID ;";
    }

    private String getFetchImplementationByIdQuery() {
        return "SELECT " +
                "dataset_impls.ID as dataset_impl_id, " +
                "dataset_impl_labels.ID as dataset_impl_label_id, dataset_impl_labels.DATASET_IMPL_ID as dataset_impl_label_impl_id, dataset_impl_labels.VALUE as dataset_impl_label_value, " +
                "dataset_in_mem_impls.ID as dataset_in_mem_impl_id, dataset_in_mem_impl_kvs.ID as dataset_in_mem_impl_kv_id, dataset_in_mem_impl_kvs.IMPL_MEM_ID as dataset_in_mem_impl_kv_impl_id, dataset_in_mem_impl_kvs.KEY as dataset_in_mem_impl_kvs_key, dataset_in_mem_impl_kvs.VALUE as dataset_in_mem_impl_kvs_value " +
                "FROM " + metadataTablesConfiguration.getMetadataTableNameByLabel("Datasets").getName() + " datasets " +
                "left outer join " + metadataTablesConfiguration.getMetadataTableNameByLabel("DatasetImplementations").getName() + " dataset_impls " +
                "on dataset_impls.DATASET_ID=datasets.ID " +
                "left outer join " + metadataTablesConfiguration.getMetadataTableNameByLabel("DatasetInMemoryImplementations").getName() + " dataset_in_mem_impls " +
                "on dataset_impls.ID = dataset_in_mem_impls.ID " +
                "left outer join " + metadataTablesConfiguration.getMetadataTableNameByLabel("DatasetInMemoryImplementationKeyValues").getName() + " dataset_in_mem_impl_kvs " +
                "on dataset_in_mem_impls.ID = dataset_in_mem_impl_kvs.IMPL_MEM_ID " +
                "left outer join " + metadataTablesConfiguration.getMetadataTableNameByLabel("DatasetImplementationLabels").getName() + " dataset_impl_labels " +
                "on dataset_impls.ID = dataset_impl_labels.DATASET_IMPL_ID " +
                "where dataset_impls.ID={0};";
    }

    private String getFetchImplementationsByDatasetIdQuery() {
        return "SELECT " +
                "dataset_impls.ID as dataset_impl_id, " +
                "dataset_impl_labels.ID as dataset_impl_label_id, dataset_impl_labels.DATASET_IMPL_ID as dataset_impl_label_impl_id, dataset_impl_labels.VALUE as dataset_impl_label_value, " +
                "dataset_in_mem_impls.ID as dataset_in_mem_impl_id, dataset_in_mem_impl_kvs.ID as dataset_in_mem_impl_kv_id, dataset_in_mem_impl_kvs.IMPL_MEM_ID as dataset_in_mem_impl_kv_impl_id, dataset_in_mem_impl_kvs.KEY as dataset_in_mem_impl_kvs_key, dataset_in_mem_impl_kvs.VALUE as dataset_in_mem_impl_kvs_value " +
                "FROM " + metadataTablesConfiguration.getMetadataTableNameByLabel("Datasets").getName() + " datasets " +
                "left outer join " + metadataTablesConfiguration.getMetadataTableNameByLabel("DatasetImplementations").getName() + " dataset_impls " +
                "on dataset_impls.DATASET_ID=datasets.ID " +
                "left outer join " + metadataTablesConfiguration.getMetadataTableNameByLabel("DatasetInMemoryImplementations").getName() + " dataset_in_mem_impls " +
                "on dataset_impls.ID = dataset_in_mem_impls.ID " +
                "left outer join " + metadataTablesConfiguration.getMetadataTableNameByLabel("DatasetInMemoryImplementationKeyValues").getName() + " dataset_in_mem_impl_kvs " +
                "on dataset_in_mem_impls.ID = dataset_in_mem_impl_kvs.IMPL_MEM_ID " +
                "left outer join " + metadataTablesConfiguration.getMetadataTableNameByLabel("DatasetImplementationLabels").getName() + " dataset_impl_labels " +
                "on dataset_impls.ID = dataset_impl_labels.DATASET_IMPL_ID " +
                "where datasets.ID={0};";
    }

    private String getBaseQuery(Authentication authentication, Pageable pageable, Set<DatasetFilter> datasetFilters) {
        return "select datasets.ID " +
                "FROM " + metadataTablesConfiguration.getMetadataTableNameByLabel("Datasets").getName() + " datasets " +
                getWhereClause(authentication, datasetFilters) +
                getOrderByClause(pageable) +
                getLimitAndOffsetClause(pageable);
    }

    private String getWhereClause(Authentication authentication, Set<DatasetFilter> datasetFilters) {
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
        if (authentication != null) {
            Set<String> securityGroups = authentication.getAuthorities().stream()
                    .filter(authority -> authority instanceof IESIGrantedAuthority)
                    .map(authority -> (IESIGrantedAuthority) authority)
                    .map(IESIGrantedAuthority::getSecurityGroupName)
                    .map(SQLTools::getStringForSQL).collect(Collectors.toSet());
            filterStatements = filterStatements +
                    (filterStatements.isEmpty() ? "" : " and ") +
                    " datasets.SECURITY_GROUP_NM IN (" + String.join(", ", securityGroups) + ") ";
        }
        return filterStatements.isEmpty() ? "" : " WHERE " + filterStatements;
    }

    private String getOrderByClause(Pageable pageable) {
        if (pageable.getSort().isUnsorted()) return " ORDER BY lower(datasets.NAME) ASC ";
        List<String> sorting = pageable.getSort().stream().map(order -> {
                    if (order.getProperty().equalsIgnoreCase("NAME")) {
                        return "lower(datasets.NAME) " + order.getDirection();
                    } else {
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
        if (sorting.isEmpty()) {
            return " ORDER BY lower(datasets.NAME) ASC";
        }
        return " ORDER BY " + String.join(", ", sorting) + " ";
    }

    private long getRowSize(Authentication authentication, Set<DatasetFilter> datasetFilters) throws SQLException {
        String query = "select count(*) as row_count from " +
                metadataTablesConfiguration.getMetadataTableNameByLabel("Datasets").getName() + " datasets " +
                getWhereClause(authentication, datasetFilters) + ";";
        CachedRowSet cachedRowSet = metadataRepositoryConfiguration.getDataMetadataRepository().executeQuery(query, "reader");
        cachedRowSet.next();
        return cachedRowSet.getLong("row_count");
    }
}
