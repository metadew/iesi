package io.metadew.iesi.server.rest.connection.dto;

import io.metadew.iesi.common.configuration.metadata.repository.MetadataRepositoryConfiguration;
import io.metadew.iesi.common.configuration.metadata.tables.MetadataTablesConfiguration;
import io.metadew.iesi.connection.tools.SQLTools;
import io.metadew.iesi.server.rest.connection.ConnectionFilter;
import io.metadew.iesi.server.rest.connection.ConnectionFilterOption;
import io.metadew.iesi.server.rest.dataset.FilterService;
import io.metadew.iesi.server.rest.helper.PaginatedRepository;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import javax.sql.rowset.CachedRowSet;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Repository
@ConditionalOnWebApplication
public class ConnectionDtoRepository extends PaginatedRepository implements IConnectionDtoRepository {

    private final MetadataRepositoryConfiguration metadataRepositoryConfiguration;
    private final FilterService filterService;

    @Autowired
    public ConnectionDtoRepository(MetadataRepositoryConfiguration metadataRepositoryConfiguration, FilterService filterService) {
        this.metadataRepositoryConfiguration = metadataRepositoryConfiguration;
        this.filterService = filterService;
    }

    private String getFetchAllQuery(Pageable pageable, List<ConnectionFilter> connectionFilters) {
        return "select connections.CONN_NM, connections.CONN_TYP_NM, connections.CONN_DSC, " +
                "parameters.CONN_PAR_NM, " + "parameters.CONN_PAR_VAL, environments.ENV_NM " +
                "FROM (" + getBaseQuery(pageable, connectionFilters) + ") base_connections " +
                "INNER JOIN " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("Connections").getName() + " connections " +
                "on base_connections.CONN_NM = connections.CONN_NM " +
                "LEFT OUTER JOIN " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("ConnectionParameters").getName() + " parameters " +
                "on connections.CONN_NM = parameters.CONN_NM " +
                "INNER JOIN " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("Environments").getName() + " environments " +
                "on parameters.ENV_NM = environments.ENV_NM " +
                getOrderByClause(pageable) +
                ";";
    }

    private String getBaseQuery(Pageable pageable, List<ConnectionFilter> connectionFilters) {
        return "select distinct connections.CONN_NM " +
                "from " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("Connections").getName() + " connections " +
                getWhereClause(connectionFilters) +
                getOrderByClause(pageable) +
                getLimitAndOffsetClause(pageable);
    }

    @Override
    public Page<ConnectionDto> getAll(Pageable pageable, List<ConnectionFilter> connectionFilters) {
        try {
            Map<String, ConnectionDtoBuilder> connectionDtoBuilders = new LinkedHashMap<>();
            String query = getFetchAllQuery(pageable, connectionFilters);
            CachedRowSet cachedRowSet = metadataRepositoryConfiguration.getConnectivityMetadataRepository().executeQuery(query, "reader");
            while (cachedRowSet.next()) {
                mapRow(cachedRowSet, connectionDtoBuilders);
            }
            List<ConnectionDto> connectionDtoList = connectionDtoBuilders.values().stream()
                    .map(ConnectionDtoBuilder::build)
                    .collect(Collectors.toList());
            return new PageImpl<>(connectionDtoList, pageable, getRowSize(connectionFilters));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<ConnectionDto> getByName(String name) {
        try {
            Map<String, ConnectionDtoBuilder> connectionDtoBuilders = new HashMap<>();
            List<ConnectionFilter> connectionFilters = Stream.of(
                    new ConnectionFilter(ConnectionFilterOption.NAME, name, true)
            ).collect(Collectors.toList());
            String query = getFetchAllQuery(Pageable.unpaged(), connectionFilters);
            CachedRowSet cachedRowSet = metadataRepositoryConfiguration.getConnectivityMetadataRepository().executeQuery(query, "reader");
            while (cachedRowSet.next()) {
                mapRow(cachedRowSet, connectionDtoBuilders);
            }
            return connectionDtoBuilders.values().stream().findFirst().map(ConnectionDtoBuilder::build);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private long getRowSize(List<ConnectionFilter> connectionFilters) throws SQLException {
        String query = "select count(*) as row_count from (select distinct connections.CONN_NM " +
                "from " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("Connections").getName() + " connections " +
                getWhereClause(connectionFilters) +
                ");";
        CachedRowSet cachedRowSet = metadataRepositoryConfiguration.getConnectivityMetadataRepository().executeQuery(query, "reader");
        cachedRowSet.next();
        return cachedRowSet.getLong("row_count");
    }

    private String getOrderByClause(Pageable pageable) {
        if (pageable.getSort().isUnsorted()) return " ORDER BY connections.CONN_NM ASC ";
        List<String> sorting = pageable.getSort().stream().map(order -> {
            if (order.getProperty().equalsIgnoreCase("NAME")) {
                return "connections.CONN_NM" + " " + order.getDirection();
            } else {
                return null;
            }
        })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
        if (sorting.isEmpty()) {
            return " ORDER BY connections.CONN_NM ASC";
        }
        return " ORDER BY " + String.join(", ", sorting) + " ";
    }

    private String getWhereClause(List<ConnectionFilter> componentFilters) {
        String filterStatements = componentFilters.stream()
                .map(componentFilter -> {
                    if (componentFilter.getFilterOption().equals(ConnectionFilterOption.NAME)) {
                        return filterService.getStringCondition("connections.CONN_NM", componentFilter);
                    } else {
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .collect(Collectors.joining(" and "));

        return filterStatements.isEmpty() ? "" : " WHERE " + filterStatements;
    }

    private void mapRow(CachedRowSet cachedRowSet, Map<String, ConnectionDtoBuilder> connectionDtoBuilders) throws SQLException {
        String connectionName = cachedRowSet.getString("CONN_NM");
        ConnectionDtoBuilder connectionDtoBuilder = connectionDtoBuilders.get(connectionName);
        if (connectionDtoBuilder == null) {
            connectionDtoBuilder = mapConnectionDto(cachedRowSet);
            connectionDtoBuilders.put(connectionName, connectionDtoBuilder);
        }
        mapConnectionParameters(cachedRowSet, connectionDtoBuilder);
    }

    private ConnectionDtoBuilder mapConnectionDto(CachedRowSet cachedRowSet) throws SQLException {
        return new ConnectionDtoBuilder(
                cachedRowSet.getString("CONN_NM"),
                cachedRowSet.getString("CONN_TYP_NM"),
                cachedRowSet.getString("CONN_DSC"),
                new HashMap<>()
        );
    }

    private void mapConnectionParameters(CachedRowSet cachedRowSet, ConnectionDtoBuilder connectionDtoBuilder) throws SQLException {
        String environmentName = cachedRowSet.getString("ENV_NM");
        ConnectionEnvironmentBuilder connectionEnvironmentBuilder = connectionDtoBuilder.getEnvironments().get(environmentName);

        if (connectionEnvironmentBuilder == null) {
            connectionEnvironmentBuilder = mapConnectionEnvironment(cachedRowSet);
            connectionDtoBuilder.getEnvironments().put(environmentName, connectionEnvironmentBuilder);
        }

        mapEnvironmentsParameters(cachedRowSet, connectionEnvironmentBuilder);
    }

    private ConnectionEnvironmentBuilder mapConnectionEnvironment(CachedRowSet cachedRowSet) throws SQLException {
        return new ConnectionEnvironmentBuilder(
                cachedRowSet.getString("ENV_NM"),
                new HashMap<>()
        );
    }

    private void mapEnvironmentsParameters(CachedRowSet cachedRowSet, ConnectionEnvironmentBuilder connectionEnvironmentBuilder) throws SQLException {
        String parameterName = cachedRowSet.getString("CONN_PAR_NM");
        if (parameterName != null) {
            ConnectionParameterDto connectionParameter = connectionEnvironmentBuilder.getParameters().get(parameterName);
            if (connectionParameter == null) {
                connectionEnvironmentBuilder.getParameters().put(
                        cachedRowSet.getString("CONN_PAR_NM"),
                        new ConnectionParameterDto(
                                cachedRowSet.getString("CONN_PAR_NM"),
                                SQLTools.getStringFromSQLClob(cachedRowSet, "CONN_PAR_VAL")
                        )
                );
            }
        }
    }

    @AllArgsConstructor
    @Getter
    private static class ConnectionDtoBuilder {
        private final String name;
        private final String type;
        private final String description;
        private final Map<String, ConnectionEnvironmentBuilder> environments;

        public ConnectionDto build() {
            return new ConnectionDto(
                    name,
                    type,
                    description,
                    new HashSet<>(environments.values().stream().map(ConnectionEnvironmentBuilder::build).collect(Collectors.toList()))
            );
        }
    }

    @AllArgsConstructor
    @Getter
    private static class ConnectionEnvironmentBuilder {
        private final String environment;
        private final Map<String, ConnectionParameterDto> parameters;

        public ConnectionEnvironmentDto build() {
            return new ConnectionEnvironmentDto(
                    environment,
                    new HashSet<>(parameters.values())
            );
        }
    }
}
