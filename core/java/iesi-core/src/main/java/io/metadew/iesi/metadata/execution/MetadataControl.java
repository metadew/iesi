//package io.metadew.iesi.metadata.execution;
//
//import io.metadew.iesi.metadata.repository.*;
//
//import java.text.MessageFormat;
//import java.util.ArrayList;
//import java.util.List;
//
///**
// * Class to centralize the shared metadata that will be used by the framework
// *
// * @author peter.billen
// */
//public class MetadataControl {
//
//    private DesignMetadataRepository designMetadataRepository;
//    private ExecutionServerMetadataRepository executionServerMetadataRepository;
//    private ConnectivityMetadataRepository connectivityMetadataRepository;
//    private ControlMetadataRepository controlMetadataRepository;
//    private TraceMetadataRepository traceMetadataRepository;
//    private ResultMetadataRepository resultMetadataRepository;
//
//    private static MetadataControl INSTANCE;
//
//    public synchronized static MetadataControl getInstance() {
//        if (INSTANCE == null) {
//            INSTANCE = new MetadataControl();
//        }
//        return INSTANCE;
//    }
//
//    private MetadataControl() {}
//
//
//    public void init(DesignMetadataRepository designMetadataRepository, ConnectivityMetadataRepository connectivityMetadataRepository,
//                     ControlMetadataRepository controlMetadataRepository, TraceMetadataRepository traceMetadataRepository,
//                     ResultMetadataRepository resultMetadataRepository,
//                     ExecutionServerMetadataRepository executionServerMetadataRepository) {
//        this.designMetadataRepository = designMetadataRepository;
//        this.connectivityMetadataRepository = connectivityMetadataRepository;
//        this.controlMetadataRepository = controlMetadataRepository;
//        this.traceMetadataRepository = traceMetadataRepository;
//        this.resultMetadataRepository = resultMetadataRepository;
//        this.executionServerMetadataRepository = executionServerMetadataRepository;
//    }
//
//    public void init(List<MetadataRepository> metadataRepositories) {
//        // Set shared metadata
//        for (MetadataRepository metadataRepository : metadataRepositories) {
//            setMetadataRepository(metadataRepository);
//        }
//        // Check if repositories are correctly set
//    }
//
//    private void setMetadataRepository(MetadataRepository metadataRepository) {
//        String category = metadataRepository.getCategory();
//        if (metadataRepository.getCategory().equalsIgnoreCase("connectivity")) {
//            this.connectivityMetadataRepository = (ConnectivityMetadataRepository) metadataRepository;
//        } else if (metadataRepository.getCategory().equalsIgnoreCase("control")) {
//            this.controlMetadataRepository = (ControlMetadataRepository) metadataRepository;
//        } else if (metadataRepository.getCategory().equalsIgnoreCase("design")) {
//            this.designMetadataRepository = (DesignMetadataRepository) metadataRepository;
//        } else if (metadataRepository.getCategory().equalsIgnoreCase("trace")) {
//            this.traceMetadataRepository = (TraceMetadataRepository) metadataRepository;
//        } else if (metadataRepository.getCategory().equalsIgnoreCase("result")) {
//            this.resultMetadataRepository = (ResultMetadataRepository) metadataRepository;
//        } else if (metadataRepository.getCategory().equalsIgnoreCase("execution_server")) {
//            this.executionServerMetadataRepository = (ExecutionServerMetadataRepository) metadataRepository;
//        } else {
//            throw new RuntimeException(MessageFormat.format("No Metadata repository of type {0} can be set", metadataRepository.getCategory()));
//        }
//    }
//
//    public ConnectivityMetadataRepository getConnectivityMetadataRepository() {
//        return connectivityMetadataRepository;
//    }
//
//    public TraceMetadataRepository getTraceMetadataRepository() {
//        return traceMetadataRepository;
//    }
//
//    public ResultMetadataRepository getResultMetadataRepository() {
//        return resultMetadataRepository;
//    }
//
//    public DesignMetadataRepository getDesignMetadataRepository() {
//        return designMetadataRepository;
//    }
//
//    public ControlMetadataRepository getControlMetadataRepository() {
//        return controlMetadataRepository;
//    }
//
//    public ExecutionServerMetadataRepository getExecutionServerMetadataRepository() {
//        return executionServerMetadataRepository;
//    }
//
//    public List<MetadataRepository> getMetadataRepositories() {
//        List<MetadataRepository> metadataRepositories = new ArrayList<>();
//        metadataRepositories.add(connectivityMetadataRepository);
//        metadataRepositories.add(traceMetadataRepository);
//        metadataRepositories.add(resultMetadataRepository);
//        metadataRepositories.add(designMetadataRepository);
//        metadataRepositories.add(controlMetadataRepository);
//        metadataRepositories.add(executionServerMetadataRepository);
//        return metadataRepositories;
//    }
//
//}