package io.metadew.iesi.common.crypto;

import io.metadew.iesi.common.configuration.Configuration;
import io.metadew.iesi.connection.database.DatabaseHandler;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.reflect.Whitebox;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;


class FrameworkCryptoTest {

    @BeforeAll
    static void setup() {
    }
<
    @Test
    void testHardcodeKey() throws Exception {
        // computed with https://www.devglan.com/online-tools/aes-encryption-decryption
        assertThat(FrameworkCrypto.getInstance().encrypt("teststring")).isEqualTo("ENC(whk8wC3/8tr/tqiUFop2jA==)");
        assertThat(FrameworkCrypto.getInstance().decrypt("ENC(whk8wC3/8tr/tqiUFop2jA==)")).isEqualTo("teststring");

        Whitebox.setInternalState(FrameworkCrypto.class, "INSTANCE", (FrameworkCrypto) null);
    }

    @Test
    void testConfigurationKey() {
        // mock read password
        Configuration configuration = Configuration.getInstance();
        Configuration configurationSpy = Mockito.spy(configuration);
        Whitebox.setInternalState(Configuration.class, "INSTANCE", configurationSpy);
        Mockito.doReturn(Optional.of("y8c1e47391154a6c")).when(configurationSpy).getProperty("iesi.security.encryption.key");
        Mockito.doReturn("y8c1e47391154a6c").when(configurationSpy).getMandatoryProperty("iesi.security.encryption.key");

        // computed with https://www.devglan.com/online-tools/aes-encryption-decryption
        assertThat(FrameworkCrypto.getInstance().encrypt("teststring")).isEqualTo("ENC(Tzjjbi1xovFr0Ax9Xbje+g==)");
        assertThat(FrameworkCrypto.getInstance().decrypt("ENC(Tzjjbi1xovFr0Ax9Xbje+g==)")).isEqualTo("teststring");

        Whitebox.setInternalState(Configuration.class, "INSTANCE", (Configuration) null);
        Whitebox.setInternalState(FrameworkCrypto.class, "INSTANCE", (FrameworkCrypto) null);
    }

    @Test
    void testJavaKeystoreKey() {
        // mock read password
        Console console = Console.getInstance();
        Console consoleSpy = Mockito.spy(console);
        Whitebox.setInternalState(Console.class, "INSTANCE", consoleSpy);
        Mockito.doReturn("abc".toCharArray()).when(consoleSpy).readPassword(any());

        // computed with https://www.devglan.com/online-tools/aes-encryption-decryption
        assertThat(FrameworkCrypto.getInstance().encrypt("teststring")).isEqualTo("ENC(+COX3DFR3IWeBvn6seDtWg==)");
        assertThat(FrameworkCrypto.getInstance().decrypt("ENC(+COX3DFR3IWeBvn6seDtWg==)")).isEqualTo("teststring");
    }

}