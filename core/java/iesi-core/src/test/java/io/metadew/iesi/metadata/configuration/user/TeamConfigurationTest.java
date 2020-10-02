package io.metadew.iesi.metadata.configuration.user;

import io.metadew.iesi.common.configuration.Configuration;
import io.metadew.iesi.common.configuration.metadata.repository.MetadataRepositoryConfiguration;
import io.metadew.iesi.metadata.definition.user.*;
import org.junit.jupiter.api.*;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class TeamConfigurationTest {

    private UUID uuid1;
    private Team team1;
    private UUID uuid2;
    private Team team2;

    private User user1;
    private User user2;
    private User user3;

    private Privilege privilege1;
    private Privilege privilege2;
    private Privilege privilege3;

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
        team1 = Team.builder()
                .teamKey(TeamKey.builder()
                        .uuid(uuid1)
                        .build())
                .teamName("group1")
                .build();
        uuid2 = UUID.randomUUID();
        team2 = Team.builder()
                .teamKey(TeamKey.builder()
                        .uuid(uuid2)
                        .build())
                .teamName("group2")
                .build();
        user1 = User.builder()
                .userKey(UserKey.builder()
                        .uuid(UUID.randomUUID())
                        .build())
                .username("user1")
                .enabled(true)
                .expired(false)
                .credentialsExpired(false)
                .locked(false)
                .password("password1").build();
        user2 = User.builder()
                .userKey(UserKey.builder()
                        .uuid(UUID.randomUUID())
                        .build())
                .username("user2")
                .enabled(true)
                .expired(false)
                .credentialsExpired(false)
                .locked(false)
                .password("password2")
                .build();
        user3 = User.builder()
                .userKey(UserKey.builder()
                        .uuid(UUID.randomUUID())
                        .build())
                .username("user3")
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
    void groupDoesNotExistsTest() {
        assertThat(GroupConfiguration.getInstance().exists(new TeamKey(uuid1))).isFalse();
    }

    @Test
    void groupByNameDoesNotExistsTest() {
        assertThat(GroupConfiguration.getInstance().exists(team1.getTeamName())).isFalse();
    }

    @Test
    void groupExistsTest() {
        GroupConfiguration.getInstance().insert(team1);
        assertThat(GroupConfiguration.getInstance().exists(new TeamKey(uuid1))).isTrue();
    }

    @Test
    void groupExistsByNameTest() {
        GroupConfiguration.getInstance().insert(team1);
        assertThat(GroupConfiguration.getInstance().exists(team1.getTeamName())).isTrue();
    }

    @Test
    void groupGetDoesNotExistsTest() {
        assertThat(GroupConfiguration.getInstance().get(new TeamKey(uuid1))).isEmpty();
        GroupConfiguration.getInstance().insert(team1);
        assertThat(GroupConfiguration.getInstance().get(new TeamKey(uuid2))).isEmpty();
    }

    @Test
    void groupGetByNameDoesNotExistsTest() {
        assertThat(GroupConfiguration.getInstance().get(team1.getTeamName())).isEmpty();
        GroupConfiguration.getInstance().insert(team1);
        assertThat(GroupConfiguration.getInstance().get(team2.getTeamName())).isEmpty();
    }

    @Test
    void groupGetExistsTest() {
        GroupConfiguration.getInstance().insert(team1);
        assertThat(GroupConfiguration.getInstance().get(new TeamKey(uuid1)))
                .isPresent()
                .hasValue(team1);
    }

    @Test
    void groupGetByNameExistsTest() {
        GroupConfiguration.getInstance().insert(team1);
        assertThat(GroupConfiguration.getInstance().get(team1.getTeamName()))
                .isPresent()
                .hasValue(team1);
    }

    @Test
    void groupInsertTest() {
        assertThat(GroupConfiguration.getInstance().exists(new TeamKey(uuid1)))
                .isFalse();
        GroupConfiguration.getInstance().insert(team1);
        assertThat(GroupConfiguration.getInstance().exists(new TeamKey(uuid1)))
                .isTrue();
        assertThat(GroupConfiguration.getInstance().get(new TeamKey(uuid1)))
                .isPresent()
                .hasValue(team1);
    }

    @Test
    void groupInsertAlreadyExistingTest() {
        GroupConfiguration.getInstance().insert(team1);
        assertThatThrownBy(() -> GroupConfiguration.getInstance().insert(team1))
                .isInstanceOf(RuntimeException.class);
    }

    @Test
    void groupInsertMultipleUsersTest() {
        assertThat(GroupConfiguration.getInstance().exists(new TeamKey(uuid1)))
                .isFalse();
        GroupConfiguration.getInstance().insert(team1);
        assertThat(GroupConfiguration.getInstance().exists(new TeamKey(uuid1)))
                .isTrue();
        assertThat(GroupConfiguration.getInstance().get(new TeamKey(uuid1)))
                .isPresent()
                .hasValue(team1);
        GroupConfiguration.getInstance().insert(team2);
        assertThat(GroupConfiguration.getInstance().exists(new TeamKey(uuid2)))
                .isTrue();
        assertThat(GroupConfiguration.getInstance().exists(new TeamKey(uuid1)))
                .isTrue();
        assertThat(GroupConfiguration.getInstance().get(new TeamKey(uuid2)))
                .isPresent()
                .hasValue(team2);
        assertThat(GroupConfiguration.getInstance().get(new TeamKey(uuid1)))
                .isPresent()
                .hasValue(team1);
    }

    @Test
    void groupDeleteDoesNotExistTest() {
        GroupConfiguration.getInstance().delete(team1.getMetadataKey());
    }

    @Test
    void groupDeleteByNameDoesNotExistTest() {
        GroupConfiguration.getInstance().delete(team1.getTeamName());
    }

    @Test
    void groupDeleteTest() {
        GroupConfiguration.getInstance().insert(team1);
        assertThat(GroupConfiguration.getInstance().exists(new TeamKey(uuid1)))
                .isTrue();
        GroupConfiguration.getInstance().delete(team1.getMetadataKey());

        assertThat(GroupConfiguration.getInstance().exists(new TeamKey(uuid1)))
                .isFalse();
    }

    @Test
    void groupDeleteByNameTest() {
        GroupConfiguration.getInstance().insert(team1);
        assertThat(GroupConfiguration.getInstance().exists(new TeamKey(uuid1)))
                .isTrue();
        GroupConfiguration.getInstance().delete(team1.getTeamName());

        assertThat(GroupConfiguration.getInstance().exists(new TeamKey(uuid1)))
                .isFalse();
    }

    @Test
    void userUpdateTest() {
        GroupConfiguration.getInstance().insert(team1);
        Optional<Team> user = GroupConfiguration.getInstance().get(new TeamKey(uuid1));
        assertThat(user)
                .isPresent()
                .hasValue(team1);
        assertThat(user.get().getTeamName())
                .isEqualTo("group1");

        team1.setTeamName("groupA");
        GroupConfiguration.getInstance().update(team1);

        assertThat(GroupConfiguration.getInstance().get(new TeamKey(uuid1)).get().getTeamName())
                .isEqualTo("groupA");
    }


    @Test
    void groupUpdateMultipleTest() {
        GroupConfiguration.getInstance().insert(team1);
        GroupConfiguration.getInstance().insert(team2);
        Optional<Team> fetchedUser1 = GroupConfiguration.getInstance().get(new TeamKey(uuid1));
        assertThat(fetchedUser1)
                .isPresent()
                .hasValue(team1);
        assertThat(fetchedUser1.get().getTeamName())
                .isEqualTo("group1");
        Optional<Team> fetchedUser2 = GroupConfiguration.getInstance().get(new TeamKey(uuid2));
        assertThat(fetchedUser2)
                .isPresent()
                .hasValue(team2);
        assertThat(fetchedUser2.get().getTeamName())
                .isEqualTo("group2");

        team1.setTeamName("groupA");
        GroupConfiguration.getInstance().update(team1);

        assertThat(GroupConfiguration.getInstance().get(new TeamKey(uuid1)).get().getTeamName())
                .isEqualTo("groupA");
        assertThat(GroupConfiguration.getInstance().get(new TeamKey(uuid2)).get().getTeamName())
                .isEqualTo("group2");
    }

    @Test
    void getUsers() {
        GroupConfiguration.getInstance().insert(team1);
        GroupConfiguration.getInstance().insert(team2);
        UserConfiguration.getInstance().insert(user1);
        UserConfiguration.getInstance().insert(user2);
        UserConfiguration.getInstance().insert(user3);
        GroupConfiguration.getInstance().addUser(team1.getMetadataKey(), user1.getMetadataKey());
        GroupConfiguration.getInstance().addUser(team1.getMetadataKey(), user2.getMetadataKey());
        GroupConfiguration.getInstance().addUser(team2.getMetadataKey(), user3.getMetadataKey());

        assertThat(GroupConfiguration.getInstance().getUsers(team1.getMetadataKey()))
                .containsOnly(user1, user2);
        assertThat(GroupConfiguration.getInstance().getUsers(team2.getMetadataKey()))
                .containsOnly(user3);
    }

    @Test
    void getUsersByteamName() {
        GroupConfiguration.getInstance().insert(team1);
        GroupConfiguration.getInstance().insert(team2);
        UserConfiguration.getInstance().insert(user1);
        UserConfiguration.getInstance().insert(user2);
        UserConfiguration.getInstance().insert(user3);
        GroupConfiguration.getInstance().addUser(team1.getMetadataKey(), user1.getMetadataKey());
        GroupConfiguration.getInstance().addUser(team1.getMetadataKey(), user2.getMetadataKey());
        GroupConfiguration.getInstance().addUser(team2.getMetadataKey(), user3.getMetadataKey());

        assertThat(GroupConfiguration.getInstance().getUsers(team1.getTeamName()))
                .containsOnly(user1, user2);
        assertThat(GroupConfiguration.getInstance().getUsers(team2.getTeamName()))
                .containsOnly(user3);
    }

    @Test
    void removeUser() {
        GroupConfiguration.getInstance().insert(team1);
        GroupConfiguration.getInstance().insert(team2);
        UserConfiguration.getInstance().insert(user1);
        UserConfiguration.getInstance().insert(user2);
        UserConfiguration.getInstance().insert(user3);
        GroupConfiguration.getInstance().addUser(team1.getMetadataKey(), user1.getMetadataKey());
        GroupConfiguration.getInstance().addUser(team1.getMetadataKey(), user2.getMetadataKey());
        GroupConfiguration.getInstance().addUser(team2.getMetadataKey(), user3.getMetadataKey());

        assertThat(GroupConfiguration.getInstance().getUsers(team1.getMetadataKey()))
                .containsOnly(user1, user2);
        assertThat(GroupConfiguration.getInstance().getUsers(team2.getMetadataKey()))
                .containsOnly(user3);

        GroupConfiguration.getInstance().removeUser(team1.getMetadataKey(), user1.getMetadataKey());

        assertThat(GroupConfiguration.getInstance().getUsers(team1.getMetadataKey()))
                .containsOnly(user2);
        assertThat(GroupConfiguration.getInstance().getUsers(team2.getMetadataKey()))
                .containsOnly(user3);
    }

    @Test
    void removeUserByName() {
        GroupConfiguration.getInstance().insert(team1);
        GroupConfiguration.getInstance().insert(team2);
        UserConfiguration.getInstance().insert(user1);
        UserConfiguration.getInstance().insert(user2);
        UserConfiguration.getInstance().insert(user3);
        GroupConfiguration.getInstance().addUser(team1.getMetadataKey(), user1.getMetadataKey());
        GroupConfiguration.getInstance().addUser(team1.getMetadataKey(), user2.getMetadataKey());
        GroupConfiguration.getInstance().addUser(team2.getMetadataKey(), user3.getMetadataKey());

        assertThat(GroupConfiguration.getInstance().getUsers(team1.getMetadataKey()))
                .containsOnly(user1, user2);
        assertThat(GroupConfiguration.getInstance().getUsers(team2.getMetadataKey()))
                .containsOnly(user3);

        GroupConfiguration.getInstance().removeUser(team1.getTeamName(), user1.getUsername());

        assertThat(GroupConfiguration.getInstance().getUsers(team1.getMetadataKey()))
                .containsOnly(user2);
        assertThat(GroupConfiguration.getInstance().getUsers(team2.getMetadataKey()))
                .containsOnly(user3);
    }

    @Test
    void removeNonExistingUser() {
        GroupConfiguration.getInstance().insert(team1);
        UserConfiguration.getInstance().insert(user1);
        GroupConfiguration.getInstance().addUser(team1.getMetadataKey(), user1.getMetadataKey());

        GroupConfiguration.getInstance().removeUser(team1.getMetadataKey(), user2.getMetadataKey());
    }

    @Test
    void removeNonExistingUserByName() {
        GroupConfiguration.getInstance().insert(team1);
        UserConfiguration.getInstance().insert(user1);
        GroupConfiguration.getInstance().addUser(team1.getTeamName(), user1.getUsername());

        GroupConfiguration.getInstance().removeUser(team1.getTeamName(), user2.getUsername());
    }

    @Test
    void addAlreadyMemberUser() {
        GroupConfiguration.getInstance().insert(team1);
        UserConfiguration.getInstance().insert(user1);
        GroupConfiguration.getInstance().addUser(team1.getMetadataKey(), user1.getMetadataKey());

        assertThatThrownBy(() -> GroupConfiguration.getInstance().addUser(team1.getMetadataKey(), user1.getMetadataKey()))
                .isInstanceOf(RuntimeException.class);
    }

    @Test
    void addAlreadyMemberUserByName() {
        GroupConfiguration.getInstance().insert(team1);
        UserConfiguration.getInstance().insert(user1);
        GroupConfiguration.getInstance().addUser(team1.getMetadataKey(), user1.getMetadataKey());

        assertThatThrownBy(() -> GroupConfiguration.getInstance().addUser(team1.getTeamName(), user1.getUsername()))
                .isInstanceOf(RuntimeException.class);
    }

    @Test
    void getAuthorities() {
        GroupConfiguration.getInstance().insert(team1);
        GroupConfiguration.getInstance().insert(team2);
        AuthorityConfiguration.getInstance().insert(privilege1);
        AuthorityConfiguration.getInstance().insert(privilege2);
        AuthorityConfiguration.getInstance().insert(privilege3);
        GroupConfiguration.getInstance().addAuthority(team1.getMetadataKey(), privilege1.getMetadataKey());
        GroupConfiguration.getInstance().addAuthority(team1.getMetadataKey(), privilege2.getMetadataKey());
        GroupConfiguration.getInstance().addAuthority(team2.getMetadataKey(), privilege3.getMetadataKey());

        assertThat(GroupConfiguration.getInstance().getAuthorities(team1.getMetadataKey()))
                .containsOnly(privilege1, privilege2);
        assertThat(GroupConfiguration.getInstance().getAuthorities(team2.getMetadataKey()))
                .containsOnly(privilege3);
    }

    @Test
    void getAuthoritiesByName() {
        GroupConfiguration.getInstance().insert(team1);
        GroupConfiguration.getInstance().insert(team2);
        AuthorityConfiguration.getInstance().insert(privilege1);
        AuthorityConfiguration.getInstance().insert(privilege2);
        AuthorityConfiguration.getInstance().insert(privilege3);
        GroupConfiguration.getInstance().addAuthority(team1.getMetadataKey(), privilege1.getMetadataKey());
        GroupConfiguration.getInstance().addAuthority(team1.getMetadataKey(), privilege2.getMetadataKey());
        GroupConfiguration.getInstance().addAuthority(team2.getMetadataKey(), privilege3.getMetadataKey());

        assertThat(GroupConfiguration.getInstance().getAuthorities(team1.getTeamName()))
                .containsOnly(privilege1, privilege2);
        assertThat(GroupConfiguration.getInstance().getAuthorities(team2.getTeamName()))
                .containsOnly(privilege3);
    }

    @Test
    void removeAuthority() {
        GroupConfiguration.getInstance().insert(team1);
        GroupConfiguration.getInstance().insert(team2);
        AuthorityConfiguration.getInstance().insert(privilege1);
        AuthorityConfiguration.getInstance().insert(privilege2);
        AuthorityConfiguration.getInstance().insert(privilege3);
        GroupConfiguration.getInstance().addAuthority(team1.getMetadataKey(), privilege1.getMetadataKey());
        GroupConfiguration.getInstance().addAuthority(team1.getMetadataKey(), privilege2.getMetadataKey());
        GroupConfiguration.getInstance().addAuthority(team2.getMetadataKey(), privilege3.getMetadataKey());

        assertThat(GroupConfiguration.getInstance().getAuthorities(team1.getMetadataKey()))
                .containsOnly(privilege1, privilege2);
        assertThat(GroupConfiguration.getInstance().getAuthorities(team2.getMetadataKey()))
                .containsOnly(privilege3);

        GroupConfiguration.getInstance().removeAuthority(team1.getMetadataKey(), privilege1.getMetadataKey());

        assertThat(GroupConfiguration.getInstance().getAuthorities(team1.getMetadataKey()))
                .containsOnly(privilege2);
        assertThat(GroupConfiguration.getInstance().getAuthorities(team2.getMetadataKey()))
                .containsOnly(privilege3);
    }

    @Test
    void removeAuthorityByName() {
        GroupConfiguration.getInstance().insert(team1);
        GroupConfiguration.getInstance().insert(team2);
        AuthorityConfiguration.getInstance().insert(privilege1);
        AuthorityConfiguration.getInstance().insert(privilege2);
        AuthorityConfiguration.getInstance().insert(privilege3);
        GroupConfiguration.getInstance().addAuthority(team1.getMetadataKey(), privilege1.getMetadataKey());
        GroupConfiguration.getInstance().addAuthority(team1.getMetadataKey(), privilege2.getMetadataKey());
        GroupConfiguration.getInstance().addAuthority(team2.getMetadataKey(), privilege3.getMetadataKey());

        assertThat(GroupConfiguration.getInstance().getAuthorities(team1.getMetadataKey()))
                .containsOnly(privilege1, privilege2);
        assertThat(GroupConfiguration.getInstance().getAuthorities(team2.getMetadataKey()))
                .containsOnly(privilege3);

        GroupConfiguration.getInstance().removeAuthority(team1.getTeamName(), privilege1.getAuthority());

        assertThat(GroupConfiguration.getInstance().getAuthorities(team1.getMetadataKey()))
                .containsOnly(privilege2);
        assertThat(GroupConfiguration.getInstance().getAuthorities(team2.getMetadataKey()))
                .containsOnly(privilege3);
    }

    @Test
    void removeNonExistingAuthority() {
        GroupConfiguration.getInstance().insert(team1);
        AuthorityConfiguration.getInstance().insert(privilege1);
        GroupConfiguration.getInstance().addAuthority(team1.getMetadataKey(), privilege1.getMetadataKey());

        GroupConfiguration.getInstance().removeAuthority(team1.getMetadataKey(), privilege2.getMetadataKey());
    }

    @Test
    void removeNonExistingAuthorityByName() {
        GroupConfiguration.getInstance().insert(team1);
        AuthorityConfiguration.getInstance().insert(privilege1);
        GroupConfiguration.getInstance().addAuthority(team1.getMetadataKey(), privilege1.getMetadataKey());

        GroupConfiguration.getInstance().removeAuthority(team1.getTeamName(), privilege2.getAuthority());
    }

    @Test
    void addAlreadyAuthority() {
        GroupConfiguration.getInstance().insert(team1);
        AuthorityConfiguration.getInstance().insert(privilege1);
        GroupConfiguration.getInstance().addAuthority(team1.getMetadataKey(), privilege1.getMetadataKey());

        assertThatThrownBy(() -> GroupConfiguration.getInstance().addAuthority(team1.getMetadataKey(), privilege1.getMetadataKey()))
                .isInstanceOf(RuntimeException.class);
    }

    @Test
    void addAlreadyAuthorityByName() {
        GroupConfiguration.getInstance().insert(team1);
        AuthorityConfiguration.getInstance().insert(privilege1);
        GroupConfiguration.getInstance().addAuthority(team1.getMetadataKey(), privilege1.getMetadataKey());

        assertThatThrownBy(() -> GroupConfiguration.getInstance().addAuthority(team1.getTeamName(), privilege1.getAuthority()))
                .isInstanceOf(RuntimeException.class);
    }


}
