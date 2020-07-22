package io.metadew.iesi.common.crypto;

import org.apache.commons.io.FileUtils;
import org.junit.Test;
import org.powermock.core.classloader.annotations.PrepareForTest;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.security.KeyStore;
import java.util.Scanner;

import static junit.framework.TestCase.assertEquals;

@PrepareForTest(JavaKeystore.class)
public class JavaKeystoreTest {

    @Test
    public void testEncryptKey() throws Exception {
        String password = "foobar";
        System.setIn(new ByteArrayInputStream(password.getBytes()));
        Scanner scanner = new Scanner(System.in);
        String userinput = scanner.nextLine();
        KeyStore ks = KeyStore.getInstance("PKCS12");

        String currentDirectory = System.getProperty("user.dir");
        File fIn = FileUtils.getFile(currentDirectory + "/src/test/resources/myks.p12");
        FileInputStream fisPublic = new FileInputStream(fIn);
        ks.load(fisPublic, userinput.toCharArray());
        String alias = "mypass";
        KeyStore.SecretKeyEntry ske = (KeyStore.SecretKeyEntry) ks.getEntry(alias,
                new KeyStore.PasswordProtection(password.toCharArray()));
        SecretKeyFactory factory = SecretKeyFactory.getInstance("PBE");
        PBEKeySpec keySpec = (PBEKeySpec) factory.getKeySpec(
                ske.getSecretKey(), PBEKeySpec.class);
        assertEquals("c7c1e47391154a6a", new String(keySpec.getPassword()));
    }

    @Test
    public void testEncryptWrongAlias() throws Exception {
        String password = "foobar";
        System.setIn(new ByteArrayInputStream(password.getBytes()));
        Scanner scanner = new Scanner(System.in);
        String userinput = scanner.nextLine();
        KeyStore ks = KeyStore.getInstance("PKCS12");

        String currentDirectory = System.getProperty("user.dir");
        File fIn = FileUtils.getFile(currentDirectory + "/src/test/resources/myks.p12");
        FileInputStream fisPublic = new FileInputStream(fIn);
        ks.load(fisPublic, userinput.toCharArray());
        String alias = "mypas";
        KeyStore.SecretKeyEntry ske = (KeyStore.SecretKeyEntry) ks.getEntry(alias,
                new KeyStore.PasswordProtection(password.toCharArray()));
        SecretKeyFactory factory = SecretKeyFactory.getInstance("PBE");
        try {
            factory.getKeySpec(
                    ske.getSecretKey(), PBEKeySpec.class);
        } catch (NullPointerException e) {
            assertEquals(null, e.getMessage());
        }
    }
}