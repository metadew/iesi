package io.metadew.iesi.common.crypto;

import org.apache.commons.io.FileUtils;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableEntryException;
import java.security.cert.CertificateException;
import java.security.spec.InvalidKeySpecException;

/**
 * Command line to create a Keystore:
 * For example, to store the sensitive encryption key <c7c1e47391154a6a> in the <mypass> alias in the keystore named <myks.p12> located on your Desktop
 * protected with the password <foobar>, do the following:
 *
 * keytool -importpass -storetype pkcs12 -alias mypass -keystore C:\\user.dir\\Desktop\\myks.p12
 * Enter keystore password: <foobar>
 * Re-enter new password: <foobar>
 * Enter the password to be stored: <c7c1e47391154a6a>
 * Re-enter password: <c7c1e47391154a6a>
 */

public class JavaKeystore {

    public String loadKey(char[] keyStorePassword, String keystoreLocation, String alias) {
        try {
            KeyStore ks = KeyStore.getInstance("PKCS12");

            File fIn = FileUtils.getFile(keystoreLocation);
            FileInputStream fisPublic = new FileInputStream(fIn);
            ks.load(fisPublic, keyStorePassword);

            SecretKeyFactory factory = SecretKeyFactory.getInstance("PBE");
            KeyStore.SecretKeyEntry ske = (KeyStore.SecretKeyEntry) ks.getEntry(alias,
                    new KeyStore.PasswordProtection(keyStorePassword));

            PBEKeySpec keySpec = (PBEKeySpec) factory.getKeySpec(
                    ske.getSecretKey(), PBEKeySpec.class);

            return new String(keySpec.getPassword());
        } catch (IOException | CertificateException | KeyStoreException | NoSuchAlgorithmException | UnrecoverableEntryException | InvalidKeySpecException e) {
            throw new RuntimeException(e);
        }
    }
}