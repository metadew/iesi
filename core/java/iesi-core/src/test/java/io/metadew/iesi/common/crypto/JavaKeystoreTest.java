package io.metadew.iesi.common.crypto;

import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.Test;
import org.powermock.core.classloader.annotations.PrepareForTest;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.security.KeyStore;
import java.util.Scanner;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@PrepareForTest(JavaKeystore.class)
class JavaKeystoreTest {

    @Test
    void testEncryptKey() throws Exception {
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
        assertThat(new String(keySpec.getPassword())).isEqualTo("c7c1e47391154a6a");
    }

    @Test
    void testEncryptWrongAlias() throws Exception {
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
        assertThatThrownBy(() -> {
            factory.getKeySpec(
                    ske.getSecretKey(), PBEKeySpec.class);
        }).isInstanceOf(NullPointerException.class);
    }

    @Test
    void testJavaKeystore() throws Exception {
        String password = "foobar";
        // Configuration configuration = Configuration.getInstance();
        // Configuration configurationSpy = Mockito.spy(configuration);
        // Whitebox.setInternalState(Configuration.class, "INSTANCE", configurationSpy);
        // Mockito.doReturn(Optional.of("myks.p12")).when(configurationSpy).getProperty("iesi.security.encryption.keystore-path");
        String currentDirectory = System.getProperty("user.dir");
        // String keystoreLocation = currentDirectory + "/src/test/resources/" + Configuration.getInstance().getMandatoryProperty("iesi.security.encryption.keystore-path").toString();

        System.setIn(new ByteArrayInputStream(password.getBytes()));
        Scanner scanner = new Scanner(System.in);
        String userinput = scanner.nextLine();
        String alias = "mypass";
        // String keyJKS = new JavaKeystore().loadKey(userinput.toCharArray(), keystoreLocation, alias);
        // assertThat(keyJKS).isEqualTo("c7c1e47391154a6a");
    }

    @Test
    void testJavaKeystoreWrongPassword() {
        String password = "fooar";
        // Configuration configuration = Configuration.getInstance();
        // Configuration configurationSpy = Mockito.spy(configuration);
        // Whitebox.setInternalState(Configuration.class, "INSTANCE", configurationSpy);
        // Mockito.doReturn(Optional.of("myks.p12")).when(configurationSpy).getProperty("iesi.security.encryption.keystore-path");
        String currentDirectory = System.getProperty("user.dir");
        // String keystoreLocation = currentDirectory + "/src/test/resources/" + Configuration.getInstance().getMandatoryProperty("iesi.security.encryption.keystore-path").toString();

        System.setIn(new ByteArrayInputStream(password.getBytes()));
        Scanner scanner = new Scanner(System.in);
        String userinput = scanner.nextLine();
        String alias = "mypass";
        // assertThatThrownBy(() -> {
        // new JavaKeystore().loadKey(userinput.toCharArray(), keystoreLocation, alias);
        // }).isInstanceOf(Exception.class);
    }
}