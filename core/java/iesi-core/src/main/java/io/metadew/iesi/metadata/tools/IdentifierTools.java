package io.metadew.iesi.metadata.tools;

import org.apache.commons.codec.digest.DigestUtils;

import java.util.UUID;

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

    public static String getScriptExecutionRequestIdentifier() {
        return UUID.randomUUID().toString();
    }

}