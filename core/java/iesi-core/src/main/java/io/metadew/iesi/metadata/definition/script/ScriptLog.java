package io.metadew.iesi.metadata.definition.script;

import java.sql.Timestamp;
import java.time.LocalDateTime;

//import com.fasterxml.jackson.annotation.JsonFormat;

public class ScriptLog {

    private String run;
    private long process;
    private long parent;
    private String identifier;
    private long version;
    private String environment;
    private String status;
    private LocalDateTime start;
    private LocalDateTime end;

    // Constructors
    public ScriptLog() {

    }

    public ScriptLog(String run, long process, long parent, String identifier, long version, String environment, String status, LocalDateTime start, LocalDateTime end) {
        this.run = run;
        this.process = process;
        this.parent = parent;
        this.identifier = identifier;
        this.version = version;
        this.environment = environment;
        this.status = status;
        this.start = start;
        this.end = end;
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

    public LocalDateTime getStart() {
        return start;
    }

    public void setStart(LocalDateTime start) {
        this.start = start;
    }

    public LocalDateTime getEnd() {
        return end;
    }

    public void setEnd(LocalDateTime end) {
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