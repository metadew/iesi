package io.metadew.iesi.metadata.configuration.template.matcher;

import io.metadew.iesi.metadata.configuration.Configuration;
import io.metadew.iesi.metadata.definition.template.matcher.Matcher;
import io.metadew.iesi.metadata.definition.template.matcher.MatcherKey;

import java.util.List;
import java.util.Optional;

public class MatcherConfiguration extends Configuration<Matcher, MatcherKey> {

    private static MatcherConfiguration INSTANCE;

    public synchronized static MatcherConfiguration getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new MatcherConfiguration();
        }
        return INSTANCE;
    }

    private MatcherConfiguration() {
    }

    @Override
    public Optional<Matcher> get(MatcherKey metadataKey) {
        return Optional.empty();
    }

    @Override
    public List<Matcher> getAll() {
        return null;
    }

    @Override
    public void delete(MatcherKey metadataKey) {

    }

    @Override
    public void insert(Matcher metadata) {

    }
}
