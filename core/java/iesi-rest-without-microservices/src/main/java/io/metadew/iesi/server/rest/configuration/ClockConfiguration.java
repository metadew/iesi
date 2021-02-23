package io.metadew.iesi.server.rest.configuration;

import org.springframework.context.annotation.Bean;

import java.time.Clock;

public class ClockConfiguration {

    @Bean
    public Clock clock() {
        return Clock.systemDefaultZone();
    }

}
