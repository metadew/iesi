package io.metadew.iesi.metadata.configuration.securitygroup;

import io.metadew.iesi.common.configuration.Configuration;
import io.metadew.iesi.common.configuration.metadata.repository.MetadataRepositoryConfiguration;
import io.metadew.iesi.metadata.configuration.security.SecurityGroupConfiguration;
import io.metadew.iesi.metadata.definition.security.SecurityGroup;
import io.metadew.iesi.metadata.definition.security.SecurityGroupKey;
import io.metadew.iesi.metadata.definition.user.Team;
import io.metadew.iesi.metadata.definition.user.TeamKey;
import io.metadew.iesi.metadata.repository.MetadataRepository;
import org.junit.jupiter.api.*;

import java.util.HashSet;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class SecurityGroupConfigurationTest {

    private SecurityGroupKey securityGroupKey1;
    private SecurityGroup securityGroup1;
    private SecurityGroupKey securityGroupKey2;
    private SecurityGroup securityGroup2;


    private TeamKey teamKey1;
    private Team team1;
    private TeamKey teamKey2;
    private Team team2;

    @BeforeAll
    static void prepare() {
        Configuration.getInstance();
        MetadataRepositoryConfiguration.getInstance()
                .getMetadataRepositories()
                .forEach(MetadataRepository::createAllTables);
    }

    @AfterEach
    void clearDatabase() {
        MetadataRepositoryConfiguration.getInstance()
                .getMetadataRepositories()
                .forEach(MetadataRepository::cleanAllTables);
    }

    @AfterAll
    static void teardown() {
        Configuration.getInstance();
        MetadataRepositoryConfiguration.getInstance()
                .getMetadataRepositories()
                .forEach(MetadataRepository::dropAllTables);
    }

    @BeforeEach
    void setup() {
        securityGroupKey1 = new SecurityGroupKey(UUID.randomUUID());
        securityGroupKey2 = new SecurityGroupKey(UUID.randomUUID());
        teamKey1 = new TeamKey(UUID.randomUUID());
        teamKey2 = new TeamKey(UUID.randomUUID());
        SecurityGroup securityGroup1 = new SecurityGroup(securityGroupKey1, "PUBLIC", Stream.of(teamKey1).collect(Collectors.toSet()), new HashSet<>());
        SecurityGroup securityGroup2 = new SecurityGroup(securityGroupKey2, "PRIVATE", Stream.of(teamKey1, teamKey2).collect(Collectors.toSet()), new HashSet<>());
        team1 = Team.builder()
                .teamKey(teamKey1)
                .teamName("team1")
                .securityGroups(Stream.of(securityGroup1, securityGroup2).collect(Collectors.toSet()))
                .build();
        team2 = Team.builder()
                .teamKey(teamKey2)
                .teamName("team2")
                .securityGroups(Stream.of(securityGroup2).collect(Collectors.toSet()))
                .build();
        securityGroup1 = SecurityGroup.builder()
                .metadataKey(securityGroupKey1)
                .name("group1")
                .teamKeys(Stream.of(teamKey1).collect(Collectors.toSet()))
                .securedObjects(new HashSet<>())
                .build();
        securityGroup2 = SecurityGroup.builder()
                .metadataKey(securityGroupKey2)
                .name("group2")
                .teamKeys(Stream.of(teamKey1, teamKey2).collect(Collectors.toSet()))
                .securedObjects(new HashSet<>())
                .build();
    }

    @Test
    void userDoesNotExistsTest() {
        assertThat(SecurityGroupConfiguration.getInstance().exists(securityGroupKey1)).isFalse();
    }

    @Test
    void userExistsTest() {
        SecurityGroupConfiguration.getInstance().insert(securityGroup1);
        assertThat(SecurityGroupConfiguration.getInstance().exists(securityGroupKey1)).isTrue();
    }

    @Test
    void userGetDoesNotExistsTest() {
        assertThat(SecurityGroupConfiguration.getInstance().get(securityGroupKey2)).isEmpty();
        SecurityGroupConfiguration.getInstance().insert(securityGroup1);
        assertThat(SecurityGroupConfiguration.getInstance().get(securityGroupKey2)).isEmpty();
    }

    @Test
    void userGetByNameDoesNotExistsTest() {
        assertThat(SecurityGroupConfiguration.getInstance().getByName(securityGroup1.getName())).isEmpty();
        SecurityGroupConfiguration.getInstance().insert(securityGroup1);
        assertThat(SecurityGroupConfiguration.getInstance().getByName(securityGroup2.getName())).isEmpty();
    }

    @Test
    void userGetExistsTest() {
        SecurityGroupConfiguration.getInstance().insert(securityGroup1);
        assertThat(SecurityGroupConfiguration.getInstance().get(securityGroupKey1))
                .isPresent()
                .hasValue(securityGroup1);
    }

    @Test
    void userGetByNameExistsTest() {
        SecurityGroupConfiguration.getInstance().insert(securityGroup1);
        assertThat(SecurityGroupConfiguration.getInstance().getByName(securityGroup1.getName()))
                .isPresent()
                .hasValue(securityGroup1);
    }

    @Test
    void userInsertTest() {
        assertThat(SecurityGroupConfiguration.getInstance().exists(securityGroupKey1))
                .isFalse();
        SecurityGroupConfiguration.getInstance().insert(securityGroup1);
        assertThat(SecurityGroupConfiguration.getInstance().exists(securityGroupKey1))
                .isTrue();
        assertThat(SecurityGroupConfiguration.getInstance().get(securityGroupKey1))
                .isPresent()
                .hasValue(securityGroup1);
    }

    @Test
    void userInsertAlreadyExistingTest() {
        SecurityGroupConfiguration.getInstance().insert(securityGroup1);
        assertThatThrownBy(() -> SecurityGroupConfiguration.getInstance().insert(securityGroup1))
                .isInstanceOf(RuntimeException.class);
    }

    @Test
    void userInsertMultipleUsersTest() {
        assertThat(SecurityGroupConfiguration.getInstance().exists(securityGroupKey1))
                .isFalse();
        SecurityGroupConfiguration.getInstance().insert(securityGroup1);
        assertThat(SecurityGroupConfiguration.getInstance().exists(securityGroupKey1))
                .isTrue();
        assertThat(SecurityGroupConfiguration.getInstance().get(securityGroupKey1))
                .isPresent()
                .hasValue(securityGroup1);
        SecurityGroupConfiguration.getInstance().insert(securityGroup2);
        assertThat(SecurityGroupConfiguration.getInstance().exists(securityGroupKey2))
                .isTrue();
        assertThat(SecurityGroupConfiguration.getInstance().exists(securityGroupKey1))
                .isTrue();
        assertThat(SecurityGroupConfiguration.getInstance().get(securityGroupKey2))
                .isPresent()
                .hasValue(securityGroup2);
        assertThat(SecurityGroupConfiguration.getInstance().get(securityGroupKey1))
                .isPresent()
                .hasValue(securityGroup1);
    }

    @Test
    void userDeleteDoesNotExistTest() {
        SecurityGroupConfiguration.getInstance().delete(securityGroup1.getMetadataKey());
    }

    @Test
    void userDeleteTest() {
        SecurityGroupConfiguration.getInstance().insert(securityGroup1);
        assertThat(SecurityGroupConfiguration.getInstance().exists(securityGroupKey1))
                .isTrue();
        SecurityGroupConfiguration.getInstance().delete(securityGroup1.getMetadataKey());

        assertThat(SecurityGroupConfiguration.getInstance().exists(securityGroupKey1))
                .isFalse();
    }

    @Test
    void userUpdateTest() {
        SecurityGroupConfiguration.getInstance().insert(securityGroup1);
        Optional<SecurityGroup> user = SecurityGroupConfiguration.getInstance().get(securityGroupKey1);
        assertThat(user)
                .isPresent()
                .hasValue(securityGroup1);
        assertThat(user.get().getName())
                .isEqualTo("group1");

        securityGroup1.setName("group3");
        SecurityGroupConfiguration.getInstance().update(securityGroup1);

        assertThat(SecurityGroupConfiguration.getInstance().get(securityGroupKey1).get().getName())
                .isEqualTo("group3");
    }

    @Test
    void userUpdateMultipleTest() {
        SecurityGroupConfiguration.getInstance().insert(securityGroup1);
        SecurityGroupConfiguration.getInstance().insert(securityGroup2);
        Optional<SecurityGroup> fetchedUser1 = SecurityGroupConfiguration.getInstance().get(securityGroupKey1);
        assertThat(fetchedUser1)
                .isPresent()
                .hasValue(securityGroup1);
        assertThat(fetchedUser1.get().getName())
                .isEqualTo("group1");
        Optional<SecurityGroup> fetchedUser2 = SecurityGroupConfiguration.getInstance().get(securityGroupKey2);
        assertThat(fetchedUser2)
                .isPresent()
                .hasValue(securityGroup2);
        assertThat(fetchedUser2.get().getName())
                .isEqualTo("group2");

        securityGroup1.setName("group3");
        SecurityGroupConfiguration.getInstance().update(securityGroup1);

        assertThat(SecurityGroupConfiguration.getInstance().get(securityGroupKey1).get().getName())
                .isEqualTo("group3");
        assertThat(SecurityGroupConfiguration.getInstance().get(securityGroupKey2).get().getName())
                .isEqualTo("group2");
    }

//    @Test
//    void getGroups() {
//        SecurityGroupConfiguration.getInstance().insert(user1);
//        SecurityGroupConfiguration.getInstance().insert(user2);
//        RoleConfiguration.getInstance().insert(role1);
//        RoleConfiguration.getInstance().insert(role2);
//        TeamConfiguration.getInstance().addUser(role1.getMetadataKey(), user1.getMetadataKey());
//        TeamConfiguration.getInstance().addUser(role2.getMetadataKey(), user1.getMetadataKey());
//        TeamConfiguration.getInstance().addUser(role1.getMetadataKey(), user2.getMetadataKey());
//
//        assertThat(SecurityGroupConfiguration.getInstance().getGroups(user1.getMetadataKey()))
//                .containsOnly(role1, role2);
//        assertThat(SecurityGroupConfiguration.getInstance().getGroups(user2.getMetadataKey()))
//                .containsOnly(role1);
//    }
//
//    @Test
//    void addAuthority() {
//        SecurityGroupConfiguration.getInstance().insert(user1);
//        SecurityGroupConfiguration.getInstance().insert(user2);
//        AuthorityConfiguration.getInstance().insert(privilege1);
//        AuthorityConfiguration.getInstance().insert(privilege2);
//        SecurityGroupConfiguration.getInstance().addAuthority(user1.getMetadataKey(), privilege1.getMetadataKey());
//        SecurityGroupConfiguration.getInstance().addAuthority(user1.getMetadataKey(), privilege2.getMetadataKey());
//        SecurityGroupConfiguration.getInstance().addAuthority(user2.getMetadataKey(), privilege2.getMetadataKey());
//        assertThat(SecurityGroupConfiguration.getInstance().getAuthorities(user1.getMetadataKey()))
//                .containsOnly(privilege1, privilege2);
//        assertThat(SecurityGroupConfiguration.getInstance().getAuthorities(user2.getMetadataKey()))
//                .containsOnly(privilege2);
//    }
//
//    @Test
//    void addAuthorityByName() {
//        SecurityGroupConfiguration.getInstance().insert(user1);
//        SecurityGroupConfiguration.getInstance().insert(user2);
//        AuthorityConfiguration.getInstance().insert(privilege1);
//        AuthorityConfiguration.getInstance().insert(privilege2);
//        SecurityGroupConfiguration.getInstance().addAuthority(user1.getUsername(), privilege1.getPrivilege());
//        SecurityGroupConfiguration.getInstance().addAuthority(user1.getUsername(), privilege2.getPrivilege());
//        SecurityGroupConfiguration.getInstance().addAuthority(user2.getUsername(), privilege2.getPrivilege());
//        assertThat(SecurityGroupConfiguration.getInstance().getAuthorities(user1.getMetadataKey()))
//                .containsOnly(privilege1, privilege2);
//        assertThat(SecurityGroupConfiguration.getInstance().getAuthorities(user2.getMetadataKey()))
//                .containsOnly(privilege2);
//    }
//
//    @Test
//    void removeAuthority() {
//        SecurityGroupConfiguration.getInstance().insert(user1);
//        SecurityGroupConfiguration.getInstance().insert(user2);
//        AuthorityConfiguration.getInstance().insert(privilege1);
//        AuthorityConfiguration.getInstance().insert(privilege2);
//        SecurityGroupConfiguration.getInstance().addAuthority(user1.getMetadataKey(), privilege1.getMetadataKey());
//        SecurityGroupConfiguration.getInstance().addAuthority(user1.getMetadataKey(), privilege2.getMetadataKey());
//        SecurityGroupConfiguration.getInstance().addAuthority(user2.getMetadataKey(), privilege2.getMetadataKey());
//
//        assertThat(SecurityGroupConfiguration.getInstance().getAuthorities(user1.getMetadataKey()))
//                .containsOnly(privilege1, privilege2);
//        assertThat(SecurityGroupConfiguration.getInstance().getAuthorities(user2.getMetadataKey()))
//                .containsOnly(privilege2);
//
//        SecurityGroupConfiguration.getInstance().removeAuthority(user1.getMetadataKey(), privilege1.getMetadataKey());
//
//        assertThat(SecurityGroupConfiguration.getInstance().getAuthorities(user1.getMetadataKey()))
//                .containsOnly(privilege2);
//        assertThat(SecurityGroupConfiguration.getInstance().getAuthorities(user2.getMetadataKey()))
//                .containsOnly(privilege2);
//    }
//
//    @Test
//    void removeAuthorityByName() {
//        SecurityGroupConfiguration.getInstance().insert(user1);
//        SecurityGroupConfiguration.getInstance().insert(user2);
//        AuthorityConfiguration.getInstance().insert(privilege1);
//        AuthorityConfiguration.getInstance().insert(privilege2);
//        SecurityGroupConfiguration.getInstance().addAuthority(user1.getUsername(), privilege1.getPrivilege());
//        SecurityGroupConfiguration.getInstance().addAuthority(user1.getUsername(), privilege2.getPrivilege());
//        SecurityGroupConfiguration.getInstance().addAuthority(user2.getUsername(), privilege2.getPrivilege());
//
//        assertThat(SecurityGroupConfiguration.getInstance().getAuthorities(user1.getUsername()))
//                .containsOnly(privilege1, privilege2);
//        assertThat(SecurityGroupConfiguration.getInstance().getAuthorities(user2.getUsername()))
//                .containsOnly(privilege2);
//
//        SecurityGroupConfiguration.getInstance().removeAuthority(user1.getUsername(), privilege1.getPrivilege());
//
//        assertThat(SecurityGroupConfiguration.getInstance().getAuthorities(user1.getMetadataKey()))
//                .containsOnly(privilege2);
//        assertThat(SecurityGroupConfiguration.getInstance().getAuthorities(user2.getMetadataKey()))
//                .containsOnly(privilege2);
//    }
//
//    @Test
//    void getAuthorities() {
//        SecurityGroupConfiguration.getInstance().insert(user1);
//        SecurityGroupConfiguration.getInstance().insert(user2);
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
//        SecurityGroupConfiguration.getInstance().addAuthority(user1.getMetadataKey(), privilege1.getMetadataKey());
//        SecurityGroupConfiguration.getInstance().addAuthority(user1.getMetadataKey(), privilege2.getMetadataKey());
//        SecurityGroupConfiguration.getInstance().addAuthority(user2.getMetadataKey(), privilege2.getMetadataKey());
//
//        assertThat(SecurityGroupConfiguration.getInstance().getAuthorities(user1.getMetadataKey()))
//                .containsOnly(privilege1, privilege2, privilege3, privilege4);
//        assertThat(SecurityGroupConfiguration.getInstance().getAuthorities(user2.getMetadataKey()))
//                .containsOnly(privilege2, privilege3);
//    }
//
//    @Test
//    void getAuthoritiesByName() {
//        SecurityGroupConfiguration.getInstance().insert(user1);
//        SecurityGroupConfiguration.getInstance().insert(user2);
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
//        SecurityGroupConfiguration.getInstance().addAuthority(user1.getMetadataKey(), privilege1.getMetadataKey());
//        SecurityGroupConfiguration.getInstance().addAuthority(user1.getMetadataKey(), privilege2.getMetadataKey());
//        SecurityGroupConfiguration.getInstance().addAuthority(user2.getMetadataKey(), privilege2.getMetadataKey());
//
//        assertThat(SecurityGroupConfiguration.getInstance().getAuthorities(user1.getUsername()))
//                .containsOnly(privilege1, privilege2, privilege3, privilege4);
//        assertThat(SecurityGroupConfiguration.getInstance().getAuthorities(user2.getUsername()))
//                .containsOnly(privilege2, privilege3);
//    }


}
