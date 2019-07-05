package io.metadew.iesi.framework.crypto.tools;

import org.apache.commons.lang3.StringUtils;

import javax.xml.bind.DatatypeConverter;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class CryptoTools {

    public static String formatKeyString(String input, int size) {
        String output = "";
        if (input.length() > size) {
            output = input.substring(0, size);
        } else if (input.length() < size) {
            output = StringUtils.rightPad(input, size, "0");
        } else {
            output = input;
        }
        return output;
    }

    public static String generateMD5Hash(String input) {
        MessageDigest md = null;
        String output = "";
        try {
            md = MessageDigest.getInstance("MD5");
            md.update(input.getBytes());
            byte[] digest = md.digest();
            output = DatatypeConverter.printHexBinary(digest).toUpperCase();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Not possible to generte hash value");
        }

        return output;

    }
}
