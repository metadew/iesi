package io.metadew.iesi.connection.publisher;

import io.metadew.iesi.connection.publisher.gcp.GCPIPublisherService;
import io.metadew.iesi.metadata.definition.script.result.ScriptResult;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;


public class PublisherHandler implements IPublisherHandler {
    private Map<Class<? extends Publisher>, IPublisherService> publisherServiceMap;

    private static PublisherHandler INSTANCE;

    public synchronized static PublisherHandler getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new PublisherHandler();
        }
        return INSTANCE;
    }

    private PublisherHandler() {
        this.publisherServiceMap = new HashMap<>();
        publisherServiceMap.put(GCPIPublisherService.getInstance().appliesTo(), GCPIPublisherService.getInstance());
    }

    @SuppressWarnings("unchecked")
    public void publish(Publisher publisher, ScriptResult scriptResult) throws Exception {
        getPublisherService(publisher).publish(publisher, scriptResult);
    }

    @SuppressWarnings("unchecked")
    public void shutdown(Publisher publisher) {
        getPublisherService(publisher).shutdown(publisher);
    }

    private IPublisherService getPublisherService(Publisher publisher) {
        return Optional.ofNullable(publisherServiceMap.get(publisher.getClass()))
                .orElseThrow(() -> new RuntimeException("Could not find IPublisherService for " + publisher));
    }

}
