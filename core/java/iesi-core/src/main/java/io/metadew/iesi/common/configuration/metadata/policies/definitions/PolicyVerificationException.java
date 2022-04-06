package io.metadew.iesi.common.configuration.metadata.policies.definitions;

import io.metadew.iesi.metadata.definition.script.Script;

public class PolicyVerificationException extends RuntimeException{

    public PolicyVerificationException(String message) {
        super(message);
    }
}
