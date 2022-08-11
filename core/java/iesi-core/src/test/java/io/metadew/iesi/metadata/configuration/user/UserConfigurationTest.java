package io.metadew.iesi.metadata.configuration.user;

import io.metadew.iesi.SpringContext;
import io.metadew.iesi.common.configuration.Configuration;
import io.metadew.iesi.common.configuration.metadata.repository.MetadataRepositoryConfiguration;
import io.metadew.iesi.metadata.definition.user.*;
import io.metadew.iesi.metadata.repository.MetadataRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest(classes = { Configuration.class, SpringContext.class, MetadataRepositoryConfiguration.class, UserConfiguration.class })
class UserConfigurationTest {

    private UUID uuid1;
    private User user1;
    private UUID uuid2;
    private User user2;


    private Role role1;
    private Role role2;

    private Privilege privilege1;
    private Privilege privilege2;
    private Privilege privilege3;
    private Privilege privilege4;

    @Autowired
    private static MetadataRepositoryConfiguration metadataRepositoryConfiguration;
    @Autowired
    private UserConfiguration userConfiguration;

    @BeforeAll
    static void prepare() {
        metadataRepositoryConfiguration
                .getMetadataRepositories()
                .forEach(MetadataRepository::createAllTables);
    }

    @AfterEach
    void clearDatabase() {
        metadataRepositoryConfiguration
                .getMetadataRepositories()
                .forEach(MetadataRepository::cleanAllTables);
    }

    @AfterAll
    static void teardown() {
        // Configuration.getInstance();
        metadataRepositoryConfiguration
                .getMetadataRepositories()
                .forEach(MetadataRepository::dropAllTables);
    }

    @BeforeEach
    void setup() {

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
                .password("password1")
                .roleKeys(Stream.of(role1.getMetadataKey(), role2.getMetadataKey()).collect(Collectors.toSet()))
                .build();
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
                .roleKeys(Stream.of(role1.getMetadataKey()).collect(Collectors.toSet()))
                .build();
    }

    @Test
    void userDoesNotExistsTest() {
        assertThat(userConfiguration.exists(new UserKey(uuid1))).isFalse();
    }

    @Test
    void userExistsTest() {
        userConfiguration.insert(user1);
        assertThat(userConfiguration.exists(new UserKey(uuid1))).isTrue();
    }

    @Test
    void userExistsByNameTest() {
        userConfiguration.insert(user1);
        assertThat(userConfiguration.exists(user1.getUsername())).isTrue();
    }

    @Test
    void userGetDoesNotExistsTest() {
        assertThat(userConfiguration.get(new UserKey(uuid1))).isEmpty();
        userConfiguration.insert(user1);
        assertThat(userConfiguration.get(new UserKey(uuid2))).isEmpty();
    }

    @Test
    void userGetByNameDoesNotExistsTest() {
        assertThat(userConfiguration.getByName(user1.getUsername())).isEmpty();
        userConfiguration.insert(user1);
        assertThat(userConfiguration.getByName(user2.getUsername())).isEmpty();
    }

    @Test
    void userGetExistsTest() {
        userConfiguration.insert(user1);
        assertThat(userConfiguration.get(new UserKey(uuid1)))
                .isPresent()
                .hasValue(user1);
    }

    @Test
    void userGetByNameExistsTest() {
        userConfiguration.insert(user1);
        assertThat(userConfiguration.getByName(user1.getUsername()))
                .isPresent()
                .hasValue(user1);
    }

    @Test
    void userInsertTest() {
        assertThat(userConfiguration.exists(new UserKey(uuid1)))
                .isFalse();
        userConfiguration.insert(user1);
        assertThat(userConfiguration.exists(new UserKey(uuid1)))
                .isTrue();
        assertThat(userConfiguration.get(new UserKey(uuid1)))
                .isPresent()
                .hasValue(user1);
    }

    @Test
    void userInsertAlreadyExistingTest() {
        userConfiguration.insert(user1);
        assertThatThrownBy(() -> userConfiguration.insert(user1))
                .isInstanceOf(RuntimeException.class);
    }

    @Test
    void userInsertMultipleUsersTest() {
        assertThat(userConfiguration.exists(new UserKey(uuid1)))
                .isFalse();
        userConfiguration.insert(user1);
        assertThat(userConfiguration.exists(new UserKey(uuid1)))
                .isTrue();
        assertThat(userConfiguration.get(new UserKey(uuid1)))
                .isPresent()
                .hasValue(user1);
        userConfiguration.insert(user2);
        assertThat(userConfiguration.exists(new UserKey(uuid2)))
                .isTrue();
        assertThat(userConfiguration.exists(new UserKey(uuid1)))
                .isTrue();
        assertThat(userConfiguration.get(new UserKey(uuid2)))
                .isPresent()
                .hasValue(user2);
        assertThat(userConfiguration.get(new UserKey(uuid1)))
                .isPresent()
                .hasValue(user1);
    }

    @Test
    void userDeleteDoesNotExistTest() {
        userConfiguration.delete(user1.getMetadataKey());
    }

    @Test
    void userDeleteByNameDoesNotExistTest() {
        userConfiguration.delete(user1.getUsername());
    }

    @Test
    void userDeleteTest() {
        userConfiguration.insert(user1);
        assertThat(userConfiguration.exists(new UserKey(uuid1)))
                .isTrue();
        userConfiguration.delete(user1.getMetadataKey());

        assertThat(userConfiguration.exists(new UserKey(uuid1)))
                .isFalse();
    }

    @Test
    void userDeleteByNameTest() {
        userConfiguration.insert(user1);
        assertThat(userConfiguration.exists(new UserKey(uuid1)))
                .isTrue();
        userConfiguration.delete(user1.getUsername());

        assertThat(userConfiguration.exists(new UserKey(uuid1)))
                .isFalse();
    }

    @Test
    void userUpdateTest() {
        userConfiguration.insert(user1);
        Optional<User> user = userConfiguration.get(new UserKey(uuid1));
        assertThat(user)
                .isPresent()
                .hasValue(user1);
        assertThat(user.get().isEnabled())
                .isTrue();

        user1.setEnabled(false);
        userConfiguration.update(user1);

        assertThat(userConfiguration.get(new UserKey(uuid1)).get().isEnabled())
                .isFalse();
    }

    @Test
    void userUpdateMultipleTest() {
        userConfiguration.insert(user1);
        userConfiguration.insert(user2);
        Optional<User> fetchedUser1 = userConfiguration.get(new UserKey(uuid1));
        assertThat(fetchedUser1)
                .isPresent()
                .hasValue(user1);
        assertThat(fetchedUser1.get().isEnabled())
                .isTrue();
        Optional<User> fetchedUser2 = userConfiguration.get(new UserKey(uuid2));
        assertThat(fetchedUser2)
                .isPresent()
                .hasValue(user2);
        assertThat(fetchedUser2.get().isEnabled())
                .isTrue();

        user1.setEnabled(false);
        userConfiguration.update(user1);

        assertThat(userConfiguration.get(new UserKey(uuid1)).get().isEnabled())
                .isFalse();
        assertThat(userConfiguration.get(new UserKey(uuid2)).get().isEnabled())
                .isTrue();
    }

}
