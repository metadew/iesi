package io.metadew.iesi.metadata.configuration.template.matcher.value;

import io.metadew.iesi.metadata.configuration.Configuration;
import io.metadew.iesi.metadata.definition.template.matcher.value.MatcherValue;
import io.metadew.iesi.metadata.definition.template.matcher.value.MatcherValueKey;

import java.util.List;
import java.util.Optional;

public class MatcherValueConfiguration extends Configuration<MatcherValue, MatcherValueKey> {

    private static MatcherValueConfiguration INSTANCE;

    public synchronized static MatcherValueConfiguration getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new MatcherValueConfiguration();
        }
        return INSTANCE;
    }

    private MatcherValueConfiguration() {
    }

    @Override
    public Optional<MatcherValue> get(MatcherValueKey metadataKey) {
        return Optional.empty();
    }

    @Override
    public List<MatcherValue> getAll() {
        return null;
    }

    @Override
    public void delete(MatcherValueKey metadataKey) {

    }

    @Override
    public void insert(MatcherValue metadata) {

    }
}
