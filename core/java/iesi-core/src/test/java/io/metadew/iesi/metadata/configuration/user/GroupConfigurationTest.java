package io.metadew.iesi.metadata.configuration.user;

import io.metadew.iesi.common.configuration.Configuration;
import io.metadew.iesi.common.configuration.metadata.repository.MetadataRepositoryConfiguration;
import io.metadew.iesi.metadata.definition.user.*;
import org.junit.jupiter.api.*;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class GroupConfigurationTest {

    private UUID uuid1;
    private Group group1;
    private UUID uuid2;
    private Group group2;

    private User user1;
    private User user2;
    private User user3;

    private Authority authority1;
    private Authority authority2;
    private Authority authority3;

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
        group1 = Group.builder()
                .groupKey(GroupKey.builder()
                        .uuid(uuid1)
                        .build())
                .groupName("group1")
                .build();
        uuid2 = UUID.randomUUID();
        group2 = Group.builder()
                .groupKey(GroupKey.builder()
                        .uuid(uuid2)
                        .build())
                .groupName("group2")
                .build();
        user1 = User.builder()
                .userKey(UserKey.builder()
                        .uuid(UUID.randomUUID())
                        .build())
                .username("user1")
                .enabled(true)
                .password("password1").build();
        user2 = User.builder()
                .userKey(UserKey.builder()
                        .uuid(UUID.randomUUID())
                        .build())
                .username("user2")
                .enabled(true)
                .password("password2")
                .build();
        user3 = User.builder()
                .userKey(UserKey.builder()
                        .uuid(UUID.randomUUID())
                        .build())
                .username("user3")
                .enabled(true)
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
        assertThat(GroupConfiguration.getInstance().exists(new GroupKey(uuid1))).isFalse();
    }

    @Test
    void userExistsTest() {
        GroupConfiguration.getInstance().insert(group1);
        assertThat(GroupConfiguration.getInstance().exists(new GroupKey(uuid1))).isTrue();
    }

    @Test
    void userGetDoesNotExistsTest() {
        assertThat(GroupConfiguration.getInstance().get(new GroupKey(uuid1))).isEmpty();
        GroupConfiguration.getInstance().insert(group1);
        assertThat(GroupConfiguration.getInstance().get(new GroupKey(uuid2))).isEmpty();
    }

    @Test
    void userGetExistsTest() {
        GroupConfiguration.getInstance().insert(group1);
        assertThat(GroupConfiguration.getInstance().get(new GroupKey(uuid1)))
                .isPresent()
                .hasValue(group1);
    }

    @Test
    void userInsertTest() {
        assertThat(GroupConfiguration.getInstance().exists(new GroupKey(uuid1)))
                .isFalse();
        GroupConfiguration.getInstance().insert(group1);
        assertThat(GroupConfiguration.getInstance().exists(new GroupKey(uuid1)))
                .isTrue();
        assertThat(GroupConfiguration.getInstance().get(new GroupKey(uuid1)))
                .isPresent()
                .hasValue(group1);
    }

    @Test
    void userInsertAlreadyExistingTest() {
        GroupConfiguration.getInstance().insert(group1);
        assertThatThrownBy(() -> GroupConfiguration.getInstance().insert(group1))
                .isInstanceOf(RuntimeException.class);
    }

    @Test
    void userInsertMultipleUsersTest() {
        assertThat(GroupConfiguration.getInstance().exists(new GroupKey(uuid1)))
                .isFalse();
        GroupConfiguration.getInstance().insert(group1);
        assertThat(GroupConfiguration.getInstance().exists(new GroupKey(uuid1)))
                .isTrue();
        assertThat(GroupConfiguration.getInstance().get(new GroupKey(uuid1)))
                .isPresent()
                .hasValue(group1);
        GroupConfiguration.getInstance().insert(group2);
        assertThat(GroupConfiguration.getInstance().exists(new GroupKey(uuid2)))
                .isTrue();
        assertThat(GroupConfiguration.getInstance().exists(new GroupKey(uuid1)))
                .isTrue();
        assertThat(GroupConfiguration.getInstance().get(new GroupKey(uuid2)))
                .isPresent()
                .hasValue(group2);
        assertThat(GroupConfiguration.getInstance().get(new GroupKey(uuid1)))
                .isPresent()
                .hasValue(group1);
    }

    @Test
    void userDeleteDoesNotExistTest() {
        GroupConfiguration.getInstance().delete(group1.getMetadataKey());
        //assertThatThrownBy(() -> UserConfiguration.getInstance().delete(user1.getMetadataKey()))
        //        .isInstanceOf(RuntimeException.class);
    }

    @Test
    void userDeleteTest() {
        GroupConfiguration.getInstance().insert(group1);
        assertThat(GroupConfiguration.getInstance().exists(new GroupKey(uuid1)))
                .isTrue();
        GroupConfiguration.getInstance().delete(group1.getMetadataKey());

        assertThat(GroupConfiguration.getInstance().exists(new GroupKey(uuid1)))
                .isFalse();
    }

    @Test
    void userUpdateTest() {
        GroupConfiguration.getInstance().insert(group1);
        Optional<Group> user = GroupConfiguration.getInstance().get(new GroupKey(uuid1));
        assertThat(user)
                .isPresent()
                .hasValue(group1);
        assertThat(user.get().getGroupName())
                .isEqualTo("group1");

        group1.setGroupName("groupA");
        GroupConfiguration.getInstance().update(group1);

        assertThat(GroupConfiguration.getInstance().get(new GroupKey(uuid1)).get().getGroupName())
                .isEqualTo("groupA");
    }


    @Test
    void userUpdateMultipleTest() {
        GroupConfiguration.getInstance().insert(group1);
        GroupConfiguration.getInstance().insert(group2);
        Optional<Group> fetchedUser1 = GroupConfiguration.getInstance().get(new GroupKey(uuid1));
        assertThat(fetchedUser1)
                .isPresent()
                .hasValue(group1);
        assertThat(fetchedUser1.get().getGroupName())
                .isEqualTo("group1");
        Optional<Group> fetchedUser2 = GroupConfiguration.getInstance().get(new GroupKey(uuid2));
        assertThat(fetchedUser2)
                .isPresent()
                .hasValue(group2);
        assertThat(fetchedUser2.get().getGroupName())
                .isEqualTo("group2");

        group1.setGroupName("groupA");
        GroupConfiguration.getInstance().update(group1);

        assertThat(GroupConfiguration.getInstance().get(new GroupKey(uuid1)).get().getGroupName())
                .isEqualTo("groupA");
        assertThat(GroupConfiguration.getInstance().get(new GroupKey(uuid2)).get().getGroupName())
                .isEqualTo("group2");
    }

    @Test
    void getUsers() {
        GroupConfiguration.getInstance().insert(group1);
        GroupConfiguration.getInstance().insert(group2);
        UserConfiguration.getInstance().insert(user1);
        UserConfiguration.getInstance().insert(user2);
        UserConfiguration.getInstance().insert(user3);
        GroupConfiguration.getInstance().addUser(group1.getMetadataKey(), user1.getMetadataKey());
        GroupConfiguration.getInstance().addUser(group1.getMetadataKey(), user2.getMetadataKey());
        GroupConfiguration.getInstance().addUser(group2.getMetadataKey(), user3.getMetadataKey());

        assertThat(GroupConfiguration.getInstance().getUsers(group1.getMetadataKey()))
                .containsOnly(user1, user2);
        assertThat(GroupConfiguration.getInstance().getUsers(group2.getMetadataKey()))
                .containsOnly(user3);
    }

    @Test
    void removeUser() {
        GroupConfiguration.getInstance().insert(group1);
        GroupConfiguration.getInstance().insert(group2);
        UserConfiguration.getInstance().insert(user1);
        UserConfiguration.getInstance().insert(user2);
        UserConfiguration.getInstance().insert(user3);
        GroupConfiguration.getInstance().addUser(group1.getMetadataKey(), user1.getMetadataKey());
        GroupConfiguration.getInstance().addUser(group1.getMetadataKey(), user2.getMetadataKey());
        GroupConfiguration.getInstance().addUser(group2.getMetadataKey(), user3.getMetadataKey());

        assertThat(GroupConfiguration.getInstance().getUsers(group1.getMetadataKey()))
                .containsOnly(user1, user2);
        assertThat(GroupConfiguration.getInstance().getUsers(group2.getMetadataKey()))
                .containsOnly(user3);

        GroupConfiguration.getInstance().removeUser(group1.getMetadataKey(), user1.getMetadataKey());

        assertThat(GroupConfiguration.getInstance().getUsers(group1.getMetadataKey()))
                .containsOnly(user2);
        assertThat(GroupConfiguration.getInstance().getUsers(group2.getMetadataKey()))
                .containsOnly(user3);
    }

    @Test
    void removeNonExistingUser() {
        GroupConfiguration.getInstance().insert(group1);
        UserConfiguration.getInstance().insert(user1);
        GroupConfiguration.getInstance().addUser(group1.getMetadataKey(), user1.getMetadataKey());

        GroupConfiguration.getInstance().removeUser(group1.getMetadataKey(), user2.getMetadataKey());
    }

    @Test
    void addAlreadyMemberUser() {
        GroupConfiguration.getInstance().insert(group1);
        UserConfiguration.getInstance().insert(user1);
        GroupConfiguration.getInstance().addUser(group1.getMetadataKey(), user1.getMetadataKey());

        assertThatThrownBy(() -> GroupConfiguration.getInstance().addUser(group1.getMetadataKey(), user1.getMetadataKey()))
                .isInstanceOf(RuntimeException.class);
    }

    @Test
    void getAuthorities() {
        GroupConfiguration.getInstance().insert(group1);
        GroupConfiguration.getInstance().insert(group2);
        AuthorityConfiguration.getInstance().insert(authority1);
        AuthorityConfiguration.getInstance().insert(authority2);
        AuthorityConfiguration.getInstance().insert(authority3);
        GroupConfiguration.getInstance().addAuthority(group1.getMetadataKey(), authority1.getMetadataKey());
        GroupConfiguration.getInstance().addAuthority(group1.getMetadataKey(), authority2.getMetadataKey());
        GroupConfiguration.getInstance().addAuthority(group2.getMetadataKey(), authority3.getMetadataKey());

        assertThat(GroupConfiguration.getInstance().getAuthorities(group1.getMetadataKey()))
                .containsOnly(authority1, authority2);
        assertThat(GroupConfiguration.getInstance().getAuthorities(group2.getMetadataKey()))
                .containsOnly(authority3);
    }

    @Test
    void removeAuthority() {
        GroupConfiguration.getInstance().insert(group1);
        GroupConfiguration.getInstance().insert(group2);
        AuthorityConfiguration.getInstance().insert(authority1);
        AuthorityConfiguration.getInstance().insert(authority2);
        AuthorityConfiguration.getInstance().insert(authority3);
        GroupConfiguration.getInstance().addAuthority(group1.getMetadataKey(), authority1.getMetadataKey());
        GroupConfiguration.getInstance().addAuthority(group1.getMetadataKey(), authority2.getMetadataKey());
        GroupConfiguration.getInstance().addAuthority(group2.getMetadataKey(), authority3.getMetadataKey());

        assertThat(GroupConfiguration.getInstance().getAuthorities(group1.getMetadataKey()))
                .containsOnly(authority1, authority2);
        assertThat(GroupConfiguration.getInstance().getAuthorities(group2.getMetadataKey()))
                .containsOnly(authority3);

        GroupConfiguration.getInstance().removeAuthority(group1.getMetadataKey(), authority1.getMetadataKey());

        assertThat(GroupConfiguration.getInstance().getAuthorities(group1.getMetadataKey()))
                .containsOnly(authority2);
        assertThat(GroupConfiguration.getInstance().getAuthorities(group2.getMetadataKey()))
                .containsOnly(authority3);
    }

    @Test
    void removeNonExistingAuthority() {
        GroupConfiguration.getInstance().insert(group1);
        AuthorityConfiguration.getInstance().insert(authority1);
        GroupConfiguration.getInstance().addAuthority(group1.getMetadataKey(), authority1.getMetadataKey());

        GroupConfiguration.getInstance().removeAuthority(group1.getMetadataKey(), authority2.getMetadataKey());
    }

    @Test
    void addAlreadyAuthority() {
        GroupConfiguration.getInstance().insert(group1);
        AuthorityConfiguration.getInstance().insert(authority1);
        GroupConfiguration.getInstance().addAuthority(group1.getMetadataKey(), authority1.getMetadataKey());

        assertThatThrownBy(() -> GroupConfiguration.getInstance().addAuthority(group1.getMetadataKey(), authority1.getMetadataKey()))
                .isInstanceOf(RuntimeException.class);
    }


}
