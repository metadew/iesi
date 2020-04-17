package io.metadew.iesi.gcp.bqloader.launch;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.metadew.iesi.gcp.bqloader.pubsub.Message;
import io.metadew.iesi.gcp.bqloader.pubsub.Subscription;
import io.metadew.iesi.gcp.bqloader.pubsub.Topic;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.HelpFormatter;

import java.io.IOException;

public class Pubsub
{
    public static void main( String[] args ) throws IOException, InterruptedException {

        Options options = new Options()
                .addOption(Option.builder("help").desc("print this message").build())
                .addOption(Option.builder("create").desc("create the pubsub topic and subscription").build())
                .addOption(Option.builder("delete").desc("delete the pubsub topic and subscription").build())
                .addOption(Option.builder("publish").hasArg().desc("publish a message in the topic").build())
                .addOption(Option.builder("runid").hasArg().desc("publish a run id for the BigQuery Loader").build());

        // create the parser
        CommandLineParser parser = new DefaultParser();

        CommandLine line = null;
        try {
            // parse the command line arguments
            line = parser.parse(options, args);
        } catch (Exception e) {
            System.out.println("Incorrect format provided, see --help for more info");
        }

        boolean create = false;
        boolean delete = false;
        boolean publish = false;
        boolean runid = false;

        if (line != null) {
            if (line.hasOption("help")) {
                // automatically generate the help statement
                HelpFormatter formatter = new HelpFormatter();
                formatter.printHelp("[command]", options);
                System.exit(0);
            }

            if (line.hasOption("create")) create = true;
            if (line.hasOption("delete")) delete = true;
            if (line.hasOption("publish")) publish = true;
            if (line.hasOption("runid")) runid = true;
        }

        //Define the topic and subscription
        Topic topic = new Topic("iesi-01","iesi-scriptresults");
        Subscription subscription = new Subscription(topic,"iesi-scriptresults-bigquery");

        if (create) {
            if (!topic.exists()) {
                topic.create();
            }

            if (!subscription.exists()) {
                subscription.create();
            }
        }

        if (delete) {
            if (subscription.exists()) {
                subscription.delete();
            }

            if (topic.exists()) {
                topic.delete();
            }
        }

        if (publish) {
            String message = line.getOptionValue("publish");
            topic.publish(message);
        }

        if (runid) {
            String input = line.getOptionValue("runid");
            Message message = new Message(input);
            ObjectMapper objectMapper = new ObjectMapper();
            String jsonMessageString = objectMapper.writeValueAsString(message);
            topic.publish(jsonMessageString);
        }
    }
}
