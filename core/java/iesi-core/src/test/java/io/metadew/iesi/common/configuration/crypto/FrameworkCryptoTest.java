package io.metadew.iesi.common.configuration.crypto;

import io.metadew.iesi.common.configuration.Configuration;
import io.metadew.iesi.common.crypto.FrameworkCrypto;
import io.metadew.iesi.common.crypto.JavaKeystore;
import io.metadew.iesi.common.crypto.algo.AESEncryptBasic;
import org.junit.Test;
import org.powermock.core.classloader.annotations.PrepareForTest;

import java.io.ByteArrayInputStream;
import java.util.Scanner;

import static junit.framework.TestCase.assertEquals;

@PrepareForTest(FrameworkCrypto.class)
public class FrameworkCryptoTest {
    private AESEncryptBasic aes;

    @Test
    public void testJavaKeystore() throws Exception {
        Configuration configuration = Configuration.getInstance();
        if (configuration.getProperty("iesi.security.encryption.type").isPresent()) {
            String password = "foobar";
            System.setIn(new ByteArrayInputStream(password.getBytes()));
            Scanner scanner = new Scanner(System.in);
            String userinput = scanner.nextLine();
            String keyJKS = new JavaKeystore().loadKey(userinput);
            this.aes = new AESEncryptBasic(keyJKS);
            assertEquals("c7c1e47391154a6a", keyJKS);
        }
    }

    @Test
    public void testJavaKeystoreWrongPassword() throws Exception {
        Configuration configuration = Configuration.getInstance();
        if (configuration.getProperty("iesi.security.encryption.type").isPresent()) {
            String password = "fooar";
            System.setIn(new ByteArrayInputStream(password.getBytes()));
            Scanner scanner = new Scanner(System.in);
            String userinput = scanner.nextLine();
            try {
                new JavaKeystore().loadKey(userinput);
            } catch (Exception e) {
                assertEquals("Integrity check failed: java.security.UnrecoverableKeyException: Failed PKCS12 integrity checking", e.getMessage());
            }
        }
    }

    @Test
    public void testKeyInConf() {
        Configuration configuration = Configuration.getInstance();
        if (configuration.getProperty("iesi.security.encryption.key").isPresent()) {
            assertEquals("c7c1e47391154a6a", configuration.getProperty("iesi.security.encryption.key").get().toString());
        }
    }
}
