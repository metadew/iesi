package io.metadew.iesi.metadata.tools;

import io.metadew.iesi.metadata.definition.template.TemplateKey;
import io.metadew.iesi.metadata.definition.template.matcher.MatcherKey;
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
    public static String getConnectionIdentifier(String input) {
        return DigestUtils.sha256Hex(input);
    }

    public static String getScriptExecutionRequestIdentifier() {
        return UUID.randomUUID().toString();
    }

    public static UUID getTemplateIdentifier(String name, long version) {
        StringBuilder identifier = new StringBuilder(DigestUtils.sha256Hex(version + name));
        while (identifier.length() < 32) {
            identifier.append(identifier);
        }
        String identifierString = identifier.toString();
        return UUID.fromString(identifierString.substring(0, 8) + "-" +
                identifierString.substring(8, 12) + "-" +
                identifierString.substring(12, 16) + "-" +
                identifierString.substring(16, 20) + "-" +
                identifierString.substring(20, 32));
    }

    public static UUID getMatcherValueIdentifier(TemplateKey templateKey, String key) {
        StringBuilder identifier = new StringBuilder(DigestUtils.sha256Hex(templateKey.getId().toString().substring(0, 8) + key));
        while (identifier.length() < 32) {
            identifier.append(identifier);
        }
        String identifierString = identifier.toString();
        return UUID.fromString(identifierString.substring(0, 8) + "-" +
                identifierString.substring(8, 12) + "-" +
                identifierString.substring(12, 16) + "-" +
                identifierString.substring(16, 20) + "-" +
                identifierString.substring(20, 32));
    }

    public static UUID getMatcherValueIdentifier(TemplateKey templateKey, MatcherKey matcherKey) {
        StringBuilder identifier = new StringBuilder(DigestUtils.sha256Hex(templateKey.getId().toString().substring(0, 8) + matcherKey.getId().toString().substring(0, 8)));
        while (identifier.length() < 32) {
            identifier.append(identifier);
        }
        String identifierString = identifier.toString();
        return UUID.fromString(identifierString.substring(0, 8) + "-" +
                identifierString.substring(8, 12) + "-" +
                identifierString.substring(12, 16) + "-" +
                identifierString.substring(16, 20) + "-" +
                identifierString.substring(20, 32));
    }


}