package io.metadew.iesi.framework.crypto.algo;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

public class AESEncryptBasic {

    private static final String FULL_ALGORITHM = "AES/CBC/PKCS5PADDING";
    private static final String ALGORITHM = "AES";
    private static byte[] keyValue;
    private final IvParameterSpec iv;

    public AESEncryptBasic(String keyString) {
        keyValue = new byte[keyString.length()];
        keyValue = keyString.getBytes(StandardCharsets.US_ASCII);
        iv = new IvParameterSpec("0102030405060708".getBytes(StandardCharsets.US_ASCII));
    }

    public String encrypt(String data) throws NoSuchPaddingException, NoSuchAlgorithmException, BadPaddingException, IllegalBlockSizeException, InvalidKeyException, InvalidAlgorithmParameterException {
        Key key = generateKey();
        Cipher cipher = Cipher.getInstance(FULL_ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, key, iv);
        byte[] encVal = cipher.doFinal(data.getBytes());
        return new String(Base64.getEncoder().encode(encVal), StandardCharsets.UTF_8);
    }

    public String decrypt(String encryptedData) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException, InvalidAlgorithmParameterException {
        Key key = generateKey();
        Cipher cipher = Cipher.getInstance(FULL_ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, key, iv);
        byte[] decodedValue = Base64.getDecoder().decode(encryptedData.getBytes(StandardCharsets.UTF_8));
        byte[] decValue = cipher.doFinal(decodedValue);
        return new String(decValue, StandardCharsets.UTF_8);
    }

    private static Key generateKey() {
        return new SecretKeySpec(keyValue, ALGORITHM);
    }


}
