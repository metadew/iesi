package io.metadew.iesi.framework.crypto.algo;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.SecureRandom;
import java.util.Base64;

public class AESGCMEncrypt {
    private static final String ALGO = "AES";
    private static byte[] keyValue = null;
    private String keyString = null;
    private GCMParameterSpec gcmParameterSpec = null;
    public static int AES_KEY_SIZE = 128;
    public static int IV_SIZE = 96;
    public static int TAG_BIT_LENGTH = 128;
    public static String ALGO_TRANSFORMATION_STRING = "AES/GCM/PKCS5Padding";

    public AESGCMEncrypt() {

    }

    public AESGCMEncrypt(String keyString) {
        this.setKeyString(keyString);
        byte ivBytes[] = new byte[IV_SIZE];
        ivBytes = Base64.getDecoder().decode(keyString);
        gcmParameterSpec = new GCMParameterSpec(TAG_BIT_LENGTH, ivBytes);
    }

    public String encrypt(String data) throws Exception {
        byte[] aadData = keyValue;
        SecretKey key = AESGCMEncrypt.generateKey();
        Cipher cipher = null;
        cipher = Cipher.getInstance(ALGO_TRANSFORMATION_STRING);
        cipher.init(Cipher.ENCRYPT_MODE, key, gcmParameterSpec, new SecureRandom());
        cipher.updateAAD(aadData);
        byte[] cipherTextInByteArr = null;
        cipherTextInByteArr = cipher.doFinal(data.getBytes());
        String encryptedValue = Base64.getEncoder().encodeToString(cipherTextInByteArr);
        return encryptedValue;
    }

    public String decrypt(String encryptedData) throws Exception {
        byte[] aadData = keyValue;
        Cipher cipher = null;
        SecretKey key = AESGCMEncrypt.generateKey();
        cipher = Cipher.getInstance(ALGO_TRANSFORMATION_STRING);
        cipher.init(Cipher.DECRYPT_MODE, key, gcmParameterSpec, new SecureRandom());
        cipher.updateAAD(aadData);
        byte[] plainTextInByteArr = null;
        plainTextInByteArr = cipher.doFinal(Base64.getDecoder().decode(encryptedData));
        return new String(plainTextInByteArr);
    }

    private static SecretKey generateKey() throws Exception {
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
