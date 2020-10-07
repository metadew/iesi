package io.metadew.iesi.metadata.configuration.user;

import io.metadew.iesi.common.configuration.Configuration;
import io.metadew.iesi.common.configuration.metadata.repository.MetadataRepositoryConfiguration;
import io.metadew.iesi.metadata.definition.security.SecurityGroup;
import io.metadew.iesi.metadata.definition.security.SecurityGroupKey;
import io.metadew.iesi.metadata.definition.user.*;
import org.junit.jupiter.api.*;

import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class TeamConfigurationTest {

    private TeamKey teamKey1;
    private Team team1;
    private TeamKey teamKey2;
    private Team team2;

    private SecurityGroupKey securityGroupKey1;
    private SecurityGroup securityGroup1;
    private SecurityGroupKey securityGroupKey2;
    private SecurityGroup securityGroup2;

    private UserKey userKey1;
    private User user1;
    private UserKey userKey2;
    private User user2;

    private RoleKey roleKey1;
    private Role role1;
    private RoleKey roleKey2;
    private Role role2;

    private Privilege privilege1;
    private Privilege privilege2;
    private Privilege privilege3;
    private Privilege privilege4;


    @BeforeAll
    static void prepare() {
        Configuration.getInstance();
        MetadataRepositoryConfiguration.getInstance()
                .getControlMetadataRepository()
                .createAllTables();
    }

    @BeforeEach
    void setup() {
        teamKey1 = new TeamKey(UUID.randomUUID());
        teamKey2 = new TeamKey(UUID.randomUUID());
        roleKey1 = new RoleKey(UUID.randomUUID());
        roleKey2 = new RoleKey(UUID.randomUUID());
        userKey1 = new UserKey(UUID.randomUUID());
        userKey2 = new UserKey(UUID.randomUUID());
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
                .userKeys(Stream.of(userKey1, userKey2).collect(Collectors.toSet()))
                .privileges(Stream.of(privilege1, privilege2).collect(Collectors.toSet()))
                .build();
        role2 = Role.builder()
                .metadataKey(roleKey2)
                .teamKey(teamKey2)
                .name("role2")
                .userKeys(Stream.of(userKey1).collect(Collectors.toSet()))
                .privileges(Stream.of(privilege3, privilege4).collect(Collectors.toSet()))
                .build();
        user1 = User.builder()
                .userKey(userKey1)
                .username("user1")
                .enabled(true)
                .expired(false)
                .credentialsExpired(false)
                .locked(false)
                .password("password1")
                .roleKeys(Stream.of(role1.getMetadataKey(), role2.getMetadataKey()).collect(Collectors.toSet()))
                .build();
        user2 = User.builder()
                .userKey(userKey2)
                .username("user2")
                .enabled(true)
                .expired(false)
                .credentialsExpired(false)
                .locked(false)
                .password("password3")
                .roleKeys(Stream.of(role1.getMetadataKey()).collect(Collectors.toSet()))
                .build();

        team1 = Team.builder()
                .teamKey(teamKey1)
                .teamName("team1")
                .roles(Stream.of(role1).collect(Collectors.toSet()))
                .securityGroupKeys(Stream.of(securityGroupKey1).collect(Collectors.toSet()))
                .build();
        team2 = Team.builder()
                .teamKey(teamKey2)
                .teamName("team2")
                .roles(Stream.of(role2).collect(Collectors.toSet()))
                .securityGroupKeys(Stream.of(securityGroupKey2).collect(Collectors.toSet()))
                .build();
    }

    @AfterEach
    void clearDatabase() {
        MetadataRepositoryConfiguration.getInstance()
                .getControlMetadataRepository().cleanAllTables();
    }

    @AfterAll
    static void teardown() {
        MetadataRepositoryConfiguration.getInstance()
                .getControlMetadataRepository().dropAllTables();
    }

    @Test
    void userDoesNotExistsTest() {
        assertThat(TeamConfiguration.getInstance().exists(teamKey1)).isFalse();
    }

    @Test
    void userExistsTest() {
        TeamConfiguration.getInstance().insert(team1);
        assertThat(TeamConfiguration.getInstance().exists(teamKey1)).isTrue();
    }

    @Test
    void userGetDoesNotExistsTest() {
        assertThat(TeamConfiguration.getInstance().get(teamKey1)).isEmpty();
        TeamConfiguration.getInstance().insert(team1);
        assertThat(TeamConfiguration.getInstance().get(teamKey2)).isEmpty();
    }

    @Test
    void userGetExistsTest() {
        TeamConfiguration.getInstance().insert(team1);
        assertThat(TeamConfiguration.getInstance().get(teamKey1))
                .isPresent()
                .hasValue(team1);
    }

    @Test
    void userInsertTest() {
        assertThat(TeamConfiguration.getInstance().exists(teamKey1))
                .isFalse();
        TeamConfiguration.getInstance().insert(team1);
        assertThat(TeamConfiguration.getInstance().exists(teamKey1))
                .isTrue();
        assertThat(TeamConfiguration.getInstance().get(teamKey1))
                .isPresent()
                .hasValue(team1);
    }

    @Test
    void userInsertAlreadyExistingTest() {
        TeamConfiguration.getInstance().insert(team1);
        assertThatThrownBy(() -> TeamConfiguration.getInstance().insert(team1))
                .isInstanceOf(RuntimeException.class);
    }

    @Test
    void userInsertMultipleUsersTest() {
        assertThat(TeamConfiguration.getInstance().exists(teamKey1))
                .isFalse();
        TeamConfiguration.getInstance().insert(team1);
        assertThat(TeamConfiguration.getInstance().exists(teamKey1))
                .isTrue();
        assertThat(TeamConfiguration.getInstance().get(teamKey1))
                .isPresent()
                .hasValue(team1);
        TeamConfiguration.getInstance().insert(team2);
        assertThat(TeamConfiguration.getInstance().exists(teamKey2))
                .isTrue();
        assertThat(TeamConfiguration.getInstance().exists(teamKey1))
                .isTrue();
        assertThat(TeamConfiguration.getInstance().get(teamKey2))
                .isPresent()
                .hasValue(team2);
        assertThat(TeamConfiguration.getInstance().get(teamKey1))
                .isPresent()
                .hasValue(team1);
    }

    @Test
    void userDeleteDoesNotExistTest() {
        TeamConfiguration.getInstance().delete(team1.getMetadataKey());
    }

    @Test
    void userDeleteTest() {
        TeamConfiguration.getInstance().insert(team1);
        assertThat(TeamConfiguration.getInstance().exists(teamKey1))
                .isTrue();
        TeamConfiguration.getInstance().delete(team1.getMetadataKey());

        assertThat(TeamConfiguration.getInstance().exists(teamKey1))
                .isFalse();
    }


    @Test
    void userUpdateTest() {
        TeamConfiguration.getInstance().insert(team1);
        Optional<Team> team = TeamConfiguration.getInstance().get(teamKey1);
        assertThat(team)
                .isPresent()
                .hasValue(team1);
        assertThat(team.get().getTeamName())
                .isEqualTo("team1");

        team1.setTeamName("role3");
        TeamConfiguration.getInstance().update(team1);

        assertThat(TeamConfiguration.getInstance().get(teamKey1).get().getTeamName())
                .isEqualTo("role3");
    }

    @Test
    void userUpdateMultipleTest() {
        TeamConfiguration.getInstance().insert(team1);
        TeamConfiguration.getInstance().insert(team2);
        Optional<Team> fetchedteam1 = TeamConfiguration.getInstance().get(teamKey1);
        assertThat(fetchedteam1)
                .isPresent()
                .hasValue(team1);
        assertThat(fetchedteam1.get().getTeamName())
                .isEqualTo("team1");
        Optional<Team> fetchedteam2 = TeamConfiguration.getInstance().get(teamKey2);
        assertThat(fetchedteam2)
                .isPresent()
                .hasValue(team2);
        assertThat(fetchedteam2.get().getTeamName())
                .isEqualTo("team2");

        team1.setTeamName("role3");
        TeamConfiguration.getInstance().update(team1);

        assertThat(TeamConfiguration.getInstance().get(teamKey1).get().getTeamName())
                .isEqualTo("role3");
        assertThat(TeamConfiguration.getInstance().get(teamKey2).get().getTeamName())
                .isEqualTo("team2");
    }

//    @Test
//    void getGroups() {
//        TeamConfiguration.getInstance().insert(user1);
//        TeamConfiguration.getInstance().insert(team2);
//        TeamConfiguration.getInstance().insert(role1);
//        TeamConfiguration.getInstance().insert(role2);
//        TeamConfiguration.getInstance().addUser(role1.getMetadataKey(), user1.getMetadataKey());
//        TeamConfiguration.getInstance().addUser(role2.getMetadataKey(), user1.getMetadataKey());
//        TeamConfiguration.getInstance().addUser(role1.getMetadataKey(), user2.getMetadataKey());
//
//        assertThat(TeamConfiguration.getInstance().getGroups(user1.getMetadataKey()))
//                .containsOnly(role1, role2);
//        assertThat(TeamConfiguration.getInstance().getGroups(user2.getMetadataKey()))
//                .containsOnly(role1);
//    }
//
//    @Test
//    void addAuthority() {
//        TeamConfiguration.getInstance().insert(user1);
//        TeamConfiguration.getInstance().insert(user2);
//        AuthorityConfiguration.getInstance().insert(privilege1);
//        AuthorityConfiguration.getInstance().insert(privilege2);
//        TeamConfiguration.getInstance().addAuthority(user1.getMetadataKey(), privilege1.getMetadataKey());
//        TeamConfiguration.getInstance().addAuthority(user1.getMetadataKey(), privilege2.getMetadataKey());
//        TeamConfiguration.getInstance().addAuthority(user2.getMetadataKey(), privilege2.getMetadataKey());
//        assertThat(TeamConfiguration.getInstance().getAuthorities(user1.getMetadataKey()))
//                .containsOnly(privilege1, privilege2);
//        assertThat(TeamConfiguration.getInstance().getAuthorities(user2.getMetadataKey()))
//                .containsOnly(privilege2);
//    }
//
//    @Test
//    void addAuthorityByName() {
//        TeamConfiguration.getInstance().insert(user1);
//        TeamConfiguration.getInstance().insert(user2);
//        AuthorityConfiguration.getInstance().insert(privilege1);
//        AuthorityConfiguration.getInstance().insert(privilege2);
//        TeamConfiguration.getInstance().addAuthority(user1.getUsername(), privilege1.getPrivilege());
//        TeamConfiguration.getInstance().addAuthority(user1.getUsername(), privilege2.getPrivilege());
//        TeamConfiguration.getInstance().addAuthority(user2.getUsername(), privilege2.getPrivilege());
//        assertThat(TeamConfiguration.getInstance().getAuthorities(user1.getMetadataKey()))
//                .containsOnly(privilege1, privilege2);
//        assertThat(TeamConfiguration.getInstance().getAuthorities(user2.getMetadataKey()))
//                .containsOnly(privilege2);
//    }
//
//    @Test
//    void removeAuthority() {
//        TeamConfiguration.getInstance().insert(user1);
//        TeamConfiguration.getInstance().insert(user2);
//        AuthorityConfiguration.getInstance().insert(privilege1);
//        AuthorityConfiguration.getInstance().insert(privilege2);
//        TeamConfiguration.getInstance().addAuthority(user1.getMetadataKey(), privilege1.getMetadataKey());
//        TeamConfiguration.getInstance().addAuthority(user1.getMetadataKey(), privilege2.getMetadataKey());
//        TeamConfiguration.getInstance().addAuthority(user2.getMetadataKey(), privilege2.getMetadataKey());
//
//        assertThat(TeamConfiguration.getInstance().getAuthorities(user1.getMetadataKey()))
//                .containsOnly(privilege1, privilege2);
//        assertThat(TeamConfiguration.getInstance().getAuthorities(user2.getMetadataKey()))
//                .containsOnly(privilege2);
//
//        TeamConfiguration.getInstance().removeAuthority(user1.getMetadataKey(), privilege1.getMetadataKey());
//
//        assertThat(TeamConfiguration.getInstance().getAuthorities(user1.getMetadataKey()))
//                .containsOnly(privilege2);
//        assertThat(TeamConfiguration.getInstance().getAuthorities(user2.getMetadataKey()))
//                .containsOnly(privilege2);
//    }
//
//    @Test
//    void removeAuthorityByName() {
//        TeamConfiguration.getInstance().insert(user1);
//        TeamConfiguration.getInstance().insert(user2);
//        AuthorityConfiguration.getInstance().insert(privilege1);
//        AuthorityConfiguration.getInstance().insert(privilege2);
//        TeamConfiguration.getInstance().addAuthority(user1.getUsername(), privilege1.getPrivilege());
//        TeamConfiguration.getInstance().addAuthority(user1.getUsername(), privilege2.getPrivilege());
//        TeamConfiguration.getInstance().addAuthority(user2.getUsername(), privilege2.getPrivilege());
//
//        assertThat(TeamConfiguration.getInstance().getAuthorities(user1.getUsername()))
//                .containsOnly(privilege1, privilege2);
//        assertThat(TeamConfiguration.getInstance().getAuthorities(user2.getUsername()))
//                .containsOnly(privilege2);
//
//        TeamConfiguration.getInstance().removeAuthority(user1.getUsername(), privilege1.getPrivilege());
//
//        assertThat(TeamConfiguration.getInstance().getAuthorities(user1.getMetadataKey()))
//                .containsOnly(privilege2);
//        assertThat(TeamConfiguration.getInstance().getAuthorities(user2.getMetadataKey()))
//                .containsOnly(privilege2);
//    }
//
//    @Test
//    void getAuthorities() {
//        TeamConfiguration.getInstance().insert(user1);
//        TeamConfiguration.getInstance().insert(user2);
//        AuthorityConfiguration.getInstance().insert(privilege1);
//        AuthorityConfiguration.getInstance().insert(privilege2);
//        AuthorityConfiguration.getInstance().insert(privilege3);
//        AuthorityConfiguration.getInstance().insert(privilege4);
//        TeamConfiguration.getInstance().insert(role1);
//        TeamConfiguration.getInstance().insert(role2);
//        TeamConfiguration.getInstance().addAuthority(role1.getMetadataKey(), privilege3.getMetadataKey());
//        TeamConfiguration.getInstance().addAuthority(role1.getMetadataKey(), privilege4.getMetadataKey());
//        TeamConfiguration.getInstance().addAuthority(role2.getMetadataKey(), privilege3.getMetadataKey());
//        TeamConfiguration.getInstance().addUser(role1.getMetadataKey(), user1.getMetadataKey());
//        TeamConfiguration.getInstance().addUser(role2.getMetadataKey(), user2.getMetadataKey());
//        TeamConfiguration.getInstance().addAuthority(user1.getMetadataKey(), privilege1.getMetadataKey());
//        TeamConfiguration.getInstance().addAuthority(user1.getMetadataKey(), privilege2.getMetadataKey());
//        TeamConfiguration.getInstance().addAuthority(user2.getMetadataKey(), privilege2.getMetadataKey());
//
//        assertThat(TeamConfiguration.getInstance().getAuthorities(user1.getMetadataKey()))
//                .containsOnly(privilege1, privilege2, privilege3, privilege4);
//        assertThat(TeamConfiguration.getInstance().getAuthorities(user2.getMetadataKey()))
//                .containsOnly(privilege2, privilege3);
//    }
//
//    @Test
//    void getAuthoritiesByName() {
//        TeamConfiguration.getInstance().insert(user1);
//        TeamConfiguration.getInstance().insert(user2);
//        AuthorityConfiguration.getInstance().insert(privilege1);
//        AuthorityConfiguration.getInstance().insert(privilege2);
//        AuthorityConfiguration.getInstance().insert(privilege3);
//        AuthorityConfiguration.getInstance().insert(privilege4);
//        TeamConfiguration.getInstance().insert(role1);
//        TeamConfiguration.getInstance().insert(role2);
//        TeamConfiguration.getInstance().addAuthority(role1.getMetadataKey(), privilege3.getMetadataKey());
//        TeamConfiguration.getInstance().addAuthority(role1.getMetadataKey(), privilege4.getMetadataKey());
//        TeamConfiguration.getInstance().addAuthority(role2.getMetadataKey(), privilege3.getMetadataKey());
//        TeamConfiguration.getInstance().addUser(role1.getMetadataKey(), user1.getMetadataKey());
//        TeamConfiguration.getInstance().addUser(role2.getMetadataKey(), user2.getMetadataKey());
//        TeamConfiguration.getInstance().addAuthority(user1.getMetadataKey(), privilege1.getMetadataKey());
//        TeamConfiguration.getInstance().addAuthority(user1.getMetadataKey(), privilege2.getMetadataKey());
//        TeamConfiguration.getInstance().addAuthority(user2.getMetadataKey(), privilege2.getMetadataKey());
//
//        assertThat(TeamConfiguration.getInstance().getAuthorities(user1.getUsername()))
//                .containsOnly(privilege1, privilege2, privilege3, privilege4);
//        assertThat(TeamConfiguration.getInstance().getAuthorities(user2.getUsername()))
//                .containsOnly(privilege2, privilege3);
//    }


}
