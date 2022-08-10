package io.metadew.iesi.common.crypto;

import io.metadew.iesi.SpringContext;
import io.metadew.iesi.common.configuration.Configuration;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.powermock.reflect.Whitebox;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;

@SpringBootTest(classes = { Configuration.class, SpringContext.class, FrameworkCrypto.class })
class FrameworkCryptoTest {

    @MockBean
    Configuration configuration;

    @Autowired
    FrameworkCrypto frameworkCrypto;

    @BeforeAll
    static void setup() {
    }

    @Test
    void testHardcodeKey() {
        // computed with https://www.devglan.com/online-tools/aes-encryption-decryption
        assertThat(frameworkCrypto.encrypt("teststring")).isEqualTo("ENC(whk8wC3/8tr/tqiUFop2jA==)");
        assertThat(frameworkCrypto.decrypt("ENC(whk8wC3/8tr/tqiUFop2jA==)")).isEqualTo("teststring");
    }

    @Test
    void testConfigurationKey() {
        Mockito.doReturn(Optional.of("y8c1e47391154a6c")).when(configuration).getProperty("iesi.security.encryption.key");
        Mockito.doReturn("y8c1e47391154a6c").when(configuration).getMandatoryProperty("iesi.security.encryption.key");

        // computed with https://www.devglan.com/online-tools/aes-encryption-decryption
        assertThat(frameworkCrypto.encrypt("teststring")).isEqualTo("ENC(Tzjjbi1xovFr0Ax9Xbje+g==)");
        assertThat(frameworkCrypto.decrypt("ENC(Tzjjbi1xovFr0Ax9Xbje+g==)")).isEqualTo("teststring");
    }

    @Test
    void testJavaKeystoreKey() {
        // mock read password
        Console console = Console.getInstance();
        Console consoleSpy = Mockito.spy(console);
        Mockito.doReturn("foobar".toCharArray()).when(consoleSpy).readPassword(any());

        Mockito.doReturn(Optional.of("mypass")).when(configuration).getProperty("iesi.security.encryption.alias");
        String currentDirectory = System.getProperty("user.dir");
        Mockito.doReturn(Optional.of(currentDirectory + "/src/test/resources/myks.p12")).when(configuration).getProperty("iesi.security.encryption.keystore-path");

        // computed with https://www.devglan.com/online-tools/aes-encryption-decryption
        assertThat(frameworkCrypto.encrypt("teststring")).isEqualTo("ENC(+COX3DFR3IWeBvn6seDtWg==)");
        assertThat(frameworkCrypto.decrypt("ENC(+COX3DFR3IWeBvn6seDtWg==)")).isEqualTo("teststring");

        Whitebox.setInternalState(FrameworkCrypto.class, "INSTANCE", (FrameworkCrypto) null);
    }
}