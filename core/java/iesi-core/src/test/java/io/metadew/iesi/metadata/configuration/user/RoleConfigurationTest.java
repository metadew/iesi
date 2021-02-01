package io.metadew.iesi.metadata.configuration.user;

import io.metadew.iesi.common.configuration.Configuration;
import io.metadew.iesi.common.configuration.metadata.repository.MetadataRepositoryConfiguration;
import io.metadew.iesi.metadata.definition.user.*;
import io.metadew.iesi.metadata.repository.MetadataRepository;
import org.junit.jupiter.api.*;

import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class RoleConfigurationTest {

    private TeamKey teamKey1;
    private Team team1;
    private TeamKey teamKey2;
    private Team team2;

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
        teamKey1 = new TeamKey(UUID.randomUUID());
        teamKey2 = new TeamKey(UUID.randomUUID());
        roleKey1 = new RoleKey(UUID.randomUUID());
        roleKey2 = new RoleKey(UUID.randomUUID());
        userKey1 = new UserKey(UUID.randomUUID());
        userKey2 = new UserKey(UUID.randomUUID());

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
    }

    @Test
    void userDoesNotExistsTest() {
        assertThat(RoleConfiguration.getInstance().exists(roleKey1)).isFalse();
    }

    @Test
    void userExistsTest() {
        RoleConfiguration.getInstance().insert(role1);
        assertThat(RoleConfiguration.getInstance().exists(roleKey1)).isTrue();
    }

    @Test
    void userGetDoesNotExistsTest() {
        assertThat(RoleConfiguration.getInstance().get(roleKey1)).isEmpty();
        RoleConfiguration.getInstance().insert(role1);
        assertThat(RoleConfiguration.getInstance().get(roleKey2)).isEmpty();
    }

    @Test
    void userGetExistsTest() {
        RoleConfiguration.getInstance().insert(role1);
        assertThat(RoleConfiguration.getInstance().get(roleKey1))
                .isPresent()
                .hasValue(role1);
    }

    @Test
    void userInsertTest() {
        assertThat(RoleConfiguration.getInstance().exists(roleKey1))
                .isFalse();
        RoleConfiguration.getInstance().insert(role1);
        assertThat(RoleConfiguration.getInstance().exists(roleKey1))
                .isTrue();
        assertThat(RoleConfiguration.getInstance().get(roleKey1))
                .isPresent()
                .hasValue(role1);
    }

    @Test
    void userInsertAlreadyExistingTest() {
        RoleConfiguration.getInstance().insert(role1);
        assertThatThrownBy(() -> RoleConfiguration.getInstance().insert(role1))
                .isInstanceOf(RuntimeException.class);
    }

    @Test
    void userInsertMultipleUsersTest() {
        assertThat(RoleConfiguration.getInstance().exists(roleKey1))
                .isFalse();
        RoleConfiguration.getInstance().insert(role1);
        assertThat(RoleConfiguration.getInstance().exists(roleKey1))
                .isTrue();
        assertThat(RoleConfiguration.getInstance().get(roleKey1))
                .isPresent()
                .hasValue(role1);
        RoleConfiguration.getInstance().insert(role2);
        assertThat(RoleConfiguration.getInstance().exists(roleKey2))
                .isTrue();
        assertThat(RoleConfiguration.getInstance().exists(roleKey1))
                .isTrue();
        assertThat(RoleConfiguration.getInstance().get(roleKey2))
                .isPresent()
                .hasValue(role2);
        assertThat(RoleConfiguration.getInstance().get(roleKey1))
                .isPresent()
                .hasValue(role1);
    }

    @Test
    void userDeleteDoesNotExistTest() {
        RoleConfiguration.getInstance().delete(role1.getMetadataKey());
    }

    @Test
    void userDeleteTest() {
        RoleConfiguration.getInstance().insert(role1);
        assertThat(RoleConfiguration.getInstance().exists(roleKey1))
                .isTrue();
        RoleConfiguration.getInstance().delete(role1.getMetadataKey());

        assertThat(RoleConfiguration.getInstance().exists(roleKey1))
                .isFalse();
    }


    @Test
    void userUpdateTest() {
        RoleConfiguration.getInstance().insert(role1);
        Optional<Role> user = RoleConfiguration.getInstance().get(roleKey1);
        assertThat(user)
                .isPresent()
                .hasValue(role1);
        assertThat(user.get().getName())
                .isEqualTo("role1");

        role1.setName("role3");
        RoleConfiguration.getInstance().update(role1);

        assertThat(RoleConfiguration.getInstance().get(roleKey1).get().getName())
                .isEqualTo("role3");
    }

    @Test
    void userUpdateMultipleTest() {
        RoleConfiguration.getInstance().insert(role1);
        RoleConfiguration.getInstance().insert(role2);
        Optional<Role> fetchedrole1 = RoleConfiguration.getInstance().get(roleKey1);
        assertThat(fetchedrole1)
                .isPresent()
                .hasValue(role1);
        assertThat(fetchedrole1.get().getName())
                .isEqualTo("role1");
        Optional<Role> fetchedrole2 = RoleConfiguration.getInstance().get(roleKey2);
        assertThat(fetchedrole2)
                .isPresent()
                .hasValue(role2);
        assertThat(fetchedrole2.get().getName())
                .isEqualTo("role2");

        role1.setName("role3");
        RoleConfiguration.getInstance().update(role1);

        assertThat(RoleConfiguration.getInstance().get(roleKey1).get().getName())
                .isEqualTo("role3");
        assertThat(RoleConfiguration.getInstance().get(roleKey2).get().getName())
                .isEqualTo("role2");
    }

//    @Test
//    void getGroups() {
//        RoleConfiguration.getInstance().insert(user1);
//        RoleConfiguration.getInstance().insert(role2);
//        RoleConfiguration.getInstance().insert(role1);
//        RoleConfiguration.getInstance().insert(role2);
//        TeamConfiguration.getInstance().addUser(role1.getMetadataKey(), user1.getMetadataKey());
//        TeamConfiguration.getInstance().addUser(role2.getMetadataKey(), user1.getMetadataKey());
//        TeamConfiguration.getInstance().addUser(role1.getMetadataKey(), user2.getMetadataKey());
//
//        assertThat(RoleConfiguration.getInstance().getGroups(user1.getMetadataKey()))
//                .containsOnly(role1, role2);
//        assertThat(RoleConfiguration.getInstance().getGroups(user2.getMetadataKey()))
//                .containsOnly(role1);
//    }
//
//    @Test
//    void addAuthority() {
//        RoleConfiguration.getInstance().insert(user1);
//        RoleConfiguration.getInstance().insert(user2);
//        AuthorityConfiguration.getInstance().insert(privilege1);
//        AuthorityConfiguration.getInstance().insert(privilege2);
//        RoleConfiguration.getInstance().addAuthority(user1.getMetadataKey(), privilege1.getMetadataKey());
//        RoleConfiguration.getInstance().addAuthority(user1.getMetadataKey(), privilege2.getMetadataKey());
//        RoleConfiguration.getInstance().addAuthority(user2.getMetadataKey(), privilege2.getMetadataKey());
//        assertThat(RoleConfiguration.getInstance().getAuthorities(user1.getMetadataKey()))
//                .containsOnly(privilege1, privilege2);
//        assertThat(RoleConfiguration.getInstance().getAuthorities(user2.getMetadataKey()))
//                .containsOnly(privilege2);
//    }
//
//    @Test
//    void addAuthorityByName() {
//        RoleConfiguration.getInstance().insert(user1);
//        RoleConfiguration.getInstance().insert(user2);
//        AuthorityConfiguration.getInstance().insert(privilege1);
//        AuthorityConfiguration.getInstance().insert(privilege2);
//        RoleConfiguration.getInstance().addAuthority(user1.getUsername(), privilege1.getPrivilege());
//        RoleConfiguration.getInstance().addAuthority(user1.getUsername(), privilege2.getPrivilege());
//        RoleConfiguration.getInstance().addAuthority(user2.getUsername(), privilege2.getPrivilege());
//        assertThat(RoleConfiguration.getInstance().getAuthorities(user1.getMetadataKey()))
//                .containsOnly(privilege1, privilege2);
//        assertThat(RoleConfiguration.getInstance().getAuthorities(user2.getMetadataKey()))
//                .containsOnly(privilege2);
//    }
//
//    @Test
//    void removeAuthority() {
//        RoleConfiguration.getInstance().insert(user1);
//        RoleConfiguration.getInstance().insert(user2);
//        AuthorityConfiguration.getInstance().insert(privilege1);
//        AuthorityConfiguration.getInstance().insert(privilege2);
//        RoleConfiguration.getInstance().addAuthority(user1.getMetadataKey(), privilege1.getMetadataKey());
//        RoleConfiguration.getInstance().addAuthority(user1.getMetadataKey(), privilege2.getMetadataKey());
//        RoleConfiguration.getInstance().addAuthority(user2.getMetadataKey(), privilege2.getMetadataKey());
//
//        assertThat(RoleConfiguration.getInstance().getAuthorities(user1.getMetadataKey()))
//                .containsOnly(privilege1, privilege2);
//        assertThat(RoleConfiguration.getInstance().getAuthorities(user2.getMetadataKey()))
//                .containsOnly(privilege2);
//
//        RoleConfiguration.getInstance().removeAuthority(user1.getMetadataKey(), privilege1.getMetadataKey());
//
//        assertThat(RoleConfiguration.getInstance().getAuthorities(user1.getMetadataKey()))
//                .containsOnly(privilege2);
//        assertThat(RoleConfiguration.getInstance().getAuthorities(user2.getMetadataKey()))
//                .containsOnly(privilege2);
//    }
//
//    @Test
//    void removeAuthorityByName() {
//        RoleConfiguration.getInstance().insert(user1);
//        RoleConfiguration.getInstance().insert(user2);
//        AuthorityConfiguration.getInstance().insert(privilege1);
//        AuthorityConfiguration.getInstance().insert(privilege2);
//        RoleConfiguration.getInstance().addAuthority(user1.getUsername(), privilege1.getPrivilege());
//        RoleConfiguration.getInstance().addAuthority(user1.getUsername(), privilege2.getPrivilege());
//        RoleConfiguration.getInstance().addAuthority(user2.getUsername(), privilege2.getPrivilege());
//
//        assertThat(RoleConfiguration.getInstance().getAuthorities(user1.getUsername()))
//                .containsOnly(privilege1, privilege2);
//        assertThat(RoleConfiguration.getInstance().getAuthorities(user2.getUsername()))
//                .containsOnly(privilege2);
//
//        RoleConfiguration.getInstance().removeAuthority(user1.getUsername(), privilege1.getPrivilege());
//
//        assertThat(RoleConfiguration.getInstance().getAuthorities(user1.getMetadataKey()))
//                .containsOnly(privilege2);
//        assertThat(RoleConfiguration.getInstance().getAuthorities(user2.getMetadataKey()))
//                .containsOnly(privilege2);
//    }
//
//    @Test
//    void getAuthorities() {
//        RoleConfiguration.getInstance().insert(user1);
//        RoleConfiguration.getInstance().insert(user2);
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
//        RoleConfiguration.getInstance().addAuthority(user1.getMetadataKey(), privilege1.getMetadataKey());
//        RoleConfiguration.getInstance().addAuthority(user1.getMetadataKey(), privilege2.getMetadataKey());
//        RoleConfiguration.getInstance().addAuthority(user2.getMetadataKey(), privilege2.getMetadataKey());
//
//        assertThat(RoleConfiguration.getInstance().getAuthorities(user1.getMetadataKey()))
//                .containsOnly(privilege1, privilege2, privilege3, privilege4);
//        assertThat(RoleConfiguration.getInstance().getAuthorities(user2.getMetadataKey()))
//                .containsOnly(privilege2, privilege3);
//    }
//
//    @Test
//    void getAuthoritiesByName() {
//        RoleConfiguration.getInstance().insert(user1);
//        RoleConfiguration.getInstance().insert(user2);
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
//        RoleConfiguration.getInstance().addAuthority(user1.getMetadataKey(), privilege1.getMetadataKey());
//        RoleConfiguration.getInstance().addAuthority(user1.getMetadataKey(), privilege2.getMetadataKey());
//        RoleConfiguration.getInstance().addAuthority(user2.getMetadataKey(), privilege2.getMetadataKey());
//
//        assertThat(RoleConfiguration.getInstance().getAuthorities(user1.getUsername()))
//                .containsOnly(privilege1, privilege2, privilege3, privilege4);
//        assertThat(RoleConfiguration.getInstance().getAuthorities(user2.getUsername()))
//                .containsOnly(privilege2, privilege3);
//    }


}
