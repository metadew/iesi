package io.metadew.iesi.connection.gcp;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.api.core.ApiFuture;
import com.google.protobuf.ByteString;
import com.google.pubsub.v1.PubsubMessage;
import io.metadew.iesi.metadata.definition.script.result.ScriptResult;
import lombok.extern.log4j.Log4j2;

import java.util.concurrent.ExecutionException;

@Log4j2
public class GCPIPublisherService implements IGCPPublisherService {

    private static GCPIPublisherService INSTANCE;

    public synchronized static GCPIPublisherService getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new GCPIPublisherService();
        }
        return INSTANCE;
    }

    private GCPIPublisherService() {
    }

    @Override
    public void publish(GCPPublisher gcpPublisher, ScriptResult scriptResult) throws ExecutionException, InterruptedException, JsonProcessingException {
        ByteString data = ByteString.copyFromUtf8(new ObjectMapper().writeValueAsString(scriptResult));
        PubsubMessage pubsubMessage = PubsubMessage.newBuilder().setData(data).build();

        // Once published, returns a server-assigned message id (unique within the topic)
        ApiFuture<String> messageIdFuture = gcpPublisher.getPublisher().publish(pubsubMessage);
        String messageId = messageIdFuture.get();
        log.info("Published " + scriptResult.toString() + " to " + gcpPublisher.getPublisher().getTopicNameString() + " with message ID: " + messageId);
    }

    @Override
    public void shutdown(GCPPublisher gcpPublisher) {
        gcpPublisher.getPublisher().shutdown();
    }


    @Override
    public Class<GCPPublisher> appliesTo() {
        return GCPPublisher.class;
    }
}
