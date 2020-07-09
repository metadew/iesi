package io.metadew.iesi.common.crypto;

import io.metadew.iesi.common.configuration.Configuration;
import io.metadew.iesi.common.crypto.algo.AESEncryptBasic;
import lombok.extern.log4j.Log4j2;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.security.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Log4j2
public class FrameworkCrypto {

    private AESEncryptBasic aes;

    private static FrameworkCrypto INSTANCE;

    public static FrameworkCrypto getInstance() throws Exception {
        if (INSTANCE == null) {
            INSTANCE = new FrameworkCrypto();
        }
        return INSTANCE;
    }

    private FrameworkCrypto() throws Exception {
        Configuration configuration = Configuration.getInstance();
        if (configuration.getProperty("iesi.security.encryption.typ").isPresent()) {
            BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
            System.out.println("Enter your password: ");
            String password = br.readLine();
            String keyJKS = new JavaKeystore().loadKey(password);
            this.aes = new AESEncryptBasic(keyJKS);
        } else if (configuration.getProperty("iesi.security.encryption.key").isPresent()) {
            this.aes = new AESEncryptBasic(configuration.getProperty("iesi.security.encryption.key").get().toString());
        } else {
            this.aes = new AESEncryptBasic("c7c1e47391154a6a");
        }
    }

    // Methods
    public String encrypt(String input) {
        String output = "";
        try {
            output = "ENC(" + aes.encrypt(input) + ")";
        } catch (NoSuchPaddingException | NoSuchAlgorithmException | BadPaddingException | IllegalBlockSizeException | InvalidKeyException | InvalidAlgorithmParameterException e) {
            e.printStackTrace();
        }

        return output;
    }

    public String decrypt(String input) {
        log.debug("decrypting '" + input + "'");
        if (input.trim().equalsIgnoreCase(""))
            return input;

        if (input.substring(0, 4).equalsIgnoreCase("ENC(")) {
            if (!input.substring(input.length() - 1).equalsIgnoreCase(")")) {
                throw new RuntimeException("Encrypted password not set correctly");
            }
            try {
                return aes.decrypt(input.substring(4, input.length() - 1));
            } catch (NoSuchPaddingException | NoSuchAlgorithmException | InvalidKeyException | BadPaddingException | IllegalBlockSizeException | InvalidAlgorithmParameterException e) {
                throw new RuntimeException(e);
            }
        } else {
            throw new RuntimeException("Encrypted password not set correctly");
        }
    }

    public String decryptIfNeeded(String input) {
        String output = "";
        if (input.trim().equalsIgnoreCase(""))
            return output;

        if (input.length() > 5 && input.substring(0, 4).equalsIgnoreCase("ENC(")) {
            if (!input.substring(input.length() - 1).equalsIgnoreCase(")")) {
                throw new RuntimeException("Encrypted password not set correctly");
            }
            try {
                output = aes.decrypt(input.substring(4, input.length() - 1));
            } catch (NoSuchPaddingException | NoSuchAlgorithmException | InvalidKeyException | BadPaddingException | IllegalBlockSizeException | InvalidAlgorithmParameterException e) {
                throw new RuntimeException("Encrypted password cannot be decrypted on this host", e);
            }
        } else {
            // not doing anything
            output = input;
        }

        return output;
    }


    public String decryptAll(String input) {
        // TODO not working yet
        String output = "";
        if (input.trim().equalsIgnoreCase(""))
            return output;

        int openPos;
        int closePos;
        String variable_char = "ENC(";
        String variable_char_close = ")";
        String midBit;
        String replaceValue;
        String temp = input;
        while (temp.indexOf(variable_char) > 0 || temp.startsWith(variable_char)) {
            List<String> items = new ArrayList<>();
            String tempInstructions = temp;
            while (tempInstructions.indexOf(variable_char) > 0 || tempInstructions.startsWith(variable_char)) {
                openPos = tempInstructions.indexOf(variable_char);
                closePos = tempInstructions.indexOf(variable_char_close);
                midBit = tempInstructions.substring(openPos + 4, closePos).trim();
                items.add(midBit);
                tempInstructions = midBit;
            }

            // get last value
            String instruction = items.get(items.size() - 1);
            String instructionOutput = instruction;

            // Lookup
            try {
                instructionOutput = aes.decrypt(instructionOutput.substring(4, instructionOutput.length() - 1));
            } catch (NoSuchPaddingException | NoSuchAlgorithmException | InvalidKeyException | BadPaddingException | IllegalBlockSizeException | InvalidAlgorithmParameterException e) {
                throw new RuntimeException("Encrypted password cannot be decrypted on this host", e);
            }

            replaceValue = instructionOutput;

            if (replaceValue != null) {
                input = input.replace(variable_char + instruction + variable_char_close, replaceValue);
            }
            temp = input;
        }


        return output;
    }

    public String resolve(String input) {
        log.debug("resolving '" + input + "'");
        int openPos;
        int closePos;
        String variable_char = "ENC(";
        String variable_char_close = ")";
        String midBit;
        String replaceValue;
        String temp = input;
        while (temp.indexOf(variable_char) > 0 || temp.startsWith(variable_char)) {
            openPos = temp.indexOf(variable_char);
            closePos = temp.indexOf(variable_char_close, openPos + 1);
            midBit = temp.substring(openPos + 4, closePos);

            // Replace
            replaceValue = this.decrypt(variable_char + midBit + variable_char_close);
            if (replaceValue != null) {
                input = input.replace(variable_char + midBit + variable_char_close, replaceValue);
            }
            temp = temp.substring(closePos + 1, temp.length());

        }
        return input;
    }

    public String redact(String input) {
        // TODO: encrypted values should be secure enough?
        // Catch null pointer exceptions
        if (input == null)
            input = "";

        // Redact the input value
        int openPos;
        int closePos;
        String variable_char_open = "ENC(";
        String variable_char_close = ")";
        String midBit;
        String replaceValue;
        String temp = input;
        while (temp.indexOf(variable_char_open) > 0 || temp.startsWith(variable_char_open)) {
            openPos = temp.indexOf(variable_char_open);
            closePos = temp.indexOf(variable_char_close, openPos + 1);
            midBit = temp.substring(openPos + variable_char_open.length(), closePos);

            // Replace
            replaceValue = "*******";
            if (replaceValue != null) {
                // Use replace instead of replaceAll to avoid regex replace issues with special
                // characters
                input = input.replace(variable_char_open + midBit + variable_char_close, replaceValue);
            }
            temp = temp.substring(closePos + 1, temp.length());
        }
        return input;
    }

    public String redact(String input, ArrayList<String> redactionList) {
        for (String curVal : redactionList) {
            input = input.replace(curVal, "*******");
        }
        return input;
    }

}