package io.metadew.iesi.server.rest.user;

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
public class UserDtoRepository extends PaginatedRepository implements IUserDtoRepository {

    private static final String FETCH_SINGLE_QUERY = "select " +
            "users.ID as user_id, users.USERNAME as user_username, " +
            "users.ENABLED as user_enabled, users.EXPIRED as user_expired, users.CREDENTIALS_EXPIRED as user_credentials_expired, users.LOCKED as user_locked, " +
            "roles.ID as role_id, roles.role_name as role_role_name, " +
            "privileges.ID as privilege_id, privileges.privilege as privilege_privilege, " +
            "teams.ID as team_id, teams.TEAM_NAME as team_name, " +
            "security_groups.ID as security_group_id, security_groups.NAME as security_group_name " +
            " FROM " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("Users").getName() + " users" +
            " LEFT OUTER JOIN " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("UserRoles").getName() + " user_roles " +
            " ON users.ID = user_roles.USER_ID " +
            " LEFT OUTER JOIN " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("Roles").getName() + " roles " +
            " ON user_roles.ROLE_ID = roles.ID " +
            " LEFT OUTER JOIN " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("Privileges").getName() + " privileges " +
            " ON privileges.ROLE_ID = roles.ID " +
            " LEFT OUTER JOIN " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("Teams").getName() + " teams " +
            " ON teams.ID = roles.TEAM_ID " +
            " LEFT OUTER JOIN " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("SecurityGroupTeams").getName() + " security_group_teams " +
            " ON teams.ID = security_group_teams.TEAM_ID " +
            " LEFT OUTER JOIN " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("SecurityGroups").getName() + " security_groups " +
            " ON security_groups.ID = security_group_teams.SECURITY_GROUP_ID " +
            " WHERE users.ID={0};";


    private static final String FETCH_SINGLE_BY_NAME_QUERY = "select " +
            "users.ID as user_id, users.USERNAME as user_username, " +
            "users.ENABLED as user_enabled, users.EXPIRED as user_expired, users.CREDENTIALS_EXPIRED as user_credentials_expired, users.LOCKED as user_locked, " +
            "roles.ID as role_id, roles.role_name as role_role_name, " +
            "privileges.ID as privilege_id, privileges.privilege as privilege_privilege, " +
            "teams.ID as team_id, teams.TEAM_NAME as team_name, " +
            "security_groups.ID as security_group_id, security_groups.NAME as security_group_name " +
            " FROM " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("Users").getName() + " users" +
            " LEFT OUTER JOIN " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("UserRoles").getName() + " user_roles " +
            " ON users.ID = user_roles.USER_ID " +
            " LEFT OUTER JOIN " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("Roles").getName() + " roles " +
            " ON user_roles.ROLE_ID = roles.ID " +
            " LEFT OUTER JOIN " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("Privileges").getName() + " privileges " +
            " ON privileges.ROLE_ID = roles.ID " +
            " LEFT OUTER JOIN " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("Teams").getName() + " teams " +
            " ON teams.ID = roles.TEAM_ID " +
            " LEFT OUTER JOIN " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("SecurityGroupTeams").getName() + " security_group_teams " +
            " ON teams.ID = security_group_teams.TEAM_ID " +
            " LEFT OUTER JOIN " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("SecurityGroups").getName() + " security_groups " +
            " ON security_groups.ID = security_group_teams.SECURITY_GROUP_ID " +
            " WHERE users.USERNAME={0};";

    private final MetadataRepositoryConfiguration metadataRepositoryConfiguration;
    private final FilterService filterService;

    @Autowired
    public UserDtoRepository(MetadataRepositoryConfiguration metadataRepositoryConfiguration, FilterService filterService) {
        this.metadataRepositoryConfiguration = metadataRepositoryConfiguration;
        this.filterService = filterService;
    }

    private String getFetchAllQuery(Pageable pageable, Set<UserFilter> userFilters) {
        return "select " +
                "users.ID as user_id, users.USERNAME as user_username, " +
                "users.ENABLED as user_enabled, users.EXPIRED as user_expired, users.CREDENTIALS_EXPIRED as user_credentials_expired, users.LOCKED as user_locked, " +
                "roles.ID as role_id, roles.role_name as role_role_name, " +
                "privileges.ID as privilege_id, privileges.privilege as privilege_privilege, " +
                "teams.ID as team_id, teams.TEAM_NAME as team_name, " +
                "security_groups.ID as security_group_id, security_groups.NAME as security_group_name " +
                " FROM (" + getBaseQuery(pageable, userFilters) + ") base_users " + //base table
                " inner join " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("Users").getName() + " users " +
                " on base_users.ID=users.ID " +
                " LEFT OUTER JOIN " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("UserRoles").getName() + " user_roles " +
                " ON users.ID = user_roles.USER_ID " +
                " LEFT OUTER JOIN " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("Roles").getName() + " roles " +
                " ON user_roles.ROLE_ID = roles.ID " +
                " LEFT OUTER JOIN " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("Privileges").getName() + " privileges " +
                " ON privileges.ROLE_ID = roles.ID " +
                " LEFT OUTER JOIN " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("Teams").getName() + " teams " +
                " ON teams.ID = roles.TEAM_ID" +
                " LEFT OUTER JOIN " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("SecurityGroupTeams").getName() + " security_group_teams " +
                " ON teams.ID = security_group_teams.TEAM_ID " +
                " LEFT OUTER JOIN " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("SecurityGroups").getName() + " security_groups " +
                " ON security_groups.ID = security_group_teams.SECURITY_GROUP_ID;";
    }

    private String getBaseQuery(Pageable pageable, Set<UserFilter> userFilters) {
        return "select users.ID " +
                "FROM " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("Users").getName() + " users " +
                getWhereClause(userFilters) +
                getOrderByClause(pageable) +
                getLimitAndOffsetClause(pageable);
    }

    private String getOrderByClause(Pageable pageable) {
        if (pageable.getSort().isUnsorted()) return " ORDER BY lower(users.username) ASC ";
        List<String> sorting = pageable.getSort().stream().map(order -> {
                    if (order.getProperty().equalsIgnoreCase("USERNAME")) {
                        return "lower(users.username)" + order.getDirection();
                    } else {
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
        if (sorting.isEmpty()) {
            sorting.add("lower(users.username) ASC");
        }

        return " ORDER BY " + String.join(", ", sorting) + " ";
    }

    private String getWhereClause(Set<UserFilter> datasetFilters) {
        String filterStatements = datasetFilters.stream()
                .map(datasetFilter -> {
                    if (datasetFilter.getFilterOption().equals(UserFilterOption.USERNAME)) {
                        return filterService.getStringCondition("users.USERNAME", datasetFilter);
                    } else {
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .collect(Collectors.joining(" and "));
        return filterStatements.isEmpty() ? "" : " WHERE " + filterStatements;
    }

    public Optional<UserDto> get(String username) {
        try {
            CachedRowSet cachedRowSet = metadataRepositoryConfiguration.getDesignMetadataRepository().executeQuery(
                    MessageFormat.format(FETCH_SINGLE_BY_NAME_QUERY, SQLTools.getStringForSQL(username)),
                    "reader");
            return new UserDtoListResultSetExtractor()
                    .extractData(cachedRowSet).stream()
                    .findFirst();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public Optional<UserDto> get(UUID id) {
        try {
            CachedRowSet cachedRowSet = metadataRepositoryConfiguration.getDesignMetadataRepository().executeQuery(
                    MessageFormat.format(FETCH_SINGLE_QUERY, SQLTools.getStringForSQL(id)),
                    "reader");
            return new UserDtoListResultSetExtractor()
                    .extractData(cachedRowSet).stream()
                    .findFirst();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public Page<UserDto> getAll(Pageable pageable, Set<UserFilter> userFilters) {
        try {
            CachedRowSet cachedRowSet = metadataRepositoryConfiguration.getDesignMetadataRepository().executeQuery(
                    getFetchAllQuery(pageable, userFilters),
                    "reader");
            return new PageImpl<>(new UserDtoListResultSetExtractor().extractData(cachedRowSet),
                    pageable,
                    getRowSize(userFilters));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private long getRowSize(Set<UserFilter> userFilters) throws SQLException {
        String query = "select count(*) as row_count from " +
                MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("Users").getName() + " users " +
                getWhereClause(userFilters) + ";";
        CachedRowSet cachedRowSet = metadataRepositoryConfiguration.getDesignMetadataRepository().executeQuery(query, "reader");
        cachedRowSet.next();
        return cachedRowSet.getLong("row_count");
    }

}
