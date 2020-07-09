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


    private Group group1;
    private Group group2;

    private Authority authority1;
    private Authority authority2;
    private Authority authority3;
    private Authority authority4;

    @BeforeAll
    static void prepare() throws Exception {
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
    void clearDatabase() throws Exception {
        MetadataRepositoryConfiguration.getInstance()
                .getControlMetadataRepository().cleanAllTables();
    }

    @AfterAll
    static void teardown() throws Exception {
        MetadataRepositoryConfiguration.getInstance()
                .getControlMetadataRepository().dropAllTables();
    }

    @Test
    void userDoesNotExistsTest() throws Exception {
        assertThat(UserConfiguration.getInstance().exists(new UserKey(uuid1))).isFalse();
    }

    @Test
    void userExistsTest() throws Exception {
        UserConfiguration.getInstance().insert(user1);
        assertThat(UserConfiguration.getInstance().exists(new UserKey(uuid1))).isTrue();
    }

    @Test
    void userExistsByNameTest() throws Exception {
        UserConfiguration.getInstance().insert(user1);
        assertThat(UserConfiguration.getInstance().exists(user1.getUsername())).isTrue();
    }

    @Test
    void userGetDoesNotExistsTest() throws Exception {
        assertThat(UserConfiguration.getInstance().get(new UserKey(uuid1))).isEmpty();
        UserConfiguration.getInstance().insert(user1);
        assertThat(UserConfiguration.getInstance().get(new UserKey(uuid2))).isEmpty();
    }

    @Test
    void userGetByNameDoesNotExistsTest() throws Exception {
        assertThat(UserConfiguration.getInstance().get(user1.getUsername())).isEmpty();
        UserConfiguration.getInstance().insert(user1);
        assertThat(UserConfiguration.getInstance().get(user2.getUsername())).isEmpty();
    }

    @Test
    void userGetExistsTest() throws Exception {
        UserConfiguration.getInstance().insert(user1);
        assertThat(UserConfiguration.getInstance().get(new UserKey(uuid1)))
                .isPresent()
                .hasValue(user1);
    }

    @Test
    void userGetByNameExistsTest() throws Exception {
        UserConfiguration.getInstance().insert(user1);
        assertThat(UserConfiguration.getInstance().get(user1.getUsername()))
                .isPresent()
                .hasValue(user1);
    }

    @Test
    void userInsertTest() throws Exception {
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
    void userInsertAlreadyExistingTest() throws Exception {
        UserConfiguration.getInstance().insert(user1);
        assertThatThrownBy(() -> UserConfiguration.getInstance().insert(user1))
                .isInstanceOf(RuntimeException.class);
    }

    @Test
    void userInsertMultipleUsersTest() throws Exception {
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
    void userDeleteDoesNotExistTest() throws Exception {
        UserConfiguration.getInstance().delete(user1.getMetadataKey());
    }

    @Test
    void userDeleteByNameDoesNotExistTest() throws Exception {
        UserConfiguration.getInstance().delete(user1.getUsername());
    }

    @Test
    void userDeleteTest() throws Exception {
        UserConfiguration.getInstance().insert(user1);
        assertThat(UserConfiguration.getInstance().exists(new UserKey(uuid1)))
                .isTrue();
        UserConfiguration.getInstance().delete(user1.getMetadataKey());

        assertThat(UserConfiguration.getInstance().exists(new UserKey(uuid1)))
                .isFalse();
    }

    @Test
    void userDeleteByNameTest() throws Exception {
        UserConfiguration.getInstance().insert(user1);
        assertThat(UserConfiguration.getInstance().exists(new UserKey(uuid1)))
                .isTrue();
        UserConfiguration.getInstance().delete(user1.getUsername());

        assertThat(UserConfiguration.getInstance().exists(new UserKey(uuid1)))
                .isFalse();
    }

    @Test
    void userUpdateTest() throws Exception {
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
    void userUpdateMultipleTest() throws Exception {
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
    void getGroups() throws Exception {
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
    void addAuthority() throws Exception {
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
    void addAuthorityByName() throws Exception {
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
    void removeAuthority() throws Exception {
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
    void removeAuthorityByName() throws Exception {
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
    void getAuthorities() throws Exception {
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
    void getAuthoritiesByName() throws Exception {
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


}
