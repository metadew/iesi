package io.metadew.iesi.common.configuration.publisher;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.metadew.iesi.common.configuration.Configuration;
import io.metadew.iesi.connection.publisher.Publisher;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

@Log4j2
@Getter
public class PublishersConfiguration {

    private static PublishersConfiguration INSTANCE;
    private static final String publishersKey = "publishers";

    private List<PublisherConfiguration> publisherConfigurations;
    private List<Publisher> publishers;

    public synchronized static PublishersConfiguration getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new PublishersConfiguration();
        }
        return INSTANCE;
    }

    @SuppressWarnings("unchecked")
    private PublishersConfiguration() {
        publisherConfigurations = new ArrayList<>();
        publishers = new ArrayList<>();
        if (containsConfiguration()) {
            List<Object> publisherConfigurations = (List<Object>) Configuration.getInstance().getProperties()
                    .get(publishersKey);
            ObjectMapper objectMapper = new ObjectMapper();
            for (Object entry : publisherConfigurations) {
                PublisherConfiguration publisherConfiguration = objectMapper.convertValue(entry, PublisherConfiguration.class);
                this.publisherConfigurations.add(publisherConfiguration);
                try {
                    publishers.add(publisherConfiguration.convert());
                } catch (IOException e) {
                    StringWriter stackTrace = new StringWriter();
                    e.printStackTrace(new PrintWriter(stackTrace));
                    log.warn("unable to publish script result due to: " + stackTrace.toString());
                }
            }
        } else {
            log.warn("no metadata configuration found on system variable, classpath or filesystem");
        }
    }


    private boolean containsConfiguration() {
        return Configuration.getInstance().getProperties().containsKey(PublishersConfiguration.publishersKey) &&
                (Configuration.getInstance().getProperties().get(PublishersConfiguration.publishersKey) instanceof List);
    }

}
