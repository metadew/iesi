//package io.metadew.iesi.metadata.definition.dataset;
//
//import io.metadew.iesi.metadata.configuration.Configuration;
//import io.metadew.iesi.metadata.repository.MetadataRepository;
//
//import java.util.List;
//import java.util.Optional;
//
//public class DesignDatasetLabelConfiguration extends Configuration<DatasetImplementationLabel, DatasetImplementationLabelKey> {
//
//    private static DesignDatasetLabelConfiguration INSTANCE;
//
//    public synchronized static DesignDatasetLabelConfiguration getInstance() {
//        if (INSTANCE == null) {
//            INSTANCE = new DesignDatasetLabelConfiguration();
//        }
//        return INSTANCE;
//    }
//
//    private DesignDatasetLabelConfiguration() {
//    }
//
//    public void init(MetadataRepository metadataRepository) {
//        setMetadataRepository(metadataRepository);
//    }
//
//    @Override
//    public Optional<DatasetImplementationLabel> get(DatasetImplementationLabelKey metadataKey) {
//        return Optional.empty();
//    }
//
//    @Override
//    public List<DatasetImplementationLabel> getAll() {
//        return null;
//    }
//
//    @Override
//    public void delete(DatasetImplementationLabelKey metadataKey) {
//
//    }
//
//    @Override
//    public void insert(DatasetImplementationLabel metadata) {
//
//    }
//
//}