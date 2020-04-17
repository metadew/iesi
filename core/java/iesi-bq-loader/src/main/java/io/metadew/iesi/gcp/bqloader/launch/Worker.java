package io.metadew.iesi.gcp.bqloader.launch;

import com.google.cloud.pubsub.v1.AckReplyConsumer;
import com.google.cloud.pubsub.v1.MessageReceiver;
import com.google.cloud.pubsub.v1.Subscriber;
import com.google.pubsub.v1.ProjectSubscriptionName;
import com.google.pubsub.v1.PubsubMessage;

import org.apache.commons.cli.Options;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.HelpFormatter;

import java.io.IOException;

public class Worker
{
    static class MessageReceiverExample implements MessageReceiver {

        @Override
        public void receiveMessage(PubsubMessage message, AckReplyConsumer consumer) {
            System.out.println(
                    "Message Id: " + message.getMessageId() + " Data: " + message.getData().toStringUtf8());
            // Ack only after all work for the message is complete.
            consumer.ack();
        }
    }
    public static void main( String[] args ) throws IOException, InterruptedException {

        Options options = new Options()
                .addOption(Option.builder("help").desc("print this message").build());

        // create the parser
        CommandLineParser parser = new DefaultParser();

        CommandLine line = null;
        try {
            // parse the command line arguments
            line = parser.parse(options, args);
        } catch (Exception e) {
            System.out.println("Incorrect format provided, see --help for more info");
        }

        if (line != null) {
            if (line.hasOption("help")) {
                // automatically generate the help statement
                HelpFormatter formatter = new HelpFormatter();
                formatter.printHelp("[command]", options);
                System.exit(0);
            }
        }

        // set subscriber id, eg. my-sub
        String subscriptionId = "iesi-scriptresults-bigquery";
        ProjectSubscriptionName subscriptionName =
                ProjectSubscriptionName.of("iesi-01", subscriptionId);
        Subscriber subscriber = null;
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
