package io.metadew.iesi.server.rest.environment.dto;

import io.metadew.iesi.common.configuration.metadata.repository.MetadataRepositoryConfiguration;
import io.metadew.iesi.common.configuration.metadata.tables.MetadataTablesConfiguration;
import io.metadew.iesi.server.rest.dataset.FilterService;
import io.metadew.iesi.server.rest.environment.EnvironmentFilter;
import io.metadew.iesi.server.rest.environment.EnvironmentFilterOption;
import io.metadew.iesi.server.rest.helper.PaginatedRepository;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Repository;

import javax.sql.rowset.CachedRowSet;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Repository
@ConditionalOnWebApplication
public class EnvironmentDtoRepository extends PaginatedRepository implements IEnvironmentDtoRepository {

    private final String ENVIRONMENT_TABLE_LABEL = "Environments";
    private final String ENVIRONMENT_PARAMETER_TABLE_LABEL = "EnvironmentParameters";

    private final MetadataRepositoryConfiguration metadataRepositoryConfiguration;
    private final MetadataTablesConfiguration metadataTablesConfiguration;
    private final FilterService filterService;

    @Autowired
    public EnvironmentDtoRepository(MetadataRepositoryConfiguration metadataRepositoryConfiguration,
                                    MetadataTablesConfiguration metadataTablesConfiguration,
                                    FilterService filterService) {
        this.metadataRepositoryConfiguration = metadataRepositoryConfiguration;
        this.metadataTablesConfiguration = metadataTablesConfiguration;
        this.filterService = filterService;
    }

    private String getFetchAllQuery(Authentication authentication, Pageable pageable, List<EnvironmentFilter> environmentFilters) {
        return "select environments.ENV_NM, environments.ENV_DSC, " +
                "parameters.ENV_PAR_NM, parameters.ENV_PAR_VAL " +
                "FROM (" + getBaseQuery(authentication, pageable, environmentFilters) + ") base_environments " +
                "INNER JOIN " + metadataTablesConfiguration.getMetadataTableNameByLabel(ENVIRONMENT_TABLE_LABEL).getName() + " environments " +
                "on base_environments.ENV_NM = environments.ENV_NM " +
                "LEFT OUTER JOIN " + metadataTablesConfiguration.getMetadataTableNameByLabel(ENVIRONMENT_PARAMETER_TABLE_LABEL).getName() + " parameters " +
                "on environments.ENV_NM = parameters.ENV_NM " +
                getOrderByClause(pageable) +
                ";";
    }
    private String getBaseQuery(Authentication authentication, Pageable pageable, List<EnvironmentFilter> environmentFilters) {
        return "select distinct environments.ENV_NM " +
                "from " + metadataTablesConfiguration.getMetadataTableNameByLabel(ENVIRONMENT_TABLE_LABEL).getName() + " environments " +
                getWhereClause(authentication, environmentFilters) +
                getOrderByClause(pageable) +
                getLimitAndOffsetClause(pageable);
    }

    @Override
    public Optional<EnvironmentDto> getByName(Authentication authentication, String name) {
        try {
            Map<String, EnvironmentDtoRepository.EnvironmentDtoBuilder> environmentDtoBuilder = new HashMap<>();
            List<EnvironmentFilter> environmentFilters = Stream.of(
                    new EnvironmentFilter(EnvironmentFilterOption.NAME, name, true)
            ).collect(Collectors.toList());
            String query = getFetchAllQuery(authentication, Pageable.unpaged(), environmentFilters);
            CachedRowSet cachedRowSet = metadataRepositoryConfiguration.getConnectivityMetadataRepository().executeQuery(query, "reader");
            while (cachedRowSet.next()) {
                mapRow(cachedRowSet, environmentDtoBuilder);
            }
            return environmentDtoBuilder.values().stream().findFirst().map(EnvironmentDtoRepository.EnvironmentDtoBuilder::build);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private String getWhereClause(Authentication authentication, List<EnvironmentFilter> environmentFilters) {
        String filterStatements = environmentFilters.stream()
                .map(environmentFilter -> {
                    if (environmentFilter.getFilterOption().equals(EnvironmentFilterOption.NAME)) {
                        return filterService.getStringCondition("environments.ENV_NM", environmentFilter);
                    } else {
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .collect(Collectors.joining(" and "));
        

        return filterStatements.isEmpty() ? "" : " WHERE " + filterStatements;
    }

    /*
    private String getBaseQuery(Pageable pageable){
        return "select distinct environments.ENV_NM " +
                "from " + metadataTablesConfiguration.getMetadataTableNameByLabel(ENVIRONMENT_TABLE_LABEL).getName() + " environments " +
                getOrderByClause(pageable) +
                getLimitAndOffsetClause(pageable);
    }

    @Override
    public Page<EnvironmentDto> getAll(Pageable pageable) {
        try {
            Map<String, EnvironmentDtoBuilder> environmentDtoBuilders = new LinkedHashMap<>();
            String query = getFetchAllQuery(pageable);
            CachedRowSet cachedRowSet = metadataRepositoryConfiguration.getConnectivityMetadataRepository().executeQuery(query, "reader");
            while (cachedRowSet.next()){
                mapRow(cachedRowSet, environmentDtoBuilders);
            }
            List<EnvironmentDto> environmentDtoList = environmentDtoBuilders.values().stream()
                    .map(EnvironmentDtoBuilder::build)
                    .collect(Collectors.toList());
            return new PageImpl<>(environmentDtoList, pageable, getRowSize());
        }catch (SQLException e){
            throw new RuntimeException(e);
        }
    }*/
    @Override
    public Page<EnvironmentDto> getAll(Authentication authentication, Pageable pageable, List<EnvironmentFilter> environmentFilters) {
        try {
            Map<String, EnvironmentDtoRepository.EnvironmentDtoBuilder> environmentDtoBuilder = new LinkedHashMap<>();
            String query = getFetchAllQuery(authentication, pageable, environmentFilters);
            CachedRowSet cachedRowSet = metadataRepositoryConfiguration.getConnectivityMetadataRepository().executeQuery(query, "reader");
            while (cachedRowSet.next()) {
                mapRow(cachedRowSet, environmentDtoBuilder);
            }
            List<EnvironmentDto> environmentDtoList = environmentDtoBuilder.values().stream()
                    .map(EnvironmentDtoRepository.EnvironmentDtoBuilder::build)
                    .collect(Collectors.toList());

            return new PageImpl<>(environmentDtoList, pageable, getRowSize(authentication, environmentFilters));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private long getRowSize(Authentication authentication, List<EnvironmentFilter> environmentFilters) throws SQLException {
        String query = "select count(*) as row_count from (select distinct environments.ENV_NM " +
                "from " + metadataTablesConfiguration.getMetadataTableNameByLabel(ENVIRONMENT_TABLE_LABEL).getName() + " environments " +
                getWhereClause(authentication, environmentFilters) +
                ");";
        CachedRowSet cachedRowSet = metadataRepositoryConfiguration.getConnectivityMetadataRepository().executeQuery(query, "reader");
        cachedRowSet.next();
        return cachedRowSet.getLong("row_count");
    }

    private String getOrderByClause(Pageable pageable) {
        if (pageable.getSort().isUnsorted()) return " ORDER BY lower(environments.ENV_NM) ASC ";
        List<String> sorting = pageable.getSort().stream().map(order -> {
            if (order.getProperty().equalsIgnoreCase("NAME")) {
                return "lower(environments.ENV_NM)" + order.getDirection();
            } else {
                return null;
            }
        })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
        if (sorting.isEmpty()) {
            return " ORDER BY lower(environments.ENV_NM) ASC";
        }
        return " ORDER BY " + String.join(", ", sorting) + " ";
    }

    private void mapRow(CachedRowSet cachedRowSet, Map<String, EnvironmentDtoBuilder> environmentDtoBuilders) throws SQLException{
        String environmentName = cachedRowSet.getString("ENV_NM");
        EnvironmentDtoBuilder environmentDtoBuilder = environmentDtoBuilders.get(environmentName);
        if(environmentDtoBuilder == null){
            environmentDtoBuilder = mapEnvironmentDto(cachedRowSet);
            environmentDtoBuilders.put(environmentName, environmentDtoBuilder);
        }
        mapEnvironmentParameter(cachedRowSet, environmentDtoBuilder);
    }

    private EnvironmentDtoBuilder mapEnvironmentDto(CachedRowSet cachedRowSet) throws SQLException{
        return new EnvironmentDtoBuilder(
                cachedRowSet.getString("ENV_NM"),
                cachedRowSet.getString("ENV_DSC"),
                new HashMap<>()
        );
    }

    private void mapEnvironmentParameter(CachedRowSet cachedRowSet, EnvironmentDtoBuilder environmentDtoBuilder) throws SQLException{
        String environmentParameterName = cachedRowSet.getString("ENV_PAR_NM");
        if(environmentParameterName != null){
            EnvironmentParameterDto environmentParameterDto = environmentDtoBuilder.getParameters().get(environmentParameterName);
            if(environmentParameterDto == null){
                environmentDtoBuilder.getParameters().put(environmentParameterName, new EnvironmentParameterDto(
                        cachedRowSet.getString("ENV_PAR_NM"),
                        cachedRowSet.getString("ENV_PAR_VAL")
                ));
            }
        }
    }

    @AllArgsConstructor
    @Getter
    private static class EnvironmentDtoBuilder{
        private final String name;
        private final String description;
        private final Map<String, EnvironmentParameterDto> parameters;

        public EnvironmentDto build(){
            return new EnvironmentDto(
                    name,
                    description,
                    new ArrayList<>(parameters.values())
            );
        }
    }
}



