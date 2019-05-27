package io.metadew.iesi.framework.crypto.algo;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.security.Key;
import java.util.Base64;

public class AESEncrypt {
    private static final String ALGO = "AES";
    private static byte[] keyValue = null;
    private String keyString = null;

    public AESEncrypt() {

    }

    public AESEncrypt(String keyString) {
        this.setKeyString(keyString);
    }

    public String encrypt(String Data) throws Exception {
        Key key = AESEncrypt.generateKey();
        Cipher cipher = Cipher.getInstance(ALGO);
        cipher.init(1, key);
        byte[] encVal = cipher.doFinal(Data.getBytes());
        String encryptedValue = Base64.getEncoder().encodeToString(encVal);
        return encryptedValue;
    }

    public String decrypt(String encryptedData) throws Exception {
        Key key = AESEncrypt.generateKey();
        Cipher cipher = Cipher.getInstance(ALGO);
        cipher.init(2, key);
        byte[] decordedValue = Base64.getDecoder().decode(encryptedData);
        byte[] decValue = cipher.doFinal(decordedValue);
        String decryptedValue = new String(decValue);
        return decryptedValue;
    }

    private static Key generateKey() throws Exception {
        SecretKeySpec key = new SecretKeySpec(keyValue, ALGO);
        return key;
    }

    public String getKeyString() {
        return this.keyString;
    }

    public void setKeyString(String keyString) {
        this.keyString = keyString;
        keyValue = new byte[this.keyString.length()];
        keyValue = this.keyString.getBytes();
    }

    public static String getAlgo() {
        return ALGO;
    }
}
