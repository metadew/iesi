package io.metadew.iesi.common.crypto;

import io.metadew.iesi.common.configuration.Configuration;
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

    public String loadKey(String keyStorePassword, String keystoreLocation) throws KeyStoreException, NoSuchAlgorithmException, UnrecoverableEntryException, InvalidKeySpecException, IOException, CertificateException {
        Configuration configuration = Configuration.getInstance();
        KeyStore ks = KeyStore.getInstance("PKCS12");
        String alias = configuration.getProperty("iesi.security.encryption.alias").get().toString();

        File fIn = FileUtils.getFile(keystoreLocation);
        FileInputStream fisPublic = new FileInputStream(fIn);
        ks.load(fisPublic, keyStorePassword.toCharArray());

        SecretKeyFactory factory = SecretKeyFactory.getInstance("PBE");
        KeyStore.SecretKeyEntry ske = (KeyStore.SecretKeyEntry) ks.getEntry(alias,
                new KeyStore.PasswordProtection(keyStorePassword.toCharArray()));

        PBEKeySpec keySpec = (PBEKeySpec) factory.getKeySpec(
                ske.getSecretKey(), PBEKeySpec.class);

        return new String(keySpec.getPassword());
    }
}
