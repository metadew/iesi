package io.metadew.iesi.metadata.configuration.user;

import io.metadew.iesi.common.configuration.Configuration;
import io.metadew.iesi.common.configuration.metadata.repository.MetadataRepositoryConfiguration;
import io.metadew.iesi.metadata.definition.user.*;
<<<<<<< HEAD
=======
import io.metadew.iesi.metadata.repository.MetadataRepository;
>>>>>>> master
import org.junit.jupiter.api.*;

import java.util.Optional;
import java.util.UUID;
<<<<<<< HEAD
=======
import java.util.stream.Collectors;
import java.util.stream.Stream;
>>>>>>> master

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class UserConfigurationTest {

    private UUID uuid1;
    private User user1;
    private UUID uuid2;
    private User user2;


<<<<<<< HEAD
    private Group group1;
    private Group group2;

    private Authority authority1;
    private Authority authority2;
    private Authority authority3;
    private Authority authority4;
=======
    private Role role1;
    private Role role2;

    private Privilege privilege1;
    private Privilege privilege2;
    private Privilege privilege3;
    private Privilege privilege4;
>>>>>>> master

    @BeforeAll
    static void prepare() {
        Configuration.getInstance();
        MetadataRepositoryConfiguration.getInstance()
<<<<<<< HEAD
                .getControlMetadataRepository()
                .createAllTables();
=======
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
>>>>>>> master
    }

    @BeforeEach
    void setup() {
<<<<<<< HEAD
=======

        privilege1 = Privilege.builder()
                .privilegeKey(PrivilegeKey.builder()
                        .uuid(UUID.randomUUID())
                        .build())
                .privilege("authority1")
                .build();
        privilege2 = Privilege.builder()
                .privilegeKey(PrivilegeKey.builder()
                        .uuid(UUID.randomUUID())
                        .build())
                .privilege("authority2")
                .build();
        privilege3 = Privilege.builder()
                .privilegeKey(PrivilegeKey.builder()
                        .uuid(UUID.randomUUID())
                        .build())
                .privilege("authority3")
                .build();
        privilege4 = Privilege.builder()
                .privilegeKey(PrivilegeKey.builder()
                        .uuid(UUID.randomUUID())
                        .build())
                .privilege("authority4")
                .build();
        role1 = Role.builder()
                .metadataKey(new RoleKey(UUID.randomUUID()))
                .teamKey(TeamKey.builder()
                        .uuid(UUID.randomUUID())
                        .build())
                .name("group1")
                .privileges(Stream.of(privilege1, privilege2).collect(Collectors.toSet()))
                .build();
        role2 = Role.builder()
                .metadataKey(new RoleKey(UUID.randomUUID()))
                .teamKey(TeamKey.builder()
                        .uuid(UUID.randomUUID())
                        .build())
                .name("group2")
                .privileges(Stream.of(privilege1, privilege2).collect(Collectors.toSet()))
                .build();
>>>>>>> master
        uuid1 = UUID.randomUUID();
        user1 = User.builder()
                .userKey(UserKey.builder()
                        .uuid(uuid1)
                        .build())
                .username("user1")
                .enabled(true)
                .expired(false)
                .credentialsExpired(false)
                .locked(false)
<<<<<<< HEAD
                .password("password1").build();
=======
                .password("password1")
                .roleKeys(Stream.of(role1.getMetadataKey(), role2.getMetadataKey()).collect(Collectors.toSet()))
                .build();
>>>>>>> master
        uuid2 = UUID.randomUUID();
        user2 = User.builder()
                .userKey(UserKey.builder()
                        .uuid(uuid2)
                        .build())
                .username("user2")
                .enabled(true)
                .expired(false)
                .credentialsExpired(false)
                .locked(false)
                .password("password3")
<<<<<<< HEAD
                .build();
        authority1 = Authority.builder()
                .authorityKey(AuthorityKey.builder()
                        .uuid(UUID.randomUUID())
                        .build())
                .authority("authority1")
                .build();
        authority2 = Authority.builder()
                .authorityKey(AuthorityKey.builder()
                        .uuid(UUID.randomUUID())
                        .build())
                .authority("authority2")
                .build();
        authority3 = Authority.builder()
                .authorityKey(AuthorityKey.builder()
                        .uuid(UUID.randomUUID())
                        .build())
                .authority("authority3")
                .build();
        authority4 = Authority.builder()
                .authorityKey(AuthorityKey.builder()
                        .uuid(UUID.randomUUID())
                        .build())
                .authority("authority4")
                .build();
        group1 = Group.builder()
                .groupKey(GroupKey.builder()
                        .uuid(UUID.randomUUID())
                        .build())
                .groupName("group1")
                .build();
        group2 = Group.builder()
                .groupKey(GroupKey.builder()
                        .uuid(UUID.randomUUID())
                        .build())
                .groupName("group2")
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
=======
                .roleKeys(Stream.of(role1.getMetadataKey()).collect(Collectors.toSet()))
                .build();
>>>>>>> master
    }

    @Test
    void userDoesNotExistsTest() {
        assertThat(UserConfiguration.getInstance().exists(new UserKey(uuid1))).isFalse();
    }

    @Test
    void userExistsTest() {
        UserConfiguration.getInstance().insert(user1);
        assertThat(UserConfiguration.getInstance().exists(new UserKey(uuid1))).isTrue();
    }

    @Test
    void userExistsByNameTest() {
        UserConfiguration.getInstance().insert(user1);
        assertThat(UserConfiguration.getInstance().exists(user1.getUsername())).isTrue();
    }

    @Test
    void userGetDoesNotExistsTest() {
        assertThat(UserConfiguration.getInstance().get(new UserKey(uuid1))).isEmpty();
        UserConfiguration.getInstance().insert(user1);
        assertThat(UserConfiguration.getInstance().get(new UserKey(uuid2))).isEmpty();
    }

    @Test
    void userGetByNameDoesNotExistsTest() {
<<<<<<< HEAD
        assertThat(UserConfiguration.getInstance().get(user1.getUsername())).isEmpty();
        UserConfiguration.getInstance().insert(user1);
        assertThat(UserConfiguration.getInstance().get(user2.getUsername())).isEmpty();
=======
        assertThat(UserConfiguration.getInstance().getByName(user1.getUsername())).isEmpty();
        UserConfiguration.getInstance().insert(user1);
        assertThat(UserConfiguration.getInstance().getByName(user2.getUsername())).isEmpty();
>>>>>>> master
    }

    @Test
    void userGetExistsTest() {
        UserConfiguration.getInstance().insert(user1);
        assertThat(UserConfiguration.getInstance().get(new UserKey(uuid1)))
                .isPresent()
                .hasValue(user1);
    }

    @Test
    void userGetByNameExistsTest() {
        UserConfiguration.getInstance().insert(user1);
<<<<<<< HEAD
        assertThat(UserConfiguration.getInstance().get(user1.getUsername()))
=======
        assertThat(UserConfiguration.getInstance().getByName(user1.getUsername()))
>>>>>>> master
                .isPresent()
                .hasValue(user1);
    }

    @Test
    void userInsertTest() {
        assertThat(UserConfiguration.getInstance().exists(new UserKey(uuid1)))
                .isFalse();
        UserConfiguration.getInstance().insert(user1);
        assertThat(UserConfiguration.getInstance().exists(new UserKey(uuid1)))
                .isTrue();
        assertThat(UserConfiguration.getInstance().get(new UserKey(uuid1)))
                .isPresent()
                .hasValue(user1);
    }

    @Test
    void userInsertAlreadyExistingTest() {
        UserConfiguration.getInstance().insert(user1);
        assertThatThrownBy(() -> UserConfiguration.getInstance().insert(user1))
                .isInstanceOf(RuntimeException.class);
    }

    @Test
    void userInsertMultipleUsersTest() {
        assertThat(UserConfiguration.getInstance().exists(new UserKey(uuid1)))
                .isFalse();
        UserConfiguration.getInstance().insert(user1);
        assertThat(UserConfiguration.getInstance().exists(new UserKey(uuid1)))
                .isTrue();
        assertThat(UserConfiguration.getInstance().get(new UserKey(uuid1)))
                .isPresent()
                .hasValue(user1);
        UserConfiguration.getInstance().insert(user2);
        assertThat(UserConfiguration.getInstance().exists(new UserKey(uuid2)))
                .isTrue();
        assertThat(UserConfiguration.getInstance().exists(new UserKey(uuid1)))
                .isTrue();
        assertThat(UserConfiguration.getInstance().get(new UserKey(uuid2)))
                .isPresent()
                .hasValue(user2);
        assertThat(UserConfiguration.getInstance().get(new UserKey(uuid1)))
                .isPresent()
                .hasValue(user1);
    }

    @Test
    void userDeleteDoesNotExistTest() {
        UserConfiguration.getInstance().delete(user1.getMetadataKey());
    }

    @Test
    void userDeleteByNameDoesNotExistTest() {
        UserConfiguration.getInstance().delete(user1.getUsername());
    }

    @Test
    void userDeleteTest() {
        UserConfiguration.getInstance().insert(user1);
        assertThat(UserConfiguration.getInstance().exists(new UserKey(uuid1)))
                .isTrue();
        UserConfiguration.getInstance().delete(user1.getMetadataKey());

        assertThat(UserConfiguration.getInstance().exists(new UserKey(uuid1)))
                .isFalse();
    }

    @Test
    void userDeleteByNameTest() {
        UserConfiguration.getInstance().insert(user1);
        assertThat(UserConfiguration.getInstance().exists(new UserKey(uuid1)))
                .isTrue();
        UserConfiguration.getInstance().delete(user1.getUsername());

        assertThat(UserConfiguration.getInstance().exists(new UserKey(uuid1)))
                .isFalse();
    }

    @Test
    void userUpdateTest() {
        UserConfiguration.getInstance().insert(user1);
        Optional<User> user = UserConfiguration.getInstance().get(new UserKey(uuid1));
        assertThat(user)
                .isPresent()
                .hasValue(user1);
        assertThat(user.get().getUsername())
                .isEqualTo("user1");

        user1.setUsername("userA");
        UserConfiguration.getInstance().update(user1);

        assertThat(UserConfiguration.getInstance().get(new UserKey(uuid1)).get().getUsername())
                .isEqualTo("userA");
    }

    @Test
    void userUpdateMultipleTest() {
        UserConfiguration.getInstance().insert(user1);
        UserConfiguration.getInstance().insert(user2);
        Optional<User> fetchedUser1 = UserConfiguration.getInstance().get(new UserKey(uuid1));
        assertThat(fetchedUser1)
                .isPresent()
                .hasValue(user1);
        assertThat(fetchedUser1.get().getUsername())
                .isEqualTo("user1");
        Optional<User> fetchedUser2 = UserConfiguration.getInstance().get(new UserKey(uuid2));
        assertThat(fetchedUser2)
                .isPresent()
                .hasValue(user2);
        assertThat(fetchedUser2.get().getUsername())
                .isEqualTo("user2");

        user1.setUsername("userA");
        UserConfiguration.getInstance().update(user1);

        assertThat(UserConfiguration.getInstance().get(new UserKey(uuid1)).get().getUsername())
                .isEqualTo("userA");
        assertThat(UserConfiguration.getInstance().get(new UserKey(uuid2)).get().getUsername())
                .isEqualTo("user2");
    }

<<<<<<< HEAD
    @Test
    void getGroups() {
        UserConfiguration.getInstance().insert(user1);
        UserConfiguration.getInstance().insert(user2);
        GroupConfiguration.getInstance().insert(group1);
        GroupConfiguration.getInstance().insert(group2);
        GroupConfiguration.getInstance().addUser(group1.getMetadataKey(), user1.getMetadataKey());
        GroupConfiguration.getInstance().addUser(group2.getMetadataKey(), user1.getMetadataKey());
        GroupConfiguration.getInstance().addUser(group1.getMetadataKey(), user2.getMetadataKey());

        assertThat(UserConfiguration.getInstance().getGroups(user1.getMetadataKey()))
                .containsOnly(group1, group2);
        assertThat(UserConfiguration.getInstance().getGroups(user2.getMetadataKey()))
                .containsOnly(group1);
    }

    @Test
    void addAuthority() {
        UserConfiguration.getInstance().insert(user1);
        UserConfiguration.getInstance().insert(user2);
        AuthorityConfiguration.getInstance().insert(authority1);
        AuthorityConfiguration.getInstance().insert(authority2);
        UserConfiguration.getInstance().addAuthority(user1.getMetadataKey(), authority1.getMetadataKey());
        UserConfiguration.getInstance().addAuthority(user1.getMetadataKey(), authority2.getMetadataKey());
        UserConfiguration.getInstance().addAuthority(user2.getMetadataKey(), authority2.getMetadataKey());
        assertThat(UserConfiguration.getInstance().getAuthorities(user1.getMetadataKey()))
                .containsOnly(authority1, authority2);
        assertThat(UserConfiguration.getInstance().getAuthorities(user2.getMetadataKey()))
                .containsOnly(authority2);
    }

    @Test
    void addAuthorityByName() {
        UserConfiguration.getInstance().insert(user1);
        UserConfiguration.getInstance().insert(user2);
        AuthorityConfiguration.getInstance().insert(authority1);
        AuthorityConfiguration.getInstance().insert(authority2);
        UserConfiguration.getInstance().addAuthority(user1.getUsername(), authority1.getAuthority());
        UserConfiguration.getInstance().addAuthority(user1.getUsername(), authority2.getAuthority());
        UserConfiguration.getInstance().addAuthority(user2.getUsername(), authority2.getAuthority());
        assertThat(UserConfiguration.getInstance().getAuthorities(user1.getMetadataKey()))
                .containsOnly(authority1, authority2);
        assertThat(UserConfiguration.getInstance().getAuthorities(user2.getMetadataKey()))
                .containsOnly(authority2);
    }

    @Test
    void removeAuthority() {
        UserConfiguration.getInstance().insert(user1);
        UserConfiguration.getInstance().insert(user2);
        AuthorityConfiguration.getInstance().insert(authority1);
        AuthorityConfiguration.getInstance().insert(authority2);
        UserConfiguration.getInstance().addAuthority(user1.getMetadataKey(), authority1.getMetadataKey());
        UserConfiguration.getInstance().addAuthority(user1.getMetadataKey(), authority2.getMetadataKey());
        UserConfiguration.getInstance().addAuthority(user2.getMetadataKey(), authority2.getMetadataKey());

        assertThat(UserConfiguration.getInstance().getAuthorities(user1.getMetadataKey()))
                .containsOnly(authority1, authority2);
        assertThat(UserConfiguration.getInstance().getAuthorities(user2.getMetadataKey()))
                .containsOnly(authority2);

        UserConfiguration.getInstance().removeAuthority(user1.getMetadataKey(), authority1.getMetadataKey());

        assertThat(UserConfiguration.getInstance().getAuthorities(user1.getMetadataKey()))
                .containsOnly(authority2);
        assertThat(UserConfiguration.getInstance().getAuthorities(user2.getMetadataKey()))
                .containsOnly(authority2);
    }

    @Test
    void removeAuthorityByName() {
        UserConfiguration.getInstance().insert(user1);
        UserConfiguration.getInstance().insert(user2);
        AuthorityConfiguration.getInstance().insert(authority1);
        AuthorityConfiguration.getInstance().insert(authority2);
        UserConfiguration.getInstance().addAuthority(user1.getUsername(), authority1.getAuthority());
        UserConfiguration.getInstance().addAuthority(user1.getUsername(), authority2.getAuthority());
        UserConfiguration.getInstance().addAuthority(user2.getUsername(), authority2.getAuthority());

        assertThat(UserConfiguration.getInstance().getAuthorities(user1.getUsername()))
                .containsOnly(authority1, authority2);
        assertThat(UserConfiguration.getInstance().getAuthorities(user2.getUsername()))
                .containsOnly(authority2);

        UserConfiguration.getInstance().removeAuthority(user1.getUsername(), authority1.getAuthority());

        assertThat(UserConfiguration.getInstance().getAuthorities(user1.getMetadataKey()))
                .containsOnly(authority2);
        assertThat(UserConfiguration.getInstance().getAuthorities(user2.getMetadataKey()))
                .containsOnly(authority2);
    }

    @Test
    void getAuthorities() {
        UserConfiguration.getInstance().insert(user1);
        UserConfiguration.getInstance().insert(user2);
        AuthorityConfiguration.getInstance().insert(authority1);
        AuthorityConfiguration.getInstance().insert(authority2);
        AuthorityConfiguration.getInstance().insert(authority3);
        AuthorityConfiguration.getInstance().insert(authority4);
        GroupConfiguration.getInstance().insert(group1);
        GroupConfiguration.getInstance().insert(group2);
        GroupConfiguration.getInstance().addAuthority(group1.getMetadataKey(), authority3.getMetadataKey());
        GroupConfiguration.getInstance().addAuthority(group1.getMetadataKey(), authority4.getMetadataKey());
        GroupConfiguration.getInstance().addAuthority(group2.getMetadataKey(), authority3.getMetadataKey());
        GroupConfiguration.getInstance().addUser(group1.getMetadataKey(), user1.getMetadataKey());
        GroupConfiguration.getInstance().addUser(group2.getMetadataKey(), user2.getMetadataKey());
        UserConfiguration.getInstance().addAuthority(user1.getMetadataKey(), authority1.getMetadataKey());
        UserConfiguration.getInstance().addAuthority(user1.getMetadataKey(), authority2.getMetadataKey());
        UserConfiguration.getInstance().addAuthority(user2.getMetadataKey(), authority2.getMetadataKey());

        assertThat(UserConfiguration.getInstance().getAuthorities(user1.getMetadataKey()))
                .containsOnly(authority1, authority2, authority3, authority4);
        assertThat(UserConfiguration.getInstance().getAuthorities(user2.getMetadataKey()))
                .containsOnly(authority2, authority3);
    }

    @Test
    void getAuthoritiesByName() {
        UserConfiguration.getInstance().insert(user1);
        UserConfiguration.getInstance().insert(user2);
        AuthorityConfiguration.getInstance().insert(authority1);
        AuthorityConfiguration.getInstance().insert(authority2);
        AuthorityConfiguration.getInstance().insert(authority3);
        AuthorityConfiguration.getInstance().insert(authority4);
        GroupConfiguration.getInstance().insert(group1);
        GroupConfiguration.getInstance().insert(group2);
        GroupConfiguration.getInstance().addAuthority(group1.getMetadataKey(), authority3.getMetadataKey());
        GroupConfiguration.getInstance().addAuthority(group1.getMetadataKey(), authority4.getMetadataKey());
        GroupConfiguration.getInstance().addAuthority(group2.getMetadataKey(), authority3.getMetadataKey());
        GroupConfiguration.getInstance().addUser(group1.getMetadataKey(), user1.getMetadataKey());
        GroupConfiguration.getInstance().addUser(group2.getMetadataKey(), user2.getMetadataKey());
        UserConfiguration.getInstance().addAuthority(user1.getMetadataKey(), authority1.getMetadataKey());
        UserConfiguration.getInstance().addAuthority(user1.getMetadataKey(), authority2.getMetadataKey());
        UserConfiguration.getInstance().addAuthority(user2.getMetadataKey(), authority2.getMetadataKey());

        assertThat(UserConfiguration.getInstance().getAuthorities(user1.getUsername()))
                .containsOnly(authority1, authority2, authority3, authority4);
        assertThat(UserConfiguration.getInstance().getAuthorities(user2.getUsername()))
                .containsOnly(authority2, authority3);
    }
=======
//    @Test
//    void getGroups() {
//        UserConfiguration.getInstance().insert(user1);
//        UserConfiguration.getInstance().insert(user2);
//        RoleConfiguration.getInstance().insert(role1);
//        RoleConfiguration.getInstance().insert(role2);
//        TeamConfiguration.getInstance().addUser(role1.getMetadataKey(), user1.getMetadataKey());
//        TeamConfiguration.getInstance().addUser(role2.getMetadataKey(), user1.getMetadataKey());
//        TeamConfiguration.getInstance().addUser(role1.getMetadataKey(), user2.getMetadataKey());
//
//        assertThat(UserConfiguration.getInstance().getGroups(user1.getMetadataKey()))
//                .containsOnly(role1, role2);
//        assertThat(UserConfiguration.getInstance().getGroups(user2.getMetadataKey()))
//                .containsOnly(role1);
//    }
//
//    @Test
//    void addAuthority() {
//        UserConfiguration.getInstance().insert(user1);
//        UserConfiguration.getInstance().insert(user2);
//        AuthorityConfiguration.getInstance().insert(privilege1);
//        AuthorityConfiguration.getInstance().insert(privilege2);
//        UserConfiguration.getInstance().addAuthority(user1.getMetadataKey(), privilege1.getMetadataKey());
//        UserConfiguration.getInstance().addAuthority(user1.getMetadataKey(), privilege2.getMetadataKey());
//        UserConfiguration.getInstance().addAuthority(user2.getMetadataKey(), privilege2.getMetadataKey());
//        assertThat(UserConfiguration.getInstance().getAuthorities(user1.getMetadataKey()))
//                .containsOnly(privilege1, privilege2);
//        assertThat(UserConfiguration.getInstance().getAuthorities(user2.getMetadataKey()))
//                .containsOnly(privilege2);
//    }
//
//    @Test
//    void addAuthorityByName() {
//        UserConfiguration.getInstance().insert(user1);
//        UserConfiguration.getInstance().insert(user2);
//        AuthorityConfiguration.getInstance().insert(privilege1);
//        AuthorityConfiguration.getInstance().insert(privilege2);
//        UserConfiguration.getInstance().addAuthority(user1.getUsername(), privilege1.getPrivilege());
//        UserConfiguration.getInstance().addAuthority(user1.getUsername(), privilege2.getPrivilege());
//        UserConfiguration.getInstance().addAuthority(user2.getUsername(), privilege2.getPrivilege());
//        assertThat(UserConfiguration.getInstance().getAuthorities(user1.getMetadataKey()))
//                .containsOnly(privilege1, privilege2);
//        assertThat(UserConfiguration.getInstance().getAuthorities(user2.getMetadataKey()))
//                .containsOnly(privilege2);
//    }
//
//    @Test
//    void removeAuthority() {
//        UserConfiguration.getInstance().insert(user1);
//        UserConfiguration.getInstance().insert(user2);
//        AuthorityConfiguration.getInstance().insert(privilege1);
//        AuthorityConfiguration.getInstance().insert(privilege2);
//        UserConfiguration.getInstance().addAuthority(user1.getMetadataKey(), privilege1.getMetadataKey());
//        UserConfiguration.getInstance().addAuthority(user1.getMetadataKey(), privilege2.getMetadataKey());
//        UserConfiguration.getInstance().addAuthority(user2.getMetadataKey(), privilege2.getMetadataKey());
//
//        assertThat(UserConfiguration.getInstance().getAuthorities(user1.getMetadataKey()))
//                .containsOnly(privilege1, privilege2);
//        assertThat(UserConfiguration.getInstance().getAuthorities(user2.getMetadataKey()))
//                .containsOnly(privilege2);
//
//        UserConfiguration.getInstance().removeAuthority(user1.getMetadataKey(), privilege1.getMetadataKey());
//
//        assertThat(UserConfiguration.getInstance().getAuthorities(user1.getMetadataKey()))
//                .containsOnly(privilege2);
//        assertThat(UserConfiguration.getInstance().getAuthorities(user2.getMetadataKey()))
//                .containsOnly(privilege2);
//    }
//
//    @Test
//    void removeAuthorityByName() {
//        UserConfiguration.getInstance().insert(user1);
//        UserConfiguration.getInstance().insert(user2);
//        AuthorityConfiguration.getInstance().insert(privilege1);
//        AuthorityConfiguration.getInstance().insert(privilege2);
//        UserConfiguration.getInstance().addAuthority(user1.getUsername(), privilege1.getPrivilege());
//        UserConfiguration.getInstance().addAuthority(user1.getUsername(), privilege2.getPrivilege());
//        UserConfiguration.getInstance().addAuthority(user2.getUsername(), privilege2.getPrivilege());
//
//        assertThat(UserConfiguration.getInstance().getAuthorities(user1.getUsername()))
//                .containsOnly(privilege1, privilege2);
//        assertThat(UserConfiguration.getInstance().getAuthorities(user2.getUsername()))
//                .containsOnly(privilege2);
//
//        UserConfiguration.getInstance().removeAuthority(user1.getUsername(), privilege1.getPrivilege());
//
//        assertThat(UserConfiguration.getInstance().getAuthorities(user1.getMetadataKey()))
//                .containsOnly(privilege2);
//        assertThat(UserConfiguration.getInstance().getAuthorities(user2.getMetadataKey()))
//                .containsOnly(privilege2);
//    }
//
//    @Test
//    void getAuthorities() {
//        UserConfiguration.getInstance().insert(user1);
//        UserConfiguration.getInstance().insert(user2);
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
//        UserConfiguration.getInstance().addAuthority(user1.getMetadataKey(), privilege1.getMetadataKey());
//        UserConfiguration.getInstance().addAuthority(user1.getMetadataKey(), privilege2.getMetadataKey());
//        UserConfiguration.getInstance().addAuthority(user2.getMetadataKey(), privilege2.getMetadataKey());
//
//        assertThat(UserConfiguration.getInstance().getAuthorities(user1.getMetadataKey()))
//                .containsOnly(privilege1, privilege2, privilege3, privilege4);
//        assertThat(UserConfiguration.getInstance().getAuthorities(user2.getMetadataKey()))
//                .containsOnly(privilege2, privilege3);
//    }
//
//    @Test
//    void getAuthoritiesByName() {
//        UserConfiguration.getInstance().insert(user1);
//        UserConfiguration.getInstance().insert(user2);
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
//        UserConfiguration.getInstance().addAuthority(user1.getMetadataKey(), privilege1.getMetadataKey());
//        UserConfiguration.getInstance().addAuthority(user1.getMetadataKey(), privilege2.getMetadataKey());
//        UserConfiguration.getInstance().addAuthority(user2.getMetadataKey(), privilege2.getMetadataKey());
//
//        assertThat(UserConfiguration.getInstance().getAuthorities(user1.getUsername()))
//                .containsOnly(privilege1, privilege2, privilege3, privilege4);
//        assertThat(UserConfiguration.getInstance().getAuthorities(user2.getUsername()))
//                .containsOnly(privilege2, privilege3);
//    }
>>>>>>> master


}
