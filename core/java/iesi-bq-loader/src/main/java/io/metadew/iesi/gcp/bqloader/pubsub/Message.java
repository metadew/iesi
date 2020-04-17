package io.metadew.iesi.gcp.bqloader.pubsub;

public class Message {

    private String runid;

    public String getRunid() {
        return runid;
    }

    public void setRunid(String runid) {
        this.runid = runid;
    }

    public Message(String runid) {
        this.setRunid(runid);
    }

}
