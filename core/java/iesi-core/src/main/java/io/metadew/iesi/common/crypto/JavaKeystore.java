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

//keytool -importpass -storetype pkcs12 -alias mypass -keystore Desktop/myks.p12
public class JavaKeystore {

    public String loadKey(char[] keyStorePassword, String keystoreLocation, String alias) throws KeyStoreException, IOException, CertificateException, NoSuchAlgorithmException, UnrecoverableEntryException, InvalidKeySpecException {
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
    }
}