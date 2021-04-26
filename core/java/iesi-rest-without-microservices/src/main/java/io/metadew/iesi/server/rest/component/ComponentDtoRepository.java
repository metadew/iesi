package io.metadew.iesi.server.rest.component;

import io.metadew.iesi.common.configuration.metadata.repository.MetadataRepositoryConfiguration;
import io.metadew.iesi.common.configuration.metadata.tables.MetadataTablesConfiguration;
import io.metadew.iesi.metadata.definition.component.key.ComponentKey;
import io.metadew.iesi.server.rest.component.dto.ComponentDto;
import io.metadew.iesi.server.rest.component.dto.ComponentParameterDto;
import io.metadew.iesi.server.rest.component.dto.ComponentVersionDto;
import io.metadew.iesi.server.rest.dataset.FilterService;
import io.metadew.iesi.server.rest.helper.PaginatedRepository;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Repository;

import javax.sql.rowset.CachedRowSet;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

@Repository
public class ComponentDtoRepository extends PaginatedRepository implements IComponentDtoRepository {
    private final MetadataRepositoryConfiguration metadataRepositoryConfiguration;
    private final FilterService filterService;

    public ComponentDtoRepository(MetadataRepositoryConfiguration metadataRepositoryConfiguration, FilterService filterService) {
        this.metadataRepositoryConfiguration = metadataRepositoryConfiguration;
        this.filterService = filterService;
    }

    private String getFetchAllQuery(Authentication authentication, Pageable pageable, List<ComponentFilter> componentFilters) {
        return "select component_designs.COMP_ID,component_designs.COMP_TYP_NM, component_designs.COMP_NM, component_designs.COMP_DSC, versions.COMP_VRS_NB, " +
                "versions.COMP_VRS_DSC, " + "parameters.COMP_PAR_NM, " + "parameters.COMP_PAR_VAL, " + "versions.COMP_VRS_DSC " +
                "FROM (" + getBaseQuery(authentication, pageable, componentFilters) + ") base_components " +
                "INNER JOIN " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("Components").getName() + " component_designs " +
                "on base_components.COMP_ID = component_designs.COMP_ID " +
                "INNER JOIN " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("ComponentVersions").getName() + " versions " +
                "on base_components.COMP_ID = versions.COMP_ID AND base_components.COMP_VRS_NB = versions.COMP_VRS_NB " +
                "LEFT OUTER JOIN " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("ComponentParameters").getName() + " parameters " +
                "on base_components.COMP_ID = parameters.COMP_ID AND base_components.COMP_VRS_NB = parameters.COMP_VRS_NB " +
                getOrderByClause(pageable) +
                ";";
    }

    private String getBaseQuery(Authentication authentication, Pageable pageable, List<ComponentFilter> componentFilters) {
        return "select distinct component_designs.COMP_ID, versions.COMP_VRS_NB " +
                "from " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("Components").getName() + " component_designs " +
                "INNER JOIN " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("ComponentVersions").getName() + " versions " +
                "on component_designs.COMP_ID = versions.COMP_ID" +
                getWhereClause(componentFilters) +
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
            List<ComponentDto> componentDtoList = componentDtoBuilders.values().stream()
                    .map(ComponentDtoBuilder::build)
                    .collect(Collectors.toList());
            return new PageImpl<>(componentDtoList, pageable, getRowSize(authentication));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private long getRowSize(Authentication authentication) throws SQLException {
        String query = "select count(*) as row_count from (select distinct component_designs.COMP_ID, versions.COMP_VRS_NB " +
                "from " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("Components").getName() + " component_designs " +
                "INNER JOIN " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("ComponentVersions").getName() + " versions " +
                "on component_designs.COMP_ID = versions.COMP_ID );";
        CachedRowSet cachedRowSet = metadataRepositoryConfiguration.getDesignMetadataRepository().executeQuery(query, "reader");
        cachedRowSet.next();
        return cachedRowSet.getLong("row_count");
    }

    private String getOrderByClause(Pageable pageable) {
        if (pageable.getSort().isUnsorted()) return " ";
        List<String> sorting = pageable.getSort().stream().map(order -> {
            // add further sort on the ScriptAndScriptVersionTable here
            if (order.getProperty().equalsIgnoreCase("NAME")) {
                return "component_designs.COMP_NM" + " " + order.getDirection();
            } else if (order.getProperty().equalsIgnoreCase("VERSION")) {
                return "versions.COMP_VRS_NB" + " " + order.getDirection();
            } else {
                return null;
            }
        })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
        if (sorting.isEmpty()) {
            return "";
        }
        return " ORDER BY " + String.join(", ", sorting) + " ";
    }

    private String getWhereClause(List<ComponentFilter> componentFilters) {
        String filterStatements = componentFilters.stream()
                .map(componentFilter -> filterService.getStringCondition("component_designs.COMP_NM", componentFilter))
                .filter(Objects::nonNull)
                .collect(Collectors.joining(" and "));

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
                        cachedRowSet.getString("COMP_PAR_NM"), cachedRowSet.getString("COMP_PAR_VAL")
                ));
            }
        }
    }

    @AllArgsConstructor
    @Getter
    private static class ComponentDtoBuilder {
        private final String type;
        private final String name;
        private final String description;
        private final ComponentVersionDto version;
        private final Map<String, ComponentParameterDto> parameters;

        public ComponentDto build() {
            return new ComponentDto(
                    type, name, description, version,
                    new ArrayList<>(parameters.values()),
                    new ArrayList<>()
            );
        }

    }
}
