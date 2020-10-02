package io.metadew.iesi.metadata.configuration.user;

import io.metadew.iesi.common.configuration.Configuration;
import io.metadew.iesi.common.configuration.metadata.repository.MetadataRepositoryConfiguration;
import io.metadew.iesi.metadata.definition.user.*;
import org.junit.jupiter.api.*;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class UserConfigurationTest {

    private UUID uuid1;
    private User user1;
    private UUID uuid2;
    private User user2;


    private Team team1;
    private Team team2;

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
                .password("password1").build();
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
                .build();
        privilege1 = Privilege.builder()
                .privilegeKey(PrivilegeKey.builder()
                        .uuid(UUID.randomUUID())
                        .build())
                .authority("authority1")
                .build();
        privilege2 = Privilege.builder()
                .privilegeKey(PrivilegeKey.builder()
                        .uuid(UUID.randomUUID())
                        .build())
                .authority("authority2")
                .build();
        privilege3 = Privilege.builder()
                .privilegeKey(PrivilegeKey.builder()
                        .uuid(UUID.randomUUID())
                        .build())
                .authority("authority3")
                .build();
        privilege4 = Privilege.builder()
                .privilegeKey(PrivilegeKey.builder()
                        .uuid(UUID.randomUUID())
                        .build())
                .authority("authority4")
                .build();
        team1 = Team.builder()
                .teamKey(TeamKey.builder()
                        .uuid(UUID.randomUUID())
                        .build())
                .teamName("group1")
                .build();
        team2 = Team.builder()
                .teamKey(TeamKey.builder()
                        .uuid(UUID.randomUUID())
                        .build())
                .teamName("group2")
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
        assertThat(UserConfiguration.getInstance().get(user1.getUsername())).isEmpty();
        UserConfiguration.getInstance().insert(user1);
        assertThat(UserConfiguration.getInstance().get(user2.getUsername())).isEmpty();
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
        assertThat(UserConfiguration.getInstance().get(user1.getUsername()))
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

    @Test
    void getGroups() {
        UserConfiguration.getInstance().insert(user1);
        UserConfiguration.getInstance().insert(user2);
        GroupConfiguration.getInstance().insert(team1);
        GroupConfiguration.getInstance().insert(team2);
        GroupConfiguration.getInstance().addUser(team1.getMetadataKey(), user1.getMetadataKey());
        GroupConfiguration.getInstance().addUser(team2.getMetadataKey(), user1.getMetadataKey());
        GroupConfiguration.getInstance().addUser(team1.getMetadataKey(), user2.getMetadataKey());

        assertThat(UserConfiguration.getInstance().getGroups(user1.getMetadataKey()))
                .containsOnly(team1, team2);
        assertThat(UserConfiguration.getInstance().getGroups(user2.getMetadataKey()))
                .containsOnly(team1);
    }

    @Test
    void addAuthority() {
        UserConfiguration.getInstance().insert(user1);
        UserConfiguration.getInstance().insert(user2);
        AuthorityConfiguration.getInstance().insert(privilege1);
        AuthorityConfiguration.getInstance().insert(privilege2);
        UserConfiguration.getInstance().addAuthority(user1.getMetadataKey(), privilege1.getMetadataKey());
        UserConfiguration.getInstance().addAuthority(user1.getMetadataKey(), privilege2.getMetadataKey());
        UserConfiguration.getInstance().addAuthority(user2.getMetadataKey(), privilege2.getMetadataKey());
        assertThat(UserConfiguration.getInstance().getAuthorities(user1.getMetadataKey()))
                .containsOnly(privilege1, privilege2);
        assertThat(UserConfiguration.getInstance().getAuthorities(user2.getMetadataKey()))
                .containsOnly(privilege2);
    }

    @Test
    void addAuthorityByName() {
        UserConfiguration.getInstance().insert(user1);
        UserConfiguration.getInstance().insert(user2);
        AuthorityConfiguration.getInstance().insert(privilege1);
        AuthorityConfiguration.getInstance().insert(privilege2);
        UserConfiguration.getInstance().addAuthority(user1.getUsername(), privilege1.getAuthority());
        UserConfiguration.getInstance().addAuthority(user1.getUsername(), privilege2.getAuthority());
        UserConfiguration.getInstance().addAuthority(user2.getUsername(), privilege2.getAuthority());
        assertThat(UserConfiguration.getInstance().getAuthorities(user1.getMetadataKey()))
                .containsOnly(privilege1, privilege2);
        assertThat(UserConfiguration.getInstance().getAuthorities(user2.getMetadataKey()))
                .containsOnly(privilege2);
    }

    @Test
    void removeAuthority() {
        UserConfiguration.getInstance().insert(user1);
        UserConfiguration.getInstance().insert(user2);
        AuthorityConfiguration.getInstance().insert(privilege1);
        AuthorityConfiguration.getInstance().insert(privilege2);
        UserConfiguration.getInstance().addAuthority(user1.getMetadataKey(), privilege1.getMetadataKey());
        UserConfiguration.getInstance().addAuthority(user1.getMetadataKey(), privilege2.getMetadataKey());
        UserConfiguration.getInstance().addAuthority(user2.getMetadataKey(), privilege2.getMetadataKey());

        assertThat(UserConfiguration.getInstance().getAuthorities(user1.getMetadataKey()))
                .containsOnly(privilege1, privilege2);
        assertThat(UserConfiguration.getInstance().getAuthorities(user2.getMetadataKey()))
                .containsOnly(privilege2);

        UserConfiguration.getInstance().removeAuthority(user1.getMetadataKey(), privilege1.getMetadataKey());

        assertThat(UserConfiguration.getInstance().getAuthorities(user1.getMetadataKey()))
                .containsOnly(privilege2);
        assertThat(UserConfiguration.getInstance().getAuthorities(user2.getMetadataKey()))
                .containsOnly(privilege2);
    }

    @Test
    void removeAuthorityByName() {
        UserConfiguration.getInstance().insert(user1);
        UserConfiguration.getInstance().insert(user2);
        AuthorityConfiguration.getInstance().insert(privilege1);
        AuthorityConfiguration.getInstance().insert(privilege2);
        UserConfiguration.getInstance().addAuthority(user1.getUsername(), privilege1.getAuthority());
        UserConfiguration.getInstance().addAuthority(user1.getUsername(), privilege2.getAuthority());
        UserConfiguration.getInstance().addAuthority(user2.getUsername(), privilege2.getAuthority());

        assertThat(UserConfiguration.getInstance().getAuthorities(user1.getUsername()))
                .containsOnly(privilege1, privilege2);
        assertThat(UserConfiguration.getInstance().getAuthorities(user2.getUsername()))
                .containsOnly(privilege2);

        UserConfiguration.getInstance().removeAuthority(user1.getUsername(), privilege1.getAuthority());

        assertThat(UserConfiguration.getInstance().getAuthorities(user1.getMetadataKey()))
                .containsOnly(privilege2);
        assertThat(UserConfiguration.getInstance().getAuthorities(user2.getMetadataKey()))
                .containsOnly(privilege2);
    }

    @Test
    void getAuthorities() {
        UserConfiguration.getInstance().insert(user1);
        UserConfiguration.getInstance().insert(user2);
        AuthorityConfiguration.getInstance().insert(privilege1);
        AuthorityConfiguration.getInstance().insert(privilege2);
        AuthorityConfiguration.getInstance().insert(privilege3);
        AuthorityConfiguration.getInstance().insert(privilege4);
        GroupConfiguration.getInstance().insert(team1);
        GroupConfiguration.getInstance().insert(team2);
        GroupConfiguration.getInstance().addAuthority(team1.getMetadataKey(), privilege3.getMetadataKey());
        GroupConfiguration.getInstance().addAuthority(team1.getMetadataKey(), privilege4.getMetadataKey());
        GroupConfiguration.getInstance().addAuthority(team2.getMetadataKey(), privilege3.getMetadataKey());
        GroupConfiguration.getInstance().addUser(team1.getMetadataKey(), user1.getMetadataKey());
        GroupConfiguration.getInstance().addUser(team2.getMetadataKey(), user2.getMetadataKey());
        UserConfiguration.getInstance().addAuthority(user1.getMetadataKey(), privilege1.getMetadataKey());
        UserConfiguration.getInstance().addAuthority(user1.getMetadataKey(), privilege2.getMetadataKey());
        UserConfiguration.getInstance().addAuthority(user2.getMetadataKey(), privilege2.getMetadataKey());

        assertThat(UserConfiguration.getInstance().getAuthorities(user1.getMetadataKey()))
                .containsOnly(privilege1, privilege2, privilege3, privilege4);
        assertThat(UserConfiguration.getInstance().getAuthorities(user2.getMetadataKey()))
                .containsOnly(privilege2, privilege3);
    }

    @Test
    void getAuthoritiesByName() {
        UserConfiguration.getInstance().insert(user1);
        UserConfiguration.getInstance().insert(user2);
        AuthorityConfiguration.getInstance().insert(privilege1);
        AuthorityConfiguration.getInstance().insert(privilege2);
        AuthorityConfiguration.getInstance().insert(privilege3);
        AuthorityConfiguration.getInstance().insert(privilege4);
        GroupConfiguration.getInstance().insert(team1);
        GroupConfiguration.getInstance().insert(team2);
        GroupConfiguration.getInstance().addAuthority(team1.getMetadataKey(), privilege3.getMetadataKey());
        GroupConfiguration.getInstance().addAuthority(team1.getMetadataKey(), privilege4.getMetadataKey());
        GroupConfiguration.getInstance().addAuthority(team2.getMetadataKey(), privilege3.getMetadataKey());
        GroupConfiguration.getInstance().addUser(team1.getMetadataKey(), user1.getMetadataKey());
        GroupConfiguration.getInstance().addUser(team2.getMetadataKey(), user2.getMetadataKey());
        UserConfiguration.getInstance().addAuthority(user1.getMetadataKey(), privilege1.getMetadataKey());
        UserConfiguration.getInstance().addAuthority(user1.getMetadataKey(), privilege2.getMetadataKey());
        UserConfiguration.getInstance().addAuthority(user2.getMetadataKey(), privilege2.getMetadataKey());

        assertThat(UserConfiguration.getInstance().getAuthorities(user1.getUsername()))
                .containsOnly(privilege1, privilege2, privilege3, privilege4);
        assertThat(UserConfiguration.getInstance().getAuthorities(user2.getUsername()))
                .containsOnly(privilege2, privilege3);
    }


}
