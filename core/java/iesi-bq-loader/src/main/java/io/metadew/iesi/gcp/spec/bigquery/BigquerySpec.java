package io.metadew.iesi.gcp.spec.bigquery;

import lombok.Data;

import java.util.List;

@Data
public class BigquerySpec {

    private String name;
    private List<BigqueryDatasetSpec> datasets;

}
