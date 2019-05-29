package io.metadew.iesi.metadata.tools;

import java.util.UUID;

import org.apache.commons.codec.digest.DigestUtils;

public final class IdentifierTools {

    public static String getScriptIdentifier(String input) {
        return DigestUtils.sha256Hex(input);
    }

    public static String getActionIdentifier(String input) {
        return DigestUtils.sha256Hex(input);
    }

    public static String getComponentIdentifier(String input) {
        return DigestUtils.sha256Hex(input);
    }

    public static String getRequestIdentifier() {
        return UUID.randomUUID().toString();
    }

}