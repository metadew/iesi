package io.metadew.iesi.common.configuration.guard;

import io.metadew.iesi.common.configuration.Configuration;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@org.springframework.context.annotation.Configuration
@Log4j2
@Getter
public class GuardConfiguration {

    private static final String guardKey = "guard";

    private Map<String, String> guardSettingsMap;
    private final Configuration configuration;

    public GuardConfiguration(Configuration configuration) {
        this.configuration = configuration;
    }


    @SuppressWarnings("unchecked")
    @PostConstruct
    private void postConstruct() {
        guardSettingsMap = new HashMap<>();
        if (containsConfiguration()) {
            guardSettingsMap = (Map<String, String>) this.configuration.getProperties()
                    .get(guardKey);
        } else {
            log.warn("no guard configuration found on system variable, classpath or filesystem");
        }
    }


    private boolean containsConfiguration() {
        return configuration.getProperties().containsKey(GuardConfiguration.guardKey) &&
                (configuration.getProperties().get(GuardConfiguration.guardKey) instanceof Map);
    }

    public Optional<String> getGuardSetting(String guardSetting) {
        return Optional.ofNullable(guardSettingsMap.get(guardSetting));
    }

}
