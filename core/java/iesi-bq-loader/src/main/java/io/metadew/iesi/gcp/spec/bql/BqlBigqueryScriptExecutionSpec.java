package io.metadew.iesi.gcp.spec.bql;

import lombok.Data;

@Data
public class BqlBigqueryScriptExecutionSpec {

    private String dataset;
    public boolean load;

}
