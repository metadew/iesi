package io.metadew.iesi.metadata.configuration.user;

import io.metadew.iesi.common.configuration.Configuration;
import io.metadew.iesi.common.configuration.metadata.repository.MetadataRepositoryConfiguration;
import io.metadew.iesi.metadata.definition.user.Authority;
import io.metadew.iesi.metadata.definition.user.AuthorityKey;
import org.junit.jupiter.api.*;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class AuthorityConfigurationTest {

    private UUID uuid1;
    private Authority authority1;
    private UUID uuid2;
    private Authority authority2;

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
        authority1 = Authority.builder()
                .authorityKey(AuthorityKey.builder()
                        .uuid(uuid1)
                        .build())
                .authority("authority1")
                .build();
        uuid2 = UUID.randomUUID();
        authority2 = Authority.builder()
                .authorityKey(AuthorityKey.builder()
                        .uuid(uuid2)
                        .build())
                .authority("authority2")
                .build();
    }

    @AfterEach
    void clearDatabase() throws Exception{
        MetadataRepositoryConfiguration.getInstance()
                .getControlMetadataRepository().cleanAllTables();
    }

    @AfterAll
    static void teardown() throws Exception{
        MetadataRepositoryConfiguration.getInstance()
                .getControlMetadataRepository().dropAllTables();
    }

    @Test
    void userDoesNotExistsTest() throws Exception{
        assertThat(AuthorityConfiguration.getInstance().exists(new AuthorityKey(uuid1))).isFalse();
    }

    @Test
    void userExistsTest() throws Exception{
        AuthorityConfiguration.getInstance().insert(authority1);
        assertThat(AuthorityConfiguration.getInstance().exists(new AuthorityKey(uuid1))).isTrue();
    }

    @Test
    void userExistsByNameTest() throws Exception{
        AuthorityConfiguration.getInstance().insert(authority1);
        assertThat(AuthorityConfiguration.getInstance().exists(authority1.getAuthority())).isTrue();
    }

    @Test
    void userGetDoesNotExistsTest() throws Exception{
        assertThat(AuthorityConfiguration.getInstance().get(new AuthorityKey(uuid1))).isEmpty();
        AuthorityConfiguration.getInstance().insert(authority1);
        assertThat(AuthorityConfiguration.getInstance().get(new AuthorityKey(uuid2))).isEmpty();
    }

    @Test
    void userGetByNameDoesNotExistsTest() throws Exception{
        assertThat(AuthorityConfiguration.getInstance().get(new AuthorityKey(uuid1))).isEmpty();
        AuthorityConfiguration.getInstance().insert(authority1);
        assertThat(AuthorityConfiguration.getInstance().get(authority2.getAuthority())).isEmpty();
    }

    @Test
    void userGetExistsTest() throws Exception{
        AuthorityConfiguration.getInstance().insert(authority1);
        assertThat(AuthorityConfiguration.getInstance().get(new AuthorityKey(uuid1)))
                .isPresent()
                .hasValue(authority1);
    }

    @Test
    void userGetByNameExistsTest() throws Exception{
        AuthorityConfiguration.getInstance().insert(authority1);
        assertThat(AuthorityConfiguration.getInstance().get(authority1.getAuthority()))
                .isPresent()
                .hasValue(authority1);
    }

    @Test
    void userInsertTest()throws Exception {
        assertThat(AuthorityConfiguration.getInstance().exists(new AuthorityKey(uuid1)))
                .isFalse();
        AuthorityConfiguration.getInstance().insert(authority1);
        assertThat(AuthorityConfiguration.getInstance().exists(new AuthorityKey(uuid1)))
                .isTrue();
        assertThat(AuthorityConfiguration.getInstance().get(new AuthorityKey(uuid1)))
                .isPresent()
                .hasValue(authority1);
    }

    @Test
    void userInsertAlreadyExistingTest() throws Exception{
        AuthorityConfiguration.getInstance().insert(authority1);
        assertThatThrownBy(() -> AuthorityConfiguration.getInstance().insert(authority1))
                .isInstanceOf(RuntimeException.class);
    }

    @Test
    void userInsertMultipleUsersTest() throws Exception{
        assertThat(AuthorityConfiguration.getInstance().exists(new AuthorityKey(uuid1)))
                .isFalse();
        AuthorityConfiguration.getInstance().insert(authority1);
        assertThat(AuthorityConfiguration.getInstance().exists(new AuthorityKey(uuid1)))
                .isTrue();
        assertThat(AuthorityConfiguration.getInstance().get(new AuthorityKey(uuid1)))
                .isPresent()
                .hasValue(authority1);
        AuthorityConfiguration.getInstance().insert(authority2);
        assertThat(AuthorityConfiguration.getInstance().exists(new AuthorityKey(uuid2)))
                .isTrue();
        assertThat(AuthorityConfiguration.getInstance().exists(new AuthorityKey(uuid1)))
                .isTrue();
        assertThat(AuthorityConfiguration.getInstance().get(new AuthorityKey(uuid2)))
                .isPresent()
                .hasValue(authority2);
        assertThat(AuthorityConfiguration.getInstance().get(new AuthorityKey(uuid1)))
                .isPresent()
                .hasValue(authority1);
    }

    @Test
    void userDeleteDoesNotExistTest()throws Exception {
        AuthorityConfiguration.getInstance().delete(authority1.getMetadataKey());
    }

    @Test
    void userDeleteByNameDoesNotExistTest()throws Exception {
        AuthorityConfiguration.getInstance().delete(authority1.getAuthority());
    }

    @Test
    void userDeleteTest() throws Exception{
        AuthorityConfiguration.getInstance().insert(authority1);
        assertThat(AuthorityConfiguration.getInstance().exists(new AuthorityKey(uuid1)))
                .isTrue();
        AuthorityConfiguration.getInstance().delete(authority1.getMetadataKey());

        assertThat(AuthorityConfiguration.getInstance().exists(new AuthorityKey(uuid1)))
                .isFalse();
    }

    @Test
    void userDeleteByNameTest() throws Exception{
        AuthorityConfiguration.getInstance().insert(authority1);
        assertThat(AuthorityConfiguration.getInstance().exists(new AuthorityKey(uuid1)))
                .isTrue();
        AuthorityConfiguration.getInstance().delete(authority1.getAuthority());

        assertThat(AuthorityConfiguration.getInstance().exists(new AuthorityKey(uuid1)))
                .isFalse();
    }

    @Test
    void userUpdateTest()throws Exception {
        AuthorityConfiguration.getInstance().insert(authority1);
        Optional<Authority> user = AuthorityConfiguration.getInstance().get(new AuthorityKey(uuid1));
        assertThat(user)
                .isPresent()
                .hasValue(authority1);
        assertThat(user.get().getAuthority())
                .isEqualTo("authority1");

        authority1.setAuthority("authorityA");
        AuthorityConfiguration.getInstance().update(authority1);

        assertThat(AuthorityConfiguration.getInstance().get(new AuthorityKey(uuid1)).get().getAuthority())
                .isEqualTo("authorityA");
    }


    @Test
    void userUpdateMultipleTest() throws Exception{
        AuthorityConfiguration.getInstance().insert(authority1);
        AuthorityConfiguration.getInstance().insert(authority2);
        Optional<Authority> fetchedUser1 = AuthorityConfiguration.getInstance().get(new AuthorityKey(uuid1));
        assertThat(fetchedUser1)
                .isPresent()
                .hasValue(authority1);
        assertThat(fetchedUser1.get().getAuthority())
                .isEqualTo("authority1");
        Optional<Authority> fetchedUser2 = AuthorityConfiguration.getInstance().get(new AuthorityKey(uuid2));
        assertThat(fetchedUser2)
                .isPresent()
                .hasValue(authority2);
        assertThat(fetchedUser2.get().getAuthority())
                .isEqualTo("authority2");

        authority1.setAuthority("authorityA");
        AuthorityConfiguration.getInstance().update(authority1);

        assertThat(AuthorityConfiguration.getInstance().get(new AuthorityKey(uuid1)).get().getAuthority())
                .isEqualTo("authorityA");
        assertThat(AuthorityConfiguration.getInstance().get(new AuthorityKey(uuid2)).get().getAuthority())
                .isEqualTo("authority2");
    }

}
