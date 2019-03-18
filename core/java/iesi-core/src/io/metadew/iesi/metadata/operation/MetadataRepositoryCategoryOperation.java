package io.metadew.iesi.metadata.operation;

import io.metadew.iesi.framework.configuration.FrameworkFolderConfiguration;
import io.metadew.iesi.metadata.configuration.MetadataRepositoryCategoryConfiguration;

/**
 * This class contains the configuration objects for all types of metadata repositories
 * 
 * @author peter.billen
 *
 */
public class MetadataRepositoryCategoryOperation
{
	private MetadataRepositoryCategoryConfiguration connectivityMetadataRepository;
	private MetadataRepositoryCategoryConfiguration controlMetadataRepository;
	private MetadataRepositoryCategoryConfiguration designMetadataRepository;
	private MetadataRepositoryCategoryConfiguration resultMetadataRepository;
	private MetadataRepositoryCategoryConfiguration traceMetadataRepository;
	
	public MetadataRepositoryCategoryOperation(FrameworkFolderConfiguration folderConfig) {
		this.setConnectivityMetadataRepository(new MetadataRepositoryCategoryConfiguration("connectivity",folderConfig));
		this.setControlMetadataRepository(new MetadataRepositoryCategoryConfiguration("control",folderConfig));
		this.setDesignMetadataRepository(new MetadataRepositoryCategoryConfiguration("design",folderConfig));
		this.setResultMetadataRepository(new MetadataRepositoryCategoryConfiguration("result",folderConfig));
		this.setTraceMetadataRepository(new MetadataRepositoryCategoryConfiguration("trace",folderConfig));
	}

	// getters and setters
	public MetadataRepositoryCategoryConfiguration getDesignMetadataRepository() {
		return designMetadataRepository;
	}

	public void setDesignMetadataRepository(MetadataRepositoryCategoryConfiguration designMetadataRepository) {
		this.designMetadataRepository = designMetadataRepository;
	}

	public MetadataRepositoryCategoryConfiguration getConnectivityMetadataRepository() {
		return connectivityMetadataRepository;
	}

	public void setConnectivityMetadataRepository(MetadataRepositoryCategoryConfiguration connectivityMetadataRepository) {
		this.connectivityMetadataRepository = connectivityMetadataRepository;
	}

	public MetadataRepositoryCategoryConfiguration getResultMetadataRepository() {
		return resultMetadataRepository;
	}

	public void setResultMetadataRepository(MetadataRepositoryCategoryConfiguration resultMetadataRepository) {
		this.resultMetadataRepository = resultMetadataRepository;
	}

	public MetadataRepositoryCategoryConfiguration getTraceMetadataRepository() {
		return traceMetadataRepository;
	}

	public void setTraceMetadataRepository(MetadataRepositoryCategoryConfiguration traceMetadataRepository) {
		this.traceMetadataRepository = traceMetadataRepository;
	}

	public MetadataRepositoryCategoryConfiguration getControlMetadataRepository() {
		return controlMetadataRepository;
	}

	public void setControlMetadataRepository(MetadataRepositoryCategoryConfiguration controlMetadataRepository) {
		this.controlMetadataRepository = controlMetadataRepository;
	}
	
}