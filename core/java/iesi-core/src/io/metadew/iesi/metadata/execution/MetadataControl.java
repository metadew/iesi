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
    private ConnectivityMetadataRepository connectivityMetadataRepository;
    private ControlMetadataRepository controlMetadataRepository;
    private TraceMetadataRepository traceMetadataRepository;
    private ResultMetadataRepository resultMetadataRepository;
    private MonitorMetadataRepository monitorMetadataRepository;
    private LedgerMetadataRepository ledgerMetadataRepository;
    private CatalogMetadataRepository catalogMetadataRepository;
    private boolean general = false;
    private boolean valid = false;

    public MetadataControl(List<MetadataRepository> metadataRepositories) {

        // Set shared metadata
        for (MetadataRepository metadataRepository : metadataRepositories) {
            this.setMetadataRepository(metadataRepository);
        }
        // Check if repositories are correctly set
        this.checkValidity();
        if (!this.isValid()) {
            throw new RuntimeException("framework.metadata.incomplete");
        }
    }

    private void checkValidity() {
        boolean result = true;
        // Mandatory repository settings
        if (this.getConnectivityMetadataRepository() == null) result = false;
        if (this.getControlMetadataRepository() == null) result = false;
        if (this.getDesignMetadataRepository() == null) result = false;
        if (this.getResultMetadataRepository() == null) result = false;
        if (this.getTraceMetadataRepository() == null) result = false;

        this.setValid(result);
    }

    private void setMetadataRepository(MetadataRepository metadataRepository) {
        if (metadataRepository.getCategory().equalsIgnoreCase("connectivity")) {
            this.setConnectivityMetadataRepository((ConnectivityMetadataRepository) metadataRepository);
        } else if (metadataRepository.getCategory().equalsIgnoreCase("control")) {
            this.setControlMetadataRepository((ControlMetadataRepository) metadataRepository);
        } else if (metadataRepository.getCategory().equalsIgnoreCase("design")) {
            this.setDesignMetadataRepository((DesignMetadataRepository) metadataRepository);
        } else if (metadataRepository.getCategory().equalsIgnoreCase("trace")) {
            this.setTraceMetadataRepository((TraceMetadataRepository) metadataRepository);
        } else if (metadataRepository.getCategory().equalsIgnoreCase("result")) {
            this.setResultMetadataRepository((ResultMetadataRepository) metadataRepository);
        } else {
            throw new RuntimeException(MessageFormat.format("No Metadata repository of type {0} can be set", metadataRepository.getCategory()));
        }
    }

    // Getters and Setters
    public ConnectivityMetadataRepository getConnectivityMetadataRepository() {
        return connectivityMetadataRepository;
    }

    public void setConnectivityMetadataRepository(
            ConnectivityMetadataRepository connectivityMetadataRepository) {
        this.connectivityMetadataRepository = connectivityMetadataRepository;
    }

    public TraceMetadataRepository getTraceMetadataRepository() {
        return traceMetadataRepository;
    }

    public void setTraceMetadataRepository(TraceMetadataRepository traceMetadataRepository) {
        this.traceMetadataRepository = traceMetadataRepository;
    }

    public ResultMetadataRepository getResultMetadataRepository() {
        return resultMetadataRepository;
    }

    public void setResultMetadataRepository(ResultMetadataRepository resultMetadataRepository) {
        this.resultMetadataRepository = resultMetadataRepository;
    }

    public DesignMetadataRepository getDesignMetadataRepository() {
        return designMetadataRepository;
    }

    public void setDesignMetadataRepository(DesignMetadataRepository designMetadataRepository) {
        this.designMetadataRepository = designMetadataRepository;
    }

    public MonitorMetadataRepository getMonitorMetadataRepository() {
        return monitorMetadataRepository;
    }

    public void setMonitorMetadataRepository(MonitorMetadataRepository monitorMetadataRepository) {
        this.monitorMetadataRepository = monitorMetadataRepository;
    }

    public boolean isGeneral() {
        return general;
    }

    public void setGeneral(boolean general) {
        this.general = general;
    }

    public LedgerMetadataRepository getLedgerMetadataRepository() {
        return ledgerMetadataRepository;
    }

    public void setLedgerMetadataRepository(LedgerMetadataRepository ledgerMetadataRepository) {
        this.ledgerMetadataRepository = ledgerMetadataRepository;
    }

    public CatalogMetadataRepository getCatalogMetadataRepository() {
        return catalogMetadataRepository;
    }

    public void setCatalogMetadataRepository(CatalogMetadataRepository catalogMetadataRepository) {
        this.catalogMetadataRepository = catalogMetadataRepository;
    }

    public ControlMetadataRepository getControlMetadataRepository() {
        return controlMetadataRepository;
    }

    public void setControlMetadataRepository(ControlMetadataRepository controlMetadataRepository) {
        this.controlMetadataRepository = controlMetadataRepository;
    }

    public boolean isValid() {
        return valid;
    }

    public void setValid(boolean valid) {
        this.valid = valid;
    }

}