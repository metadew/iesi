package io.metadew.iesi.metadata.execution;

import io.metadew.iesi.metadata.repository.*;

import java.text.MessageFormat;
import java.util.List;

/**
 * Class to centralize the shared metadata that will be used by the framework
 *
 * @author peter.billen
 */
public class MetadataControl {

    private DesignMetadataRepository designMetadataRepository;
    private ExecutionServerMetadataRepository executionServerMetadataRepository;
    private ConnectivityMetadataRepository connectivityMetadataRepository;
    private ControlMetadataRepository controlMetadataRepository;
    private TraceMetadataRepository traceMetadataRepository;
    private ResultMetadataRepository resultMetadataRepository;
    private MonitorMetadataRepository monitorMetadataRepository;
    private LedgerMetadataRepository ledgerMetadataRepository;
    private CatalogMetadataRepository catalogMetadataRepository;


    private static MetadataControl INSTANCE;

    public synchronized static MetadataControl getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new MetadataControl();
        }
        return INSTANCE;
    }

    private MetadataControl() {}


    public void init(DesignMetadataRepository designMetadataRepository, ConnectivityMetadataRepository connectivityMetadataRepository,
                     ControlMetadataRepository controlMetadataRepository, TraceMetadataRepository traceMetadataRepository,
                     ResultMetadataRepository resultMetadataRepository, MonitorMetadataRepository monitorMetadataRepository,
                     LedgerMetadataRepository ledgerMetadataRepository, CatalogMetadataRepository catalogMetadataRepository,
                     ExecutionServerMetadataRepository executionServerMetadataRepository) {
        this.designMetadataRepository = designMetadataRepository;
        this.connectivityMetadataRepository = connectivityMetadataRepository;
        this.controlMetadataRepository = controlMetadataRepository;
        this.traceMetadataRepository = traceMetadataRepository;
        this.resultMetadataRepository = resultMetadataRepository;
        this.monitorMetadataRepository = monitorMetadataRepository;
        this.ledgerMetadataRepository = ledgerMetadataRepository;
        this.catalogMetadataRepository = catalogMetadataRepository;
        this.executionServerMetadataRepository = executionServerMetadataRepository;
        // Check if repositories are correctly set
        if (!isValid()) {
            throw new RuntimeException("framework.metadata.incomplete");
        }
    }

    public void init(List<MetadataRepository> metadataRepositories) {
        // Set shared metadata
        for (MetadataRepository metadataRepository : metadataRepositories) {
            setMetadataRepository(metadataRepository);
        }
        // Check if repositories are correctly set
        if (!isValid()) {
            throw new RuntimeException("framework.metadata.incomplete");
        }
    }

    private void setMetadataRepository(MetadataRepository metadataRepository) {
        if (metadataRepository.getCategory().equalsIgnoreCase("connectivity")) {
            this.connectivityMetadataRepository = (ConnectivityMetadataRepository) metadataRepository;
        } else if (metadataRepository.getCategory().equalsIgnoreCase("catalog")) {
            this.catalogMetadataRepository = (CatalogMetadataRepository) metadataRepository;
        } else if (metadataRepository.getCategory().equalsIgnoreCase("control")) {
            this.controlMetadataRepository = (ControlMetadataRepository) metadataRepository;
        } else if (metadataRepository.getCategory().equalsIgnoreCase("design")) {
            this.designMetadataRepository = (DesignMetadataRepository) metadataRepository;
        } else if (metadataRepository.getCategory().equalsIgnoreCase("trace")) {
            this.traceMetadataRepository = (TraceMetadataRepository) metadataRepository;
        } else if (metadataRepository.getCategory().equalsIgnoreCase("result")) {
            this.resultMetadataRepository = (ResultMetadataRepository) metadataRepository;
        } else if (metadataRepository.getCategory().equalsIgnoreCase("execution_server")) {
            this.executionServerMetadataRepository = (ExecutionServerMetadataRepository) metadataRepository;
        } else {
            throw new RuntimeException(MessageFormat.format("No Metadata repository of type {0} can be set", metadataRepository.getCategory()));
        }
    }

    // Getters and Setters
    public CatalogMetadataRepository getCatalogMetadataRepository() {
        return catalogMetadataRepository;
    }

    public ConnectivityMetadataRepository getConnectivityMetadataRepository() {
        return connectivityMetadataRepository;
    }

    public TraceMetadataRepository getTraceMetadataRepository() {
        return traceMetadataRepository;
    }

    public ResultMetadataRepository getResultMetadataRepository() {
        return resultMetadataRepository;
    }

    public DesignMetadataRepository getDesignMetadataRepository() {
        return designMetadataRepository;
    }

    public MonitorMetadataRepository getMonitorMetadataRepository() {
        return monitorMetadataRepository;
    }

    public LedgerMetadataRepository getLedgerMetadataRepository() {
        return ledgerMetadataRepository;
    }

    public ControlMetadataRepository getControlMetadataRepository() {
        return controlMetadataRepository;
    }

    public ExecutionServerMetadataRepository getExecutionServerMetadataRepository() {
        return executionServerMetadataRepository;
    }

    public boolean isValid() {
        return (catalogMetadataRepository != null && connectivityMetadataRepository !=null && controlMetadataRepository != null &&
        designMetadataRepository != null && resultMetadataRepository != null && traceMetadataRepository != null);
    }

}