package io.metadew.iesi.common.configuration.guard;

import io.metadew.iesi.common.configuration.Configuration;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Log4j2
@Getter
public class GuardConfiguration {

    private static GuardConfiguration INSTANCE;
    private static final String guardKey = "guard";

    private Map<String, String> guardSettingsMap;

    public synchronized static GuardConfiguration getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new GuardConfiguration();
        }
        return INSTANCE;
    }

    @SuppressWarnings("unchecked")
    private GuardConfiguration() {
        guardSettingsMap = new HashMap<>();
        if (containsConfiguration()) {
            guardSettingsMap = (Map<String, String>) Configuration.getInstance().getProperties()
                    .get(guardKey);
        } else {
            log.warn("no guard configuration found on system variable, classpath or filesystem");
        }
    }


    private boolean containsConfiguration() {
        return Configuration.getInstance().getProperties().containsKey(GuardConfiguration.guardKey) &&
                (Configuration.getInstance().getProperties().get(GuardConfiguration.guardKey) instanceof Map);
    }

    public Optional<String> getGuardSetting(String guardSetting) {
        return Optional.ofNullable(guardSettingsMap.get(guardSetting));
    }

}
