package io.metadew.iesi.gcp.spec.pubsub;

import lombok.Data;

import java.util.List;

@Data
public class PubsubTopicSpec {

    private String name;
    private List<PubsubSubscriptionSpec> subscriptions;

}
