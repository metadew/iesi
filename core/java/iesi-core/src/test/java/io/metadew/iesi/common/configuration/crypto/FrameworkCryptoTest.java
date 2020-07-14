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

    @Test
    public void testJavaKeystore() throws Exception {
        String password = "foobar";
        String currentDirectory = System.getProperty("user.dir");
        String keystoreLocation = currentDirectory + "/src/test/resources/" + Configuration.getInstance().getMandatoryProperty("iesi.security.encryption.keystore-path").toString();

        System.setIn(new ByteArrayInputStream(password.getBytes()));
        Scanner scanner = new Scanner(System.in);
        String userinput = scanner.nextLine();
        String alias = "mypass";
        String keyJKS = new JavaKeystore().loadKey(userinput.toCharArray(), keystoreLocation, alias);
        assertEquals("c7c1e47391154a6a", keyJKS);
    }

    @Test
    public void testJavaKeystoreWrongPassword() {
        String password = "fooar";
        String currentDirectory = System.getProperty("user.dir");
        String keystoreLocation = currentDirectory + "/src/test/resources/" + Configuration.getInstance().getMandatoryProperty("iesi.security.encryption.keystore-path").toString();

        System.setIn(new ByteArrayInputStream(password.getBytes()));
        Scanner scanner = new Scanner(System.in);
        String userinput = scanner.nextLine();
        String alias = "mypass";
        try {
            new JavaKeystore().loadKey(userinput.toCharArray(), keystoreLocation, alias);
        } catch (Exception e) {
            assertEquals("Integrity check failed: java.security.UnrecoverableKeyException: Failed PKCS12 integrity checking", e.getMessage());
        }
    }

    @Test
    public void testKeyInConf() {
        if (Configuration.getInstance().getProperty("iesi.security.encryption.key").isPresent()) {
            assertEquals("c7c1e47391154a6a", Configuration.getInstance().getMandatoryProperty("iesi.security.encryption.key").toString());
        }
    }
}
