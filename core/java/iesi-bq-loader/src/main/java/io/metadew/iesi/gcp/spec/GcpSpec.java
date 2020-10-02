package io.metadew.iesi.gcp.spec;

import io.metadew.iesi.gcp.spec.bigquery.BigquerySpec;
import io.metadew.iesi.gcp.spec.bql.BqlSpec;
import io.metadew.iesi.gcp.spec.dlp.DlpSpec;
import io.metadew.iesi.gcp.spec.pubsub.PubsubSpec;
import lombok.Data;

import java.util.List;

@Data
public class GcpSpec {

    private String name;
    private String project;
    private List<PubsubSpec> pubsub;
    private List<DlpSpec> dlp;
    private List<BigquerySpec> bigquery;
    private List<BqlSpec> bql;

}
