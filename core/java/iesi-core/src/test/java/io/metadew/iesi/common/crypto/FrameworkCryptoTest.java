package io.metadew.iesi.common.crypto;

import io.metadew.iesi.common.configuration.Configuration;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.io.IOException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(PowerMockRunner.class)
@PrepareForTest(FrameworkCrypto.class)

public class FrameworkCryptoTest {

    @Test
    public void testKeyInConf() throws CertificateException, NoSuchAlgorithmException, IOException {
//        char[] password = y.toCharArray();
//        String x = new JavaKeystore().loadKey(password,"C:/Users/francois.heliodore/Desktop/IESI2/iesi/core/java/iesi-core/src/test/resources/myks.p12","mypass");
        FrameworkCrypto x = PowerMockito.mock(FrameworkCrypto.class);

        PowerMockito.when(FrameworkCrypto.getInstance()).thenReturn(x);
        assertThat("y8c1e47391154a6c").isEqualTo(Configuration.getInstance().getMandatoryProperty("iesi.security.encryption.key").toString());
    }
}