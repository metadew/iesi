package io.metadew.iesi.gcp.bqloader.launch;

import io.metadew.iesi.gcp.bqloader.pubsub.Subscription;
import io.metadew.iesi.gcp.bqloader.pubsub.Topic;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args )
    {
        System.out.println( "Hello World!" );
        Topic topic = new Topic("iesi-01","iesi-scriptresults");
        if (!topic.exists()) {
            topic.create();
        }

        //topic.delete();
        System.out.println(topic.exists());

        Subscription subscription = new Subscription(topic,"iesi-scriptresults-bigquery");
        if (!subscription.exists()) {
            subscription.create();
        }
    }
}
