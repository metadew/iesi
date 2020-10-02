package io.metadew.iesi.gcp.connection.bigquery;

import com.google.cloud.bigquery.*;

public class BigqueryDataset {

    private String project;
    private String name;

    public String getProject() {
        return project;
    }

    public void setProject(String project) {
        this.project = project;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public BigqueryDataset(String project, String name) {
        this.setProject(project);
        this.setName(name);
    }

    public void create() {
        try {
            DatasetInfo datasetInfo = DatasetInfo.newBuilder(this.getName()).build();
            com.google.cloud.bigquery.Dataset newDataset = BigqueryConnection.getInstance().getService().create(datasetInfo);
            String newDatasetName = newDataset.getDatasetId().getDataset();
            System.out.println(newDatasetName + " created successfully");
        } catch (BigQueryException e) {
            System.out.println("Dataset was not created. \n" + e.toString());
        }
    }

    public void delete() {
        DatasetId datasetId = DatasetId.of(this.getProject(), this.getName());
        boolean deleted = BigqueryConnection.getInstance().getService().delete(datasetId, BigQuery.DatasetDeleteOption.deleteContents());
        if (deleted) {
            System.out.println("Dataset was deleted.");
        } else {
            System.out.println("Dataset was not found.");
        }
    }
}
