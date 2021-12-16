package io.metadew.iesi.server.rest.encrypter;

import io.metadew.iesi.common.crypto.FrameworkCrypto;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.stereotype.Service;

/**
 * The encryption service is used to encrypt text through FrameworkCrypto.
 * @author Suyash.d.jain
 */

@Service
@ConditionalOnWebApplication
public class EncryptionService implements IEncryptionService{

    private FrameworkCrypto frameworkCrypto;

    private EncryptionService(FrameworkCrypto frameworkCrypto) {
        this.frameworkCrypto = frameworkCrypto;
    }

    @Override
    public String getEncryptedText(String text) {
        return frameworkCrypto.encrypt(text);
    }

}
