package io.metadew.iesi.gcp.services.dlp.launch;

import com.google.cloud.pubsub.v1.AckReplyConsumer;
import com.google.cloud.pubsub.v1.MessageReceiver;
import com.google.cloud.pubsub.v1.Subscriber;
import com.google.pubsub.v1.ProjectSubscriptionName;
import com.google.pubsub.v1.PubsubMessage;
import io.metadew.iesi.gcp.common.configuration.Mount;
import io.metadew.iesi.gcp.common.configuration.Spec;
import io.metadew.iesi.gcp.connection.pubsub.Topic;
import io.metadew.iesi.gcp.services.dlp.common.DlpService;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

import java.nio.file.Path;
import java.util.List;

@Command(
        name = "start"
)
public class DlpInstanceStartCommand implements Runnable {
    @Option(names = {"-n", "--name"}, required = true, description = "the dlp name to start")
    private String dlpName;

    @Parameters
    private List<Path> files;

    @Override
    public void run() {
        System.out.println("dlp instance start");

        //Read the spec
        if (files != null) {
            files.forEach(path -> Spec.getInstance().readSpec(path));
        }

        String whichProject = Mount.getInstance().getProjectName("");
        DlpService.getInstance().init(whichProject,dlpName);
        start();
    }

    public void start() {
        String subscriptionId = DlpService.getInstance().getDlpSpec().getInput();
        ProjectSubscriptionName subscriptionName =
                ProjectSubscriptionName.of(DlpService.getInstance().getProjectName(), subscriptionId);

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
            String result = DlpService.getInstance().deIdentifyWithReplacement(data);
            // Ack only after all work for the message is complete.
            consumer.ack();
            nextHop(message.getMessageId(), result);
        }

        public void nextHop(String sourceMessageId, String message) {
            //Define the topic and subscription
            Topic topic = new Topic(DlpService.getInstance().getProjectName(), DlpService.getInstance().getDlpSpec().getOutput());

            try {
                System.out.println("[O] " + sourceMessageId + " -> " + topic.publish(message));

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

}
