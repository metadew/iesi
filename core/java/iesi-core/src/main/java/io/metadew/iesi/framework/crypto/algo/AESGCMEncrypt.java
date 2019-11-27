//package io.metadew.iesi.framework.crypto.algo;
//
//import org.apache.shiro.crypto.AesCipherService;
//
//import java.util.Base64;
//
//public class AESGCMEncrypt {
//    private static byte[] keyValue = null;
//    private final AesCipherService aesCipherService;
//
//    public AESGCMEncrypt(String keyString) {
//        keyValue = new byte[keyString.length()];
//        keyValue = keyString.getBytes();
//        aesCipherService = new AesCipherService();
//
//    }
//
//    public String encrypt(String data) {
//        return Base64.getEncoder().encodeToString(aesCipherService.encrypt(data.getBytes(), keyValue).getBytes());
//    }
//
//    public String decrypt(String encryptedData) {
//        return new String(aesCipherService.decrypt(Base64.getDecoder().decode(encryptedData.getBytes()), keyValue).getBytes());
//    }
//
//}
