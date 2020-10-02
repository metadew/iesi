package io.metadew.iesi.gcp.spec.bql;

import lombok.Data;

@Data
public class BqlSpec {

    private String name;
    private String input;
    private BqlBigquerySpec bigquery;
    private String output;

}
