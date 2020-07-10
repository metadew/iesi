package io.metadew.iesi.common.configuration;

import java.io.*;
import java.security.KeyStore;

import io.metadew.iesi.common.crypto.JavaKeystore;
import org.apache.commons.io.FileUtils;
import org.junit.Test;
import org.powermock.core.classloader.annotations.PrepareForTest;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.util.Scanner;

import static junit.framework.TestCase.assertEquals;

@PrepareForTest(JavaKeystore.class)
public class JavaKeystoreTest {

    @Test
    public void testEncryptKey() throws Exception {
        Configuration configuration = Configuration.getInstance();
        String password = "foobar";
        System.setIn(new ByteArrayInputStream(password.getBytes()));
        Scanner scanner = new Scanner(System.in);
        String userinput = scanner.nextLine();
        KeyStore ks = KeyStore.getInstance("PKCS12");

        String currentDirectory = System.getProperty("user.dir");
        File fIn = FileUtils.getFile(currentDirectory + "/src/test/resources" + configuration.getProperty("iesi.security.encryption.keystore-path").get().toString());
        FileInputStream fisPublic = new FileInputStream(fIn);
        ks.load(fisPublic, userinput.toCharArray());
        KeyStore.SecretKeyEntry ske = (KeyStore.SecretKeyEntry) ks.getEntry(configuration.getProperty("iesi.security.encryption.alias").get().toString(),
                new KeyStore.PasswordProtection(password.toCharArray()));
        SecretKeyFactory factory = SecretKeyFactory.getInstance("PBE");
        PBEKeySpec keySpec = (PBEKeySpec) factory.getKeySpec(
                ske.getSecretKey(), PBEKeySpec.class);
        assertEquals("c7c1e47391154a6a", new String(keySpec.getPassword()));
    }

    @Test
    public void testEncryptWrongAlias() throws Exception {
        Configuration configuration = Configuration.getInstance();
        String password = "foobar";
        System.setIn(new ByteArrayInputStream(password.getBytes()));
        Scanner scanner = new Scanner(System.in);
        String userinput = scanner.nextLine();
        KeyStore ks = KeyStore.getInstance("PKCS12");

        String currentDirectory = System.getProperty("user.dir");
        File fIn = FileUtils.getFile(currentDirectory + "/src/test/resources" + configuration.getProperty("iesi.security.encryption.keystore-path").get().toString());
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
