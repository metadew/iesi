package io.metadew.iesi.server.rest.security_group;

import io.metadew.iesi.common.configuration.metadata.repository.MetadataRepositoryConfiguration;
import io.metadew.iesi.common.configuration.metadata.tables.MetadataTablesConfiguration;
import io.metadew.iesi.connection.tools.SQLTools;
import io.metadew.iesi.server.rest.dataset.FilterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import javax.sql.rowset.CachedRowSet;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.*;
import java.util.stream.Collectors;

@Repository
@ConditionalOnWebApplication
public class SecurityGroupDtoRepository implements ISecurityGroupDtoRepository {

    private final MetadataRepositoryConfiguration metadataRepositoryConfiguration;
    private final FilterService filterService;

    private static final String FETCH_SINGLE_QUERY = "select security_groups.id as security_groups_id, security_groups.name as security_groups_name, " +
            "security_group_teams.team_id as security_group_teams_team_id, " +
            "teams.id as team_id, teams.TEAM_NAME as team_name " +
            " FROM " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("SecurityGroups").getName() + " security_groups " +
            " LEFT OUTER JOIN " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("SecurityGroupTeams").getName() + " security_group_teams " +
            " ON security_groups.ID = security_group_teams.SECURITY_GROUP_ID " +
            " LEFT OUTER JOIN " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("Teams").getName() + " teams " +
            " ON teams.ID = security_group_teams.TEAM_ID " +
            " WHERE security_groups.ID={0};";

    private static final String FETCH_BY_NAME_QUERY = "select security_groups.id as security_groups_id, security_groups.name as security_groups_name, " +
            "security_group_teams.team_id as security_group_teams_team_id, " +
            "teams.id as team_id, teams.TEAM_NAME as team_name " +
            " FROM " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("SecurityGroups").getName() + " security_groups " +
            " LEFT OUTER JOIN " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("SecurityGroupTeams").getName() + " security_group_teams " +
            " ON security_groups.ID = security_group_teams.SECURITY_GROUP_ID " +
            " LEFT OUTER JOIN " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("Teams").getName() + " teams " +
            " ON teams.ID = security_group_teams.TEAM_ID " +
            " WHERE security_groups.NAME={0};";

    private static final String FETCH_ALL_QUERY = "select security_groups.id as security_groups_id, security_groups.name as security_groups_name, " +
            "security_group_teams.team_id as security_group_teams_team_id, " +
            "teams.id as team_id, teams.TEAM_NAME as team_name " +
            " FROM " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("SecurityGroups").getName() + " security_groups " +
            " LEFT OUTER JOIN " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("SecurityGroupTeams").getName() + " security_group_teams " +
            " ON security_groups.ID = security_group_teams.SECURITY_GROUP_ID" +
            " LEFT OUTER JOIN " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("Teams").getName() + " teams " +
            " ON teams.ID = security_group_teams.TEAM_ID;";

    @Autowired
    public SecurityGroupDtoRepository(MetadataRepositoryConfiguration metadataRepositoryConfiguration, FilterService filterService) {
        this.metadataRepositoryConfiguration = metadataRepositoryConfiguration;
        this.filterService = filterService;
    }

    private String getFetchAllQuery(Pageable pageable, List<SecurityGroupFilter> securityGroupFilters) {
        return "select " +
                "security_groups.id as security_groups_id, security_groups.name as security_groups_name, " +
                "security_group_teams.team_id as security_group_teams_team_id, " +
                "teams.id as team_id, teams.TEAM_NAME as team_name " +
                " FROM " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("SecurityGroups").getName() + " security_groups " +
                " LEFT OUTER JOIN " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("SecurityGroupTeams").getName() + " security_group_teams " +
                " ON security_groups.ID = security_group_teams.SECURITY_GROUP_ID" +
                " LEFT OUTER JOIN " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("Teams").getName() + " teams " +
                " ON teams.ID = security_group_teams.TEAM_ID;";
    }

    private String getBaseQUery(Pageable pageable, List<SecurityGroupFilter> securityGroupFilters) {
        return "select distinct security_groups.id, security_groups.name " +
                "from " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("SecurityGroups").getName() + " security_groups " +
                getWhereClause(securityGroupFilters) +


    }

    private String getWhereClause(List<SecurityGroupFilter> securityGroupFilters) {
        String filterStatements = securityGroupFilters.stream()
                .map(securityGroupFilter -> {
                    if (securityGroupFilter.getFilterOption().equals(SecurityGroupFilterOption.NAME)) {
                        return filterService.getStringCondition("security_groups.name", securityGroupFilter);
                    } else {
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .collect(Collectors.joining(" and "));
        return filterStatements;
    }

    private String getOrderByClause(Pageable pageable) {
        if (pageable.getSort().isUnsorted()) return " ORDER BY security_groups.id";
        List<String> sorting = pageable.getSort().stream().map(order -> {
            if (order.getProperty().equals("NAME")) {
                return "security_groups.name " + order.getDirection();
            } else {
                return null;
            }
        })
                .filter(Objects::nonNull)
                .collect(Collectors.toList())
    }

    public Set<SecurityGroupDto> getAll(Pageable pageable, List<SecurityGroupFilter> securityGroupFilters){
        try {
            String query = getFetchAllQuery(pageable, securityGroupFilters);
            CachedRowSet cachedRowSet = metadataRepositoryConfiguration.getControlMetadataRepository().executeQuery(
                    query,
                    "reader");
            return new HashSet<>(new SecurityGroupDtoListResultSetExtractor().extractData(cachedRowSet));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public Optional<SecurityGroupDto> get(String teamName) {
        try {
            CachedRowSet cachedRowSet = metadataRepositoryConfiguration.getControlMetadataRepository().executeQuery(
                    MessageFormat.format(FETCH_BY_NAME_QUERY, SQLTools.getStringForSQL(teamName)),
                    "reader");
            return new SecurityGroupDtoListResultSetExtractor().extractData(cachedRowSet).stream()
                    .findFirst();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public Optional<SecurityGroupDto> get(UUID id){
        try {
            CachedRowSet cachedRowSet = metadataRepositoryConfiguration.getControlMetadataRepository().executeQuery(
                    MessageFormat.format(FETCH_SINGLE_QUERY, SQLTools.getStringForSQL(id)),
                    "reader");
            return new SecurityGroupDtoListResultSetExtractor().extractData(cachedRowSet).stream()
                    .findFirst();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
