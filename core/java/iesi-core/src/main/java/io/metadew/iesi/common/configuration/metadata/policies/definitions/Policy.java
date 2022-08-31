package io.metadew.iesi.common.configuration.metadata.policies.definitions;

public interface Policy<T> {
    boolean verify(T toVerify);
}
