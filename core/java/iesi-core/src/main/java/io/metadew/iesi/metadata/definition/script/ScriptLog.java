package io.metadew.iesi.metadata.definition.script;

import java.time.LocalDateTime;


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

    public void setStatus(String status) {
        this.status = status;
    }

    public void setEnd(LocalDateTime end) {
        this.end = end;
    }

}