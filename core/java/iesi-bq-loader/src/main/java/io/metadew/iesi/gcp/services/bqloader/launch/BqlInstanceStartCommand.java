package io.metadew.iesi.gcp.services.bqloader.launch;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.cloud.pubsub.v1.AckReplyConsumer;
import com.google.cloud.pubsub.v1.MessageReceiver;
import com.google.cloud.pubsub.v1.Subscriber;
import com.google.pubsub.v1.ProjectSubscriptionName;
import com.google.pubsub.v1.PubsubMessage;
import io.metadew.iesi.gcp.connection.bigquery.BigqueryConnection;
import io.metadew.iesi.gcp.services.bqloader.common.BqlService;
import io.metadew.iesi.gcp.common.configuration.Mount;
import io.metadew.iesi.gcp.common.configuration.Spec;
import io.metadew.iesi.gcp.connection.pubsub.Topic;
import io.metadew.iesi.gcp.configuration.cco.core.ScriptResultCco;
import io.metadew.iesi.gcp.configuration.bigquery.ScriptResultConfiguration;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

import java.nio.file.Path;
import java.util.List;
import java.util.Map;

@Command(
        name = "start"
)
public class BqlInstanceStartCommand implements Runnable {
    @Option(names = {"-n", "--name"}, required = true, description = "the bql name to start")
    private String bqlName;

    @Parameters
    private List<Path> files;

    @Override
    public void run() {
        System.out.println("bql instance start");

        //Read the spec
        if (files != null) {
            files.forEach(path -> Spec.getInstance().readSpec(path));
        }

        String whichProject = Mount.getInstance().getProjectName("");
        BqlService.getInstance().init(whichProject,bqlName);
        start();
    }

    public void start() {
        String subscriptionId = BqlService.getInstance().getBqlSpec().getInput();
        ProjectSubscriptionName subscriptionName =
                ProjectSubscriptionName.of(BqlService.getInstance().getProjectName(), subscriptionId);

        Subscriber subscriber;
        try {
            subscriber = Subscriber.newBuilder(subscriptionName, new MessageReceiverExample()).build();
            subscriber.startAsync().awaitRunning();
            System.out.println("Worker has started...");
            subscriber.awaitTerminated();
        } catch (IllegalStateException e) {
            System.out.println("Subscriber unexpectedly stopped: " + e);
        }
    }

    static class MessageReceiverExample implements MessageReceiver {

        @Override
        public void receiveMessage(PubsubMessage message, AckReplyConsumer consumer) {
            String data = message.getData().toStringUtf8();
            System.out.println(
                    "[I] " + message.getMessageId());

            ObjectMapper objectMapper = new ObjectMapper();
            try {
                // Script result
                if (BqlService.getInstance().getBqlSpec().getBigquery().getScriptresult().isLoad()) {
                    Map<String, Object> map = objectMapper.readValue(data, new TypeReference<Map<String, Object>>() {});
                    ScriptResultCco scriptResultCco = objectMapper.convertValue(map, ScriptResultCco.class);
                    ScriptResultConfiguration scriptResultConfiguration = new ScriptResultConfiguration();
                    BigqueryConnection.getInstance().tableInsertRows(BqlService.getInstance().getBqlSpec().getBigquery().getScriptresult().getDataset(), scriptResultConfiguration.getTableName(), scriptResultConfiguration.getRowContent(scriptResultCco));
                }

                //Script execution
                if (BqlService.getInstance().getBqlSpec().getBigquery().getScriptexecution().isLoad()) {
                    //Get script execution
                    System.out.println("Scriptexec do");
                }
            } catch (Exception e) {
                e.printStackTrace();
                // TODO handle exceptions
            }
            String result = data;
            // Ack only after all work for the message is complete.
            consumer.ack();
            if (BqlService.getInstance().getBqlSpec().getOutput() != null && !BqlService.getInstance().getBqlSpec().getOutput().isEmpty() && !BqlService.getInstance().getBqlSpec().getOutput().equalsIgnoreCase("")) {
                nextHop(message.getMessageId(), result);
            }
        }

        public void nextHop(String sourceMessageId, String message) {
            //Define the topic and subscription
            Topic topic = new Topic(BqlService.getInstance().getProjectName(), BqlService.getInstance().getBqlSpec().getOutput());

            try {
                System.out.println("[O] " + sourceMessageId + " -> " + topic.publish(message));

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

}
