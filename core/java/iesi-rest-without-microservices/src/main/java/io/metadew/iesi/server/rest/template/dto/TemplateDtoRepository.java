package io.metadew.iesi.server.rest.template.dto;

import io.metadew.iesi.common.configuration.metadata.repository.MetadataRepositoryConfiguration;
import io.metadew.iesi.common.configuration.metadata.tables.MetadataTablesConfiguration;
import io.metadew.iesi.server.rest.dataset.FilterService;
import io.metadew.iesi.server.rest.helper.PaginatedRepository;
import io.metadew.iesi.server.rest.template.TemplateFilter;
import io.metadew.iesi.server.rest.template.TemplateFilterOption;
import lombok.extern.log4j.Log4j2;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Repository;

import javax.sql.rowset.CachedRowSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Repository
@ConditionalOnWebApplication
@Log4j2
public class TemplateDtoRepository extends PaginatedRepository implements  ITemplateDtoRepository {

    private final MetadataRepositoryConfiguration metadataRepositoryConfiguration;
    private final FilterService filterService;

    public TemplateDtoRepository(FilterService filterService, MetadataRepositoryConfiguration metadataRepositoryConfiguration) {
        this.metadataRepositoryConfiguration = metadataRepositoryConfiguration;
        this.filterService = filterService;
    }

    @Override
    public Page<TemplateDto> getAll(Authentication authentication, Pageable pageable, boolean onlyLatestVersion, Set<TemplateFilter> templateFilters) {
        try {
            log.info(getFetchAllQuery(authentication, pageable, onlyLatestVersion, templateFilters));
            CachedRowSet cachedRowSet = metadataRepositoryConfiguration.getDesignMetadataRepository().executeQuery(
                    getFetchAllQuery(authentication, pageable, onlyLatestVersion, templateFilters),
                    "reader");
            List<TemplateDto> templateDtos = new TemplateDtoListResultSetExtractor().extractData(cachedRowSet);
            log.info("TEMPLATE DTOS: " + templateDtos);
            return new PageImpl<>(templateDtos, pageable, getRowSize(authentication, templateFilters, onlyLatestVersion));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


    private String getFetchAllQuery(Authentication authentication, Pageable pageable, boolean onlyLatestVersion, Set<TemplateFilter> templateFilters) {
        return  "SELECT t.ID as template_id, t.NAME as template_name, t.DESCRIPTION as template_description, t.VERSION as template_version, " +
                "m.KEY as matcher_key, " +
                "CASE " +
                "WHEN mva.ID IS NOT NULL THEN 'any' " +
                "WHEN mvf.ID IS NOT NULL THEN 'fixed' " +
                "WHEN mvt.ID IS NOT NULL THEN 'template' " +
                "ELSE 'undefined' " +
                "END matcherValue_type, " +
                "mvf.VALUE as matcherValue_fixedValue, " +
                "mvt.TEMPLATE_NAME as matcherValue_templateName, " +
                "mvt.TEMPLATE_VERSION as matcherValue_templateVersion " +
                "FROM (" + getBaseQuery(authentication, pageable, onlyLatestVersion, templateFilters) + ") base_templates " +
                "INNER JOIN " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("Templates").getName() + " t " +
                "ON base_templates.ID = t.ID " +
                "INNER JOIN " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("Matchers").getName() + " m " +
                "ON t.ID = m.TEMPLATE_ID " +
                "INNER JOIN " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("MatcherValues").getName() + " mv " +
                "ON m.ID = mv.MATCHER_ID " +
                "LEFT OUTER JOIN " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("AnyMatcherValues").getName()  + " mva " +
                "ON mva.ID = mv.ID " +
                "LEFT OUTER JOIN " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("FixedMatcherValues").getName() + " mvf " +
                "ON mvf.ID = mv.ID " +
                "LEFT OUTER JOIN " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("TemplateMatcherValues").getName() + " mvt " +
                "ON mvt.ID = mv.ID; ";
    }

    private String getBaseQuery(Authentication authentication, Pageable pageable, boolean onlyLatestVersion, Set<TemplateFilter> templateFilters) {
        return  "SELECT distinct templates.ID, templates.NAME, templates.VERSION " +
                "FROM " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("Templates").getName() + " templates " +
                getWhereClause(authentication, templateFilters, onlyLatestVersion) +
                getOrderByClause(pageable) +
                getLimitAndOffsetClause(pageable);
    }

    private String getWhereClause(Authentication authentication, Set<TemplateFilter> templateFilters, boolean onlyLatestVersion) {
        String filterStatements = templateFilters.stream()
                .map(templateFilter -> {
                    if (templateFilter.getFilterOption().equals(TemplateFilterOption.NAME)) {
                        return filterService.getStringCondition("templates.NAME", templateFilter);
                    } else {
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .collect(Collectors.joining(" and "));
        if (onlyLatestVersion) {
            //TODO
        }

        return filterStatements.isEmpty() ? " " : " WHERE " + filterStatements;
    }

    private String getOrderByClause(Pageable pageable) {
        if (pageable.getSort().isUnsorted()) return " ORDER BY templates.ID ";
        List<String> sorting = pageable.getSort().stream().map(order -> {
            if (order.getProperty().equalsIgnoreCase("NAME")) {
                return "lower(templates.NAME) " + order.getDirection();
            } else {
                return null;
            }
        })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        if (sorting.isEmpty()) {
            return " ORDER BY lower(templates.NAME) ASC";
        }
        return " ORDER BY " + String.join(", ", sorting) + " ";
    }

    private long getRowSize(Authentication authentication, Set<TemplateFilter> templateFilters, boolean onlyLatestVersion) throws SQLException {
        String query = "select count(*) as row_count from " +
                MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("Templates").getName() + " templates " +
                getWhereClause(authentication, templateFilters, onlyLatestVersion) + ";";
        CachedRowSet cachedRowSet = metadataRepositoryConfiguration.getDesignMetadataRepository().executeQuery(query, "reader");
        cachedRowSet.next();
        return cachedRowSet.getLong("row_count");
    }
}
