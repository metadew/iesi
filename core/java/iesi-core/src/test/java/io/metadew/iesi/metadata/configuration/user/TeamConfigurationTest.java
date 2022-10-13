package io.metadew.iesi.metadata.configuration.user;

import io.metadew.iesi.TestConfiguration;
import io.metadew.iesi.common.configuration.metadata.repository.MetadataRepositoryConfiguration;
import io.metadew.iesi.metadata.configuration.security.SecurityGroupConfiguration;
import io.metadew.iesi.metadata.definition.security.SecurityGroupKey;
import io.metadew.iesi.metadata.definition.user.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.HashSet;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = { TestConfiguration.class, TeamConfiguration.class, RoleConfiguration.class, RoleListResultSetExtractor.class, UserConfiguration.class, TeamListResultSetExtractor.class,
        SecurityGroupConfiguration.class })
@ActiveProfiles("test")
class TeamConfigurationTest {

    TeamKey teamKey1;
    Team team1;
    TeamKey teamKey2;
    Team team2;
    SecurityGroupKey securityGroupKey1;
    SecurityGroupKey securityGroupKey2;
    RoleKey roleKey1;
    Role role1;
    RoleKey roleKey2;
    Role role2;
    Privilege privilege1;
    Privilege privilege2;
    Privilege privilege3;
    Privilege privilege4;

    @Autowired
    MetadataRepositoryConfiguration metadataRepositoryConfiguration;
    @Autowired
    TeamConfiguration teamConfiguration;

    @BeforeEach
    void setup() {
        teamKey1 = new TeamKey(UUID.randomUUID());
        teamKey2 = new TeamKey(UUID.randomUUID());
        roleKey1 = new RoleKey(UUID.randomUUID());
        roleKey2 = new RoleKey(UUID.randomUUID());
        securityGroupKey1 = new SecurityGroupKey(UUID.randomUUID());
        securityGroupKey2 = new SecurityGroupKey(UUID.randomUUID());

        privilege1 = Privilege.builder()
                .privilegeKey(PrivilegeKey.builder()
                        .uuid(UUID.randomUUID())
                        .build())
                .privilege("authority1")
                .roleKey(roleKey1)
                .build();
        privilege2 = Privilege.builder()
                .privilegeKey(PrivilegeKey.builder()
                        .uuid(UUID.randomUUID())
                        .build())
                .privilege("authority2")
                .roleKey(roleKey1)
                .build();
        privilege3 = Privilege.builder()
                .privilegeKey(PrivilegeKey.builder()
                        .uuid(UUID.randomUUID())
                        .build())
                .privilege("authority3")
                .roleKey(roleKey2)
                .build();
        privilege4 = Privilege.builder()
                .privilegeKey(PrivilegeKey.builder()
                        .uuid(UUID.randomUUID())
                        .build())
                .roleKey(roleKey2)
                .privilege("authority4")
                .build();
        role1 = Role.builder()
                .metadataKey(roleKey1)
                .teamKey(teamKey1)
                .name("role1")
                .users(new HashSet<>())
                .privileges(Stream.of(privilege1, privilege2).collect(Collectors.toSet()))
                .build();
        role2 = Role.builder()
                .metadataKey(roleKey2)
                .teamKey(teamKey2)
                .name("role2")
                .users(new HashSet<>())
                .privileges(Stream.of(privilege3, privilege4).collect(Collectors.toSet()))
                .build();
        team1 = Team.builder()
                .teamKey(teamKey1)
                .teamName("team1")
                .roles(Stream.of(role1).collect(Collectors.toSet()))
                .securityGroups(new HashSet<>())
                .build();
        team2 = Team.builder()
                .teamKey(teamKey2)
                .teamName("team2")
                .roles(Stream.of(role2).collect(Collectors.toSet()))
                .securityGroups(new HashSet<>())
                .build();

        metadataRepositoryConfiguration.createAllTables();
    }

    @AfterEach
    void tearDown() {
        metadataRepositoryConfiguration.dropAllTables();
    }

    @Test
    void userDoesNotExistsTest() {
        assertThat(teamConfiguration.exists(teamKey1)).isFalse();
    }

    @Test
    void userExistsTest() {
       teamConfiguration.insert(team1);
        assertThat(teamConfiguration.exists(teamKey1)).isTrue();
    }

    @Test
    void userGetDoesNotExistsTest() {
        assertThat(teamConfiguration.get(teamKey1)).isEmpty();
       teamConfiguration.insert(team1);
        assertThat(teamConfiguration.get(teamKey2)).isEmpty();
    }

    @Test
    void userGetExistsTest() {
       teamConfiguration.insert(team1);
        assertThat(teamConfiguration.get(teamKey1))
                .isPresent()
                .hasValue(team1);
    }

    @Test
    void userInsertTest() {
        assertThat(teamConfiguration.exists(teamKey1))
                .isFalse();
       teamConfiguration.insert(team1);
        assertThat(teamConfiguration.exists(teamKey1))
                .isTrue();
        assertThat(teamConfiguration.get(teamKey1))
                .isPresent()
                .hasValue(team1);
    }

    @Test
    void userInsertAlreadyExistingTest() {
       teamConfiguration.insert(team1);
        assertThatThrownBy(() ->teamConfiguration.insert(team1))
                .isInstanceOf(RuntimeException.class);
    }

    @Test
    void userInsertMultipleUsersTest() {
        assertThat(teamConfiguration.exists(teamKey1))
                .isFalse();
       teamConfiguration.insert(team1);
        assertThat(teamConfiguration.exists(teamKey1))
                .isTrue();
        assertThat(teamConfiguration.get(teamKey1))
                .isPresent()
                .hasValue(team1);
       teamConfiguration.insert(team2);
        assertThat(teamConfiguration.exists(teamKey2))
                .isTrue();
        assertThat(teamConfiguration.exists(teamKey1))
                .isTrue();
        assertThat(teamConfiguration.get(teamKey2))
                .isPresent()
                .hasValue(team2);
        assertThat(teamConfiguration.get(teamKey1))
                .isPresent()
                .hasValue(team1);
    }

    @Test
    void userDeleteDoesNotExistTest() {
       teamConfiguration.delete(team1.getMetadataKey());
    }

    @Test
    void userDeleteTest() {
       teamConfiguration.insert(team1);
        assertThat(teamConfiguration.exists(teamKey1))
                .isTrue();
       teamConfiguration.delete(team1.getMetadataKey());

        assertThat(teamConfiguration.exists(teamKey1))
                .isFalse();
    }


    @Test
    void userUpdateTest() {
       teamConfiguration.insert(team1);
        Optional<Team> team =teamConfiguration.get(teamKey1);
        assertThat(team)
                .isPresent()
                .hasValue(team1);
        assertThat(team.get().getTeamName())
                .isEqualTo("team1");

        team1.setTeamName("role3");
       teamConfiguration.update(team1);

        assertThat(teamConfiguration.get(teamKey1).get().getTeamName())
                .isEqualTo("role3");
    }

    @Test
    void userUpdateMultipleTest() {
       teamConfiguration.insert(team1);
       teamConfiguration.insert(team2);
        Optional<Team> fetchedteam1 =teamConfiguration.get(teamKey1);
        assertThat(fetchedteam1)
                .isPresent()
                .hasValue(team1);
        assertThat(fetchedteam1.get().getTeamName())
                .isEqualTo("team1");
        Optional<Team> fetchedteam2 =teamConfiguration.get(teamKey2);
        assertThat(fetchedteam2)
                .isPresent()
                .hasValue(team2);
        assertThat(fetchedteam2.get().getTeamName())
                .isEqualTo("team2");

        team1.setTeamName("role3");
       teamConfiguration.update(team1);

        assertThat(teamConfiguration.get(teamKey1).get().getTeamName())
                .isEqualTo("role3");
        assertThat(teamConfiguration.get(teamKey2).get().getTeamName())
                .isEqualTo("team2");
    }

}
