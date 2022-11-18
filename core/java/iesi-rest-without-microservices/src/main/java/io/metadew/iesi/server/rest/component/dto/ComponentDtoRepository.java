package io.metadew.iesi.server.rest.component.dto;

import io.metadew.iesi.common.configuration.metadata.repository.MetadataRepositoryConfiguration;
import io.metadew.iesi.common.configuration.metadata.tables.MetadataTablesConfiguration;
import io.metadew.iesi.connection.tools.SQLTools;
import io.metadew.iesi.metadata.definition.component.key.ComponentKey;
import io.metadew.iesi.server.rest.component.ComponentFilter;
import io.metadew.iesi.server.rest.component.ComponentFilterOption;
import io.metadew.iesi.server.rest.component.IComponentDtoRepository;
import io.metadew.iesi.server.rest.configuration.security.IESIGrantedAuthority;
import io.metadew.iesi.server.rest.dataset.FilterService;
import io.metadew.iesi.server.rest.helper.PaginatedRepository;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;
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

@Log4j2
@Repository
@ConditionalOnWebApplication
public class ComponentDtoRepository extends PaginatedRepository implements IComponentDtoRepository {

    private final MetadataRepositoryConfiguration metadataRepositoryConfiguration;
    private final MetadataTablesConfiguration metadataTablesConfiguration;
    private final FilterService filterService;

    @Autowired
    public ComponentDtoRepository(MetadataRepositoryConfiguration metadataRepositoryConfiguration,
                                  MetadataTablesConfiguration metadataTablesConfiguration,
                                  FilterService filterService) {
        this.metadataRepositoryConfiguration = metadataRepositoryConfiguration;
        this.metadataTablesConfiguration = metadataTablesConfiguration;
        this.filterService = filterService;
    }

    private String getFetchAllQuery(Authentication authentication, Pageable pageable, List<ComponentFilter> componentFilters) {
        return "select component_designs.COMP_ID, component_designs.SECURITY_GROUP_NM, component_designs.COMP_TYP_NM, component_designs.COMP_NM, component_designs.COMP_DSC, versions.COMP_VRS_NB, " +
                "versions.COMP_VRS_DSC, " + "parameters.COMP_PAR_NM, " + "parameters.COMP_PAR_VAL, " + "versions.COMP_VRS_DSC " +
                "FROM (" + getBaseQuery(authentication, pageable, componentFilters) + ") base_components " +
                "INNER JOIN " + metadataTablesConfiguration.getMetadataTableNameByLabel("Components").getName() + " component_designs " +
                "on base_components.COMP_ID = component_designs.COMP_ID " +
                "INNER JOIN " + metadataTablesConfiguration.getMetadataTableNameByLabel("ComponentVersions").getName() + " versions " +
                "on base_components.COMP_ID = versions.COMP_ID AND base_components.COMP_VRS_NB = versions.COMP_VRS_NB " +
                "LEFT OUTER JOIN " + metadataTablesConfiguration.getMetadataTableNameByLabel("ComponentParameters").getName() + " parameters " +
                "on base_components.COMP_ID = parameters.COMP_ID AND base_components.COMP_VRS_NB = parameters.COMP_VRS_NB " +
                getOrderByClause(pageable) +
                ";";
    }

    private String getBaseQuery(Authentication authentication, Pageable pageable, List<ComponentFilter> componentFilters) {
        return "select distinct component_designs.COMP_ID, component_designs.SECURITY_GROUP_NM, versions.COMP_VRS_NB " +
                "from " + metadataTablesConfiguration.getMetadataTableNameByLabel("Components").getName() + " component_designs " +
                "INNER JOIN " + metadataTablesConfiguration.getMetadataTableNameByLabel("ComponentVersions").getName() + " versions " +
                "on component_designs.COMP_ID = versions.COMP_ID" +
                getWhereClause(authentication, componentFilters) +
                getOrderByClause(pageable) +
                getLimitAndOffsetClause(pageable);
    }

    @Override
    public Page<ComponentDto> getAll(Authentication authentication, Pageable pageable, List<ComponentFilter> componentFilters) {
        try {
            Map<ComponentKey, ComponentDtoBuilder> componentDtoBuilders = new LinkedHashMap<>();
            String query = getFetchAllQuery(authentication, pageable, componentFilters);
            CachedRowSet cachedRowSet = metadataRepositoryConfiguration.getDesignMetadataRepository().executeQuery(query, "reader");

            while (cachedRowSet.next()) {
                mapRow(cachedRowSet, componentDtoBuilders);
            }
            List<ComponentDto> components = componentDtoBuilders.values().stream()
                    .map(ComponentDtoBuilder::build)
                    .collect(Collectors.toList());
            return new PageImpl<>(components, pageable, getRowSize(authentication, componentFilters));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Page<ComponentDto> getByName(Authentication authentication, Pageable pageable, String name) {
        try {
            Map<ComponentKey, ComponentDtoBuilder> componentDtoBuilders = new LinkedHashMap<>();
            List<ComponentFilter> componentFilters = Stream.of(new ComponentFilter(ComponentFilterOption.NAME, name, true)).collect(Collectors.toList());
            String query = getFetchAllQuery(authentication, pageable, componentFilters);
            CachedRowSet cachedRowSet = metadataRepositoryConfiguration.getDesignMetadataRepository().executeQuery(query, "reader");
            while (cachedRowSet.next()) {
                mapRow(cachedRowSet, componentDtoBuilders);
            }
            return new PageImpl<>(
                    componentDtoBuilders.values().stream()
                            .map(ComponentDtoBuilder::build)
                            .collect(Collectors.toList()),
                    pageable,
                    getRowSize(authentication, componentFilters));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<ComponentDto> getByNameAndVersion(Authentication authentication, String name, long version) {
        try {
            Map<ComponentKey, ComponentDtoBuilder> componentDtoBuilders = new HashMap<>();
            List<ComponentFilter> componentFilters = Stream.of(new ComponentFilter(ComponentFilterOption.NAME, name, true),
                            new ComponentFilter(ComponentFilterOption.VERSION, Long.toString(version), true))
                    .collect(Collectors.toList());
            String query = getFetchAllQuery(authentication, Pageable.unpaged(), componentFilters);
            CachedRowSet cachedRowSet = metadataRepositoryConfiguration.getDesignMetadataRepository().executeQuery(query, "reader");
            while (cachedRowSet.next()) {
                mapRow(cachedRowSet, componentDtoBuilders);
            }
            if (componentDtoBuilders.values().size() > 1) {
                log.warn("found multiple components for component " + name + "-" + version);
            }
            return componentDtoBuilders.values().stream().findFirst().map(ComponentDtoBuilder::build);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private long getRowSize(Authentication authentication, List<ComponentFilter> componentFilters) throws SQLException {
        String query = "select count(*) as row_count from (select distinct component_designs.COMP_ID, versions.COMP_VRS_NB " +
                "from " + metadataTablesConfiguration.getMetadataTableNameByLabel("Components").getName() + " component_designs " +
                "INNER JOIN " + metadataTablesConfiguration.getMetadataTableNameByLabel("ComponentVersions").getName() + " versions " +
                "on component_designs.COMP_ID = versions.COMP_ID " +
                getWhereClause(authentication, componentFilters) +
                ");";
        CachedRowSet cachedRowSet = metadataRepositoryConfiguration.getDesignMetadataRepository().executeQuery(query, "reader");
        cachedRowSet.next();
        return cachedRowSet.getLong("row_count");
    }

    private String getOrderByClause(Pageable pageable) {
        if (pageable.getSort().isUnsorted()) return " ORDER BY component_designs.COMP_ID ASC ";
        List<String> sorting = pageable.getSort().stream().map(order -> {
                    if (order.getProperty().equalsIgnoreCase("NAME")) {
                        return "lower(component_designs.COMP_NM) " + order.getDirection();
                    } else if (order.getProperty().equalsIgnoreCase("VERSION")) {
                        return "versions.COMP_VRS_NB " + order.getDirection();
                    } else {
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
        if (sorting.isEmpty()) {
            sorting.add("ORDER BY component_designs.COMP_ID ASC");
        }
        return " ORDER BY " + String.join(", ", sorting) + " ";
    }

    private String getWhereClause(Authentication authentication, List<ComponentFilter> componentFilters) {
        String filterStatements = componentFilters.stream()
                .map(componentFilter -> {
                    if (componentFilter.getFilterOption().equals(ComponentFilterOption.NAME)) {
                        return filterService.getStringCondition("component_designs.COMP_NM", componentFilter);
                    } else if (componentFilter.getFilterOption().equals(ComponentFilterOption.VERSION)) {
                        return filterService.getStringCondition("versions.COMP_VRS_NB", componentFilter);
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
                    " component_designs.SECURITY_GROUP_NM IN (" + String.join(", ", securityGroups) + ") ";
        }

        return filterStatements.isEmpty() ? "" : " WHERE " + filterStatements;
    }

    private void mapRow(CachedRowSet cachedRowSet, Map<ComponentKey, ComponentDtoBuilder> componentDtoBuilders) throws SQLException {
        ComponentKey componentKey = new ComponentKey(cachedRowSet.getString("COMP_NM"), cachedRowSet.getLong("COMP_VRS_NB"));
        ComponentDtoBuilder componentDtoBuilder = componentDtoBuilders.get(componentKey);
        if (componentDtoBuilder == null) {
            componentDtoBuilder = mapComponentDto(cachedRowSet);
            componentDtoBuilders.put(componentKey, componentDtoBuilder);
        }
        mapComponentParameter(cachedRowSet, componentDtoBuilder);
    }

    private ComponentDtoBuilder mapComponentDto(CachedRowSet cachedRowSet) throws SQLException {
        return new ComponentDtoBuilder(
                cachedRowSet.getString("COMP_TYP_NM"),
                cachedRowSet.getString("SECURITY_GROUP_NM"),
                cachedRowSet.getString("COMP_NM"),
                cachedRowSet.getString("COMP_DSC"),
                new ComponentVersionDto(
                        cachedRowSet.getLong("COMP_VRS_NB"),
                        cachedRowSet.getString("COMP_VRS_DSC")
                ),
                new HashMap<>()
        );
    }

    private void mapComponentParameter(CachedRowSet cachedRowSet, ComponentDtoBuilder componentDtoBuilder) throws SQLException {
        String componentParameterName = cachedRowSet.getString("COMP_PAR_NM");
        if (componentParameterName != null) {
            ComponentParameterDto componentParameterDto = componentDtoBuilder.getParameters().get(componentParameterName);
            if (componentParameterDto == null) {
                componentDtoBuilder.getParameters().put(componentParameterName, new ComponentParameterDto(
                        cachedRowSet.getString("COMP_PAR_NM"),
                        SQLTools.getStringFromSQLClob(cachedRowSet, "COMP_PAR_VAL")
                ));
            }
        }
    }

    @AllArgsConstructor
    @Getter
    private static class ComponentDtoBuilder {
        private final String type;
        private final String securityGroupName;
        private final String name;
        private final String description;
        private final ComponentVersionDto version;
        private final Map<String, ComponentParameterDto> parameters;

        public ComponentDto build() {
            return new ComponentDto(
                    type, securityGroupName, name, description, version,
                    new HashSet<>(parameters.values()),
                    new HashSet<>()
            );
        }
    }
}
