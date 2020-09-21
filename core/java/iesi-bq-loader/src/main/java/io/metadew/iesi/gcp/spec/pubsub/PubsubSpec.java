package io.metadew.iesi.gcp.spec.pubsub;

import lombok.Data;

import java.util.List;

@Data
public class PubsubSpec {

    private String name;
    private List<PubsubTopicSpec> topics;

}
