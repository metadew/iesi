package io.metadew.iesi.metadata.definition;

import java.sql.Timestamp;

//import com.fasterxml.jackson.annotation.JsonFormat;

public class ScriptLog {

    private String run;
    private long process;
    private long parent;
    private String identifier;
    private long version;
    private String environment;
    private String status;
    private Timestamp start;
    private Timestamp end;

    // Constructors
    public ScriptLog() {

    }

    // Getters and Setters
    public String getEnvironment() {
        return environment;
    }

    public void setEnvironment(String environment) {
        this.environment = environment;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Timestamp getStart() {
        return start;
    }

    public void setStart(Timestamp start) {
        this.start = start;
    }

    public Timestamp getEnd() {
        return end;
    }

    public void setEnd(Timestamp end) {
        this.end = end;
    }

    public long getProcess() {
        return process;
    }

    public void setProcess(long process) {
        this.process = process;
    }

    public long getParent() {
        return parent;
    }

    public void setParent(long parent) {
        this.parent = parent;
    }

    public long getVersion() {
        return version;
    }

    public void setVersion(long version) {
        this.version = version;
    }

    public String getRun() {
        return run;
    }

    public void setRun(String run) {
        this.run = run;
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

}