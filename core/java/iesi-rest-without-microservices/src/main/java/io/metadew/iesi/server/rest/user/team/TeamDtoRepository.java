package io.metadew.iesi.server.rest.user.team;

import io.metadew.iesi.common.configuration.metadata.repository.MetadataRepositoryConfiguration;
import io.metadew.iesi.common.configuration.metadata.tables.MetadataTablesConfiguration;
import io.metadew.iesi.connection.tools.SQLTools;
import io.metadew.iesi.server.rest.dataset.FilterService;
import io.metadew.iesi.server.rest.helper.PaginatedRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
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
@ConditionalOnWebApplication
public class TeamDtoRepository extends PaginatedRepository implements ITeamDtoRepository {

    private final MetadataRepositoryConfiguration metadataRepositoryConfiguration;
    private final FilterService filterService;

    private static final String FETCH_SINGLE_QUERY = "select " +
            "teams.ID as team_id, teams.TEAM_NAME as team_name, " +
            "roles.id as role_id, roles.team_id as role_team_id, roles.role_name as role_role_name, " +
            "privileges.id as privilege_id, privileges.role_id as privilege_role_id, privileges.privilege as privilege_privilege, " +
            "users.ID as user_id, users.USERNAME as user_username, " +
            "users.ENABLED as user_enabled, users.EXPIRED as user_expired, users.CREDENTIALS_EXPIRED as user_credentials_expired, users.LOCKED as user_locked, " +
            "security_groups.ID as security_group_id, security_groups.NAME as security_group_name " +
            " FROM " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("Teams").getName() + " teams" +
            " LEFT OUTER JOIN " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("Roles").getName() + " roles " +
            " ON teams.ID = roles.TEAM_ID " +
            " LEFT OUTER JOIN " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("Privileges").getName() + " privileges " +
            " ON roles.ID = privileges.ROLE_ID " +
            " LEFT OUTER JOIN " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("UserRoles").getName() + " user_roles " +
            " ON roles.ID = user_roles.ROLE_ID " +
            " LEFT OUTER JOIN " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("Users").getName() + " users " +
            " ON users.ID = user_roles.USER_ID " +
            " LEFT OUTER JOIN " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("SecurityGroupTeams").getName() + " security_group_teams " +
            " ON teams.ID = security_group_teams.TEAM_ID " +
            " LEFT OUTER JOIN " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("SecurityGroups").getName() + " security_groups " +
            " ON security_groups.ID = security_group_teams.SECURITY_GROUP_ID " +
            " WHERE teams.ID={0};";

    private static final String FETCH_SINGLE_QUERY_BY_NAME = "select " +
            "teams.ID as team_id, teams.TEAM_NAME as team_name, " +
            "roles.id as role_id, roles.team_id as role_team_id, roles.role_name as role_role_name, " +
            "privileges.id as privilege_id, privileges.role_id as privilege_role_id, privileges.privilege as privilege_privilege, " +
            "users.ID as user_id, users.USERNAME as user_username, " +
            "users.ENABLED as user_enabled, users.EXPIRED as user_expired, users.CREDENTIALS_EXPIRED as user_credentials_expired, users.LOCKED as user_locked, " +
            "security_groups.ID as security_group_id, security_groups.NAME as security_group_name " +
            " FROM " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("Teams").getName() + " teams" +
            " LEFT OUTER JOIN " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("Roles").getName() + " roles " +
            " ON teams.ID = roles.TEAM_ID " +
            " LEFT OUTER JOIN " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("Privileges").getName() + " privileges " +
            " ON roles.ID = privileges.ROLE_ID " +
            " LEFT OUTER JOIN " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("UserRoles").getName() + " user_roles " +
            " ON roles.ID = user_roles.ROLE_ID " +
            " LEFT OUTER JOIN " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("Users").getName() + " users " +
            " ON users.ID = user_roles.USER_ID " +
            " LEFT OUTER JOIN " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("SecurityGroupTeams").getName() + " security_group_teams " +
            " ON teams.ID = security_group_teams.TEAM_ID " +
            " LEFT OUTER JOIN " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("SecurityGroups").getName() + " security_groups " +
            " ON security_groups.ID = security_group_teams.SECURITY_GROUP_ID " +
            " WHERE teams.TEAM_NAME={0};";

    @Autowired
    public TeamDtoRepository(MetadataRepositoryConfiguration metadataRepositoryConfiguration, FilterService filterService) {
        this.metadataRepositoryConfiguration = metadataRepositoryConfiguration;
        this.filterService = filterService;
    }

    private String getFetchAllQuery(Pageable pageable, List<TeamFilter> teamFilters) {
        return "select " +
                "teams.ID as team_id, teams.TEAM_NAME as team_name, " +
                "roles.id as role_id, roles.team_id as role_team_id, roles.role_name as role_role_name, " +
                "privileges.id as privilege_id, privileges.role_id as privilege_role_id, privileges.privilege as privilege_privilege, " +
                "users.ID as user_id, users.USERNAME as user_username, " +
                "users.ENABLED as user_enabled, users.EXPIRED as user_expired, users.CREDENTIALS_EXPIRED as user_credentials_expired, users.LOCKED as user_locked, " +
                "security_groups.ID as security_group_id, security_groups.NAME as security_group_name " +
                "FROM (" + getBaseQuery(pageable, teamFilters) + ") base_teams " +
                "INNER JOIN " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("Teams").getName() + " teams" +
                " ON base_teams.id = teams.id " +
                "LEFT OUTER JOIN " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("Roles").getName() + " roles " +
                "ON teams.ID = roles.TEAM_ID " +
                "LEFT OUTER JOIN " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("Privileges").getName() + " privileges " +
                "ON roles.ID = privileges.ROLE_ID " +
                "LEFT OUTER JOIN " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("UserRoles").getName() + " user_roles " +
                "ON roles.ID = user_roles.ROLE_ID " +
                "LEFT OUTER JOIN " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("Users").getName() + " users " +
                "ON users.ID = user_roles.USER_ID " +
                "LEFT OUTER JOIN " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("SecurityGroupTeams").getName() + " security_group_teams " +
                "ON teams.ID = security_group_teams.TEAM_ID " +
                "LEFT OUTER JOIN " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("SecurityGroups").getName() + " security_groups " +
                "ON security_groups.ID = security_group_teams.SECURITY_GROUP_ID;";
    }

    private String getBaseQuery(Pageable pageable, List<TeamFilter> teamFilters) {
        return "select distinct teams.id, teams.team_name " +
                "from " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("Teams").getName() + " teams " +
                getWhereClause(teamFilters) +
                getOrderByCLause(pageable) +
                getLimitAndOffsetClause(pageable);
    }

    private String getWhereClause(List<TeamFilter> teamFilters) {
        String filterStatements = teamFilters.stream()
                .map(teamFilter -> {
                    if (teamFilter.getFilterOption().equals(TeamFilterOption.NAME)) {
                        return filterService.getStringCondition("teams.team_name", teamFilter);
                    } else {
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .collect(Collectors.joining(" and "));

        return filterStatements.isEmpty() ? "" : " WHERE " + filterStatements;
    }

    private String getOrderByCLause(Pageable pageable) {
        if (pageable.getSort().isUnsorted()) return " ORDER BY teams.id";
        List<String> sorting = pageable.getSort().stream().map(order -> {
                    if (order.getProperty().equalsIgnoreCase("NAME")) {
                        return "teams.team_name " + order.getDirection();
                    } else {
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
        if (sorting.isEmpty()) {
            sorting.add("ORDER BY teams.id");
        }

        return " ORDER BY " + String.join(", ", sorting) + " ";
    }

    private long getRowSize(List<TeamFilter> teamFilters) throws SQLException {
        String query = "select count(*) as row_count from (select distinct teams.id " +
                "from " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("Teams").getName() + " teams " +
                getWhereClause(teamFilters) +
                ");";
        CachedRowSet cachedRowSet = metadataRepositoryConfiguration.getDesignMetadataRepository().executeQuery(query, "reader");
        cachedRowSet.next();
        return cachedRowSet.getLong("row_count");
    }

    public Optional<TeamDto> get(String teamName) {
        try {
            CachedRowSet cachedRowSet = metadataRepositoryConfiguration.getDesignMetadataRepository().executeQuery(
                    MessageFormat.format(FETCH_SINGLE_QUERY_BY_NAME, SQLTools.getStringForSQL(teamName)),
                    "reader");
            return new TeamDtoListResultSetExtractor()
                    .extractData(cachedRowSet).stream()
                    .findFirst();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    public Optional<TeamDto> get(UUID uuid) {
        try {
            CachedRowSet cachedRowSet = metadataRepositoryConfiguration.getDesignMetadataRepository().executeQuery(
                    MessageFormat.format(FETCH_SINGLE_QUERY, SQLTools.getStringForSQL(uuid)),
                    "reader");
            return new TeamDtoListResultSetExtractor()
                    .extractData(cachedRowSet).stream()
                    .findFirst();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public Page<TeamDto> getAll(Pageable pageable, List<TeamFilter> teamFilters) {
        try {
            String query = getFetchAllQuery(pageable, teamFilters);
            CachedRowSet cachedRowSet = metadataRepositoryConfiguration.getDesignMetadataRepository().executeQuery(
                    query,
                    "reader");
            List<TeamDto> teamDtos = new ArrayList<>(new TeamDtoListResultSetExtractor().extractData(cachedRowSet));
            return new PageImpl<>(teamDtos, pageable, getRowSize(teamFilters));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
