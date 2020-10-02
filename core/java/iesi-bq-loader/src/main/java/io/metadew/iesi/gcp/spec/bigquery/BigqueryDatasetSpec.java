package io.metadew.iesi.gcp.spec.bigquery;

import lombok.Data;

import java.util.List;

@Data
public class BigqueryDatasetSpec {

    private String name;
    private List<BigqueryTableSpec> tables;

}
