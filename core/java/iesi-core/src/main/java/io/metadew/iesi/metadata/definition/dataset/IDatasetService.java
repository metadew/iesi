package io.metadew.iesi.metadata.definition.dataset;

import io.metadew.iesi.metadata.definition.dataset.implementation.DatasetImplementation;

import java.util.List;
import java.util.Optional;

public interface IDatasetService {

    public Optional<DatasetImplementation> getDesignDatasetImplementation(String name, List<DatasetLabel> labels);

    public Optional<DatasetImplementation> getResultDatasetImplementation(String name, List<DatasetLabel> labels);

    public DatasetImplementation createNewDesignDatasetImplementation(String name, List<DatasetLabel> labels);

    public DatasetImplementation createNewResultDatasetImplementation(String name, List<DatasetLabel> labels);

}