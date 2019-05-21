package io.metadew.iesi.data.generation.configuration;

import io.metadew.iesi.data.generation.execution.GenerationComponentExecution;
import io.metadew.iesi.data.generation.execution.GenerationDataExecution;

import java.util.Date;

public class Timestamp extends GenerationComponentExecution {

    public Timestamp(GenerationDataExecution execution) {
        super(execution);
    }

    public Date getNextTimestamp(String lbound, String ubound) {
        long offset = java.sql.Timestamp.valueOf(lbound).getTime();
        long end = java.sql.Timestamp.valueOf(ubound).getTime();
        long diff = end - offset + 1;
        java.sql.Timestamp rand = new java.sql.Timestamp(offset + (long) (Math.random() * diff));
        return rand;
        // 2012-01-01 00:00:00
    }

}
