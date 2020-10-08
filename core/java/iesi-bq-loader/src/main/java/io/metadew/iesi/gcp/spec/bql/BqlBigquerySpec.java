package io.metadew.iesi.gcp.spec.bql;

import lombok.Data;

@Data
public class BqlBigquerySpec {

    private BqlBigqueryScriptResultSpec scriptresult;
    private BqlBigqueryScriptExecutionSpec scriptexecution;

}
