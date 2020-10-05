package io.metadew.iesi.metadata.configuration.security;

import io.metadew.iesi.common.configuration.metadata.tables.MetadataTablesConfiguration;
import io.metadew.iesi.metadata.configuration.Configuration;
import io.metadew.iesi.metadata.definition.security.SecurityGroup;
import io.metadew.iesi.metadata.definition.security.SecurityGroupKey;
import lombok.Data;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@Data
public class SecurityGroupConfiguration extends Configuration<SecurityGroup, SecurityGroupKey> {

    private static String fetchSingleQuery = "select security_groups.id as security_groups_id, security_groups.name as security_groups_name, " +
            "security_group_teams.team_id as security_group_teams_team_id " +
            " FROM " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("SecurityGroups").getName() + " security_groups " +
            " LEFT OUTER JOIN " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("SecurityGroupTeams").getName() + " security_group_teams " +
            " ON security_groups.ID = security_group_teams.SECURITY_GROUP_ID " +
            " WHERE security_groups.ID={0};";

    private static String fetchByNameQuery = "select security_groups.id as security_groups_id, security_groups.name as security_groups_name, " +
            "security_group_teams.team_id as security_group_teams_team_id " +
            " FROM " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("SecurityGroups").getName() + " security_groups " +
            " LEFT OUTER JOIN " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("SecurityGroupTeams").getName() + " security_group_teams " +
            " ON security_groups.ID = security_group_teams.SECURITY_GROUP_ID " +
            " WHERE security_groups.NAME={0};";

    private static String deleteSingleQuery = "DELETE FROM " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("SecurityGroups").getName() +
            " WHERE ID={0};";

    private static String deleteTeamMembershipsBySecurityGroupIdQuery = "DELETE FROM " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("SecurityGroupTeams").getName() +
            " WHERE SECURITY_GROUP_ID={0};";

    private static String insertQuery = "INSERT INTO " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("SecurityGroups").getName() +
            " (ID, NAME) VALUES ({0}, {1});";

    private static String insertSecurityGroupTeamMembershipQuery = "INSERT INTO " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("SecurityGroupTeams").getName() +
            " (SECURITY_GROUP_ID, TEAM_ID) VALUES ({0}, {1});";

    private static String updateQuery = "UPDATE " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("SecurityGroups") +
            " SET NAME = {0} " +
            " WHERE ID = {1};";

    private static SecurityGroupConfiguration INSTANCE;

    public synchronized static SecurityGroupConfiguration getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new SecurityGroupConfiguration();
        }
        return INSTANCE;
    }

    private SecurityGroupConfiguration() {
    }

    @Override
    public Optional<SecurityGroup> get(SecurityGroupKey metadataKey) {
        return Optional.empty();
    }

    @Override
    public List<SecurityGroup> getAll() throws SQLException {
        return null;
    }

    @Override
    public void delete(SecurityGroupKey metadataKey) {

    }

    @Override
    public void insert(SecurityGroup metadata) {

    }
    @Override
    public void update(SecurityGroup metadata) {

    }

}
