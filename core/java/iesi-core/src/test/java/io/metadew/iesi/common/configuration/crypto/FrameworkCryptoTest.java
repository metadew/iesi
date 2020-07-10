package io.metadew.iesi.common.configuration.crypto;

import io.metadew.iesi.common.configuration.Configuration;
import io.metadew.iesi.common.crypto.FrameworkCrypto;
import io.metadew.iesi.common.crypto.JavaKeystore;
import io.metadew.iesi.common.crypto.algo.AESEncryptBasic;
import org.apache.commons.io.FileUtils;
import org.junit.Test;
import org.powermock.core.classloader.annotations.PrepareForTest;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.util.Scanner;

import static junit.framework.TestCase.assertEquals;

@PrepareForTest(FrameworkCrypto.class)
public class FrameworkCryptoTest {
    public AESEncryptBasic aes;

    @Test
    public void testJavaKeystore() throws Exception {
        Configuration configuration = Configuration.getInstance();
        if (configuration.getProperty("iesi.security.encryption.alias").isPresent()) {
            String password = "foobar";
            String currentDirectory = System.getProperty("user.dir");
            String keystoreLocation = currentDirectory + "/src/test/resources" + configuration.getProperty("iesi.security.encryption.keystore-path").get().toString();

            System.setIn(new ByteArrayInputStream(password.getBytes()));
            Scanner scanner = new Scanner(System.in);
            String userinput = scanner.nextLine();
            String keyJKS = new JavaKeystore().loadKey(userinput, keystoreLocation);
            this.aes = new AESEncryptBasic(keyJKS);
            assertEquals("c7c1e47391154a6a", keyJKS);
        }
    }

    @Test
    public void testJavaKeystoreWrongPassword() {
        Configuration configuration = Configuration.getInstance();
        if (configuration.getProperty("iesi.security.encryption.alias").get() != null) {
            String password = "fooar";
            String currentDirectory = System.getProperty("user.dir");
            String keystoreLocation = currentDirectory + "/src/test/resources" + configuration.getProperty("iesi.security.encryption.keystore-path").get().toString();

            System.setIn(new ByteArrayInputStream(password.getBytes()));
            Scanner scanner = new Scanner(System.in);
            String userinput = scanner.nextLine();
            try {
                new JavaKeystore().loadKey(userinput, keystoreLocation);
            } catch (Exception e) {
                assertEquals("Integrity check failed: java.security.UnrecoverableKeyException: Failed PKCS12 integrity checking", e.getMessage());
            }
        }
    }

    @Test
    public void testKeyInConf() {
        Configuration configuration = Configuration.getInstance();
        if (configuration.getProperty("iesi.security.encryption.key").get() != null) {
            assertEquals("c7c1e47391154a6a", configuration.getProperty("iesi.security.encryption.key").get().toString());
        }
    }
}
