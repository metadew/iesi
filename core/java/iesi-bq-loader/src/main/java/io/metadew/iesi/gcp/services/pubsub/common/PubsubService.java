package io.metadew.iesi.gcp.services.pubsub.common;

import io.metadew.iesi.gcp.common.configuration.Spec;
import io.metadew.iesi.gcp.spec.pubsub.PubsubSpec;
import lombok.Getter;

@Getter
public class PubsubService {
    private static PubsubService INSTANCE;
    private PubsubSpec pubsubSpec;
    private String projectName;

    public synchronized static PubsubService getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new PubsubService();
        }
        return INSTANCE;
    }

    private PubsubService () {

    }

    public void init (String projectName, String dlpName) {
        this.projectName = projectName;
        pubsubSpec = null;
        for (PubsubSpec entry : Spec.getInstance().getGcpSpec().getPubsub()) {
            if (entry.getName().equalsIgnoreCase(dlpName)) {
                pubsubSpec = entry;
            }
        }
    }
}