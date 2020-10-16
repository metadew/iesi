package io.metadew.iesi.gcp.services.bqloader.launch;

import com.google.cloud.pubsub.v1.AckReplyConsumer;
import com.google.cloud.pubsub.v1.MessageReceiver;
import com.google.cloud.pubsub.v1.Subscriber;
import com.google.pubsub.v1.ProjectSubscriptionName;
import com.google.pubsub.v1.PubsubMessage;

public class Worker
{
    static class MessageReceiverExample implements MessageReceiver {

        @Override
        public void receiveMessage(PubsubMessage message, AckReplyConsumer consumer) {
            String data = message.getData().toStringUtf8();
            System.out.println(
                    "Message Id: " + message.getMessageId() + " Data: " + data);
            try {
                //ScriptResultDto scriptResultDto = ScriptResultDtoService.getInstance().deserialize(data);
                //System.out.println(scriptResultDto);
                // Save to GCS
                // https://cloud.google.com/bigquery/docs/loading-data-cloud-storage-json

                //BigqueryConnection.getInstance().tableInsertRows("iesi_results", "res_script", ScriptResultsConfiguration.getInstance().getRowContent(scriptResultDto));

            } catch (Exception e) {
                e.printStackTrace();
                System.out.println("issue converting object");
                //on error write to error bucket
                //add gcs
            }
            // Ack only after all work for the message is complete.
            consumer.ack();
        }
    }
    public static void main( String[] args ) {

        // set subscriber id, eg. my-sub
        String subscriptionId = "iesi-scriptresults-bigquery";
        ProjectSubscriptionName subscriptionName =
                ProjectSubscriptionName.of("", subscriptionId);
        Subscriber subscriber;
        try {
            // create a subscriber bound to the asynchronous message receiver
            subscriber = Subscriber.newBuilder(subscriptionName, new MessageReceiverExample()).build();
            subscriber.startAsync().awaitRunning();
            System.out.println("Worker has started...");
            // Allow the subscriber to run indefinitely unless an unrecoverable error occurs.
            subscriber.awaitTerminated();
        } catch (IllegalStateException e) {
            System.out.println("Subscriber unexpectedly stopped: " + e);
        }

    }
}
