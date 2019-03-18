package io.metadew.iesi.metadata.execution;

import java.util.List;

import io.metadew.iesi.metadata.configuration.MetadataRepositoryConfiguration;

/**
 * Class to centralize the shared metadata that will be used by the framework
 * 
 * @author peter.billen
 *
 */
public class MetadataControl {

	private MetadataRepositoryConfiguration designRepositoryConfiguration;
	private MetadataRepositoryConfiguration connectivityRepositoryConfiguration;
	private MetadataRepositoryConfiguration controlRepositoryConfiguration;
	private MetadataRepositoryConfiguration traceRepositoryConfiguration;
	private MetadataRepositoryConfiguration resultRepositoryConfiguration;
	private MetadataRepositoryConfiguration monitorRepositoryConfiguration;
	private MetadataRepositoryConfiguration generalRepositoryConfiguration;
	private MetadataRepositoryConfiguration ledgerRepositoryConfiguration;
	private MetadataRepositoryConfiguration catalogRepositoryConfiguration;
	private boolean general = false;
	private boolean valid = false;

	public MetadataControl(List<MetadataRepositoryConfiguration> metadataRepositoryConfigurationList) {

		// Set shared metadata
		for (MetadataRepositoryConfiguration metadataRepositoryConfiguration : metadataRepositoryConfigurationList) {
			this.setMetadataRepositoryConfiguration(metadataRepositoryConfiguration);
		}

		// Automatically set repositories if category is set to general
		if (this.getGeneralRepositoryConfiguration() != null) {
			this.setGeneral(true);
			
			this.setConnectivityRepositoryConfiguration(this.getGeneralRepositoryConfiguration().clone());
			this.getConnectivityRepositoryConfiguration().setCategory("connectivity");
			
			this.setControlRepositoryConfiguration(this.getGeneralRepositoryConfiguration().clone());
			this.getControlRepositoryConfiguration().setCategory("control");

			this.setDesignRepositoryConfiguration(this.getGeneralRepositoryConfiguration().clone());
			this.getDesignRepositoryConfiguration().setCategory("design");

			this.setTraceRepositoryConfiguration(this.getGeneralRepositoryConfiguration().clone());
			this.getTraceRepositoryConfiguration().setCategory("trace");
						
			this.setResultRepositoryConfiguration(this.getGeneralRepositoryConfiguration().clone());
			this.getResultRepositoryConfiguration().setCategory("result");
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
		if (this.getConnectivityRepositoryConfiguration() == null) result = false;
		if (this.getControlRepositoryConfiguration() == null) result = false;
		if (this.getDesignRepositoryConfiguration() == null) result = false;
		if (this.getResultRepositoryConfiguration() == null) result = false;
		if (this.getTraceRepositoryConfiguration() == null) result = false;
		
		this.setValid(result);
	}

	private void setMetadataRepositoryConfiguration(MetadataRepositoryConfiguration metadataRepositoryConfiguration) {
		String categories = metadataRepositoryConfiguration.getCategory().toLowerCase();
		int delim = categories.indexOf(",");
		if (delim > 0) {
			String[] category = categories.split(",");
			for (int i = 0; i < category.length; i++) {
				metadataRepositoryConfiguration.setCategory(category[i]);
				this.setMetadataRepositoryConfigurationObject(metadataRepositoryConfiguration);
			}
		} else {
			this.setMetadataRepositoryConfigurationObject(metadataRepositoryConfiguration);
		}
	}

	private void setMetadataRepositoryConfigurationObject(
			MetadataRepositoryConfiguration metadataRepositoryConfiguration) {
		if (metadataRepositoryConfiguration.getCategory().equalsIgnoreCase("connectivity")) {
			this.setConnectivityRepositoryConfiguration(metadataRepositoryConfiguration);
			this.getConnectivityRepositoryConfiguration().setCategory("connectivity");
		} else if (metadataRepositoryConfiguration.getCategory().equalsIgnoreCase("control")) {
			this.setControlRepositoryConfiguration(metadataRepositoryConfiguration);
			this.getControlRepositoryConfiguration().setCategory("control");
		} else if (metadataRepositoryConfiguration.getCategory().equalsIgnoreCase("design")) {
			this.setDesignRepositoryConfiguration(metadataRepositoryConfiguration);
			this.getDesignRepositoryConfiguration().setCategory("design");
		} else if (metadataRepositoryConfiguration.getCategory().equalsIgnoreCase("trace")) {
			this.setTraceRepositoryConfiguration(metadataRepositoryConfiguration);
			this.getTraceRepositoryConfiguration().setCategory("trace");
		} else if (metadataRepositoryConfiguration.getCategory().equalsIgnoreCase("result")) {
			this.setResultRepositoryConfiguration(metadataRepositoryConfiguration);
			this.getResultRepositoryConfiguration().setCategory("result");
		} else if (metadataRepositoryConfiguration.getCategory().equalsIgnoreCase("general")) {
			this.setGeneralRepositoryConfiguration(metadataRepositoryConfiguration);
			this.getGeneralRepositoryConfiguration().setCategory("general");
		}
	}

	// Getters and Setters
	public MetadataRepositoryConfiguration getConnectivityRepositoryConfiguration() {
		return connectivityRepositoryConfiguration;
	}

	public void setConnectivityRepositoryConfiguration(
			MetadataRepositoryConfiguration connectivityRepositoryConfiguration) {
		this.connectivityRepositoryConfiguration = connectivityRepositoryConfiguration;
		this.connectivityRepositoryConfiguration.initCategoryConfiguration("connectivity");
	}

	public MetadataRepositoryConfiguration getTraceRepositoryConfiguration() {
		return traceRepositoryConfiguration;
	}

	public void setTraceRepositoryConfiguration(MetadataRepositoryConfiguration traceRepositoryConfiguration) {
		this.traceRepositoryConfiguration = traceRepositoryConfiguration;
		this.traceRepositoryConfiguration.initCategoryConfiguration("trace");
	}

	public MetadataRepositoryConfiguration getResultRepositoryConfiguration() {
		return resultRepositoryConfiguration;
	}

	public void setResultRepositoryConfiguration(MetadataRepositoryConfiguration resultRepositoryConfiguration) {
		this.resultRepositoryConfiguration = resultRepositoryConfiguration;
		this.resultRepositoryConfiguration.initCategoryConfiguration("result");
	}

	public MetadataRepositoryConfiguration getDesignRepositoryConfiguration() {
		return designRepositoryConfiguration;
	}

	public void setDesignRepositoryConfiguration(MetadataRepositoryConfiguration designRepositoryConfiguration) {
		this.designRepositoryConfiguration = designRepositoryConfiguration;
		this.designRepositoryConfiguration.initCategoryConfiguration("design");

	}

	public MetadataRepositoryConfiguration getGeneralRepositoryConfiguration() {
		return generalRepositoryConfiguration;
	}

	public void setGeneralRepositoryConfiguration(MetadataRepositoryConfiguration generalRepositoryConfiguration) {
		this.generalRepositoryConfiguration = generalRepositoryConfiguration;
	}

	public MetadataRepositoryConfiguration getMonitorRepositoryConfiguration() {
		return monitorRepositoryConfiguration;
	}

	public void setMonitorRepositoryConfiguration(MetadataRepositoryConfiguration monitorRepositoryConfiguration) {
		this.monitorRepositoryConfiguration = monitorRepositoryConfiguration;
		this.monitorRepositoryConfiguration.initCategoryConfiguration("monitoring");
	}

	public boolean isGeneral() {
		return general;
	}

	public void setGeneral(boolean general) {
		this.general = general;
	}

	public MetadataRepositoryConfiguration getLedgerRepositoryConfiguration() {
		return ledgerRepositoryConfiguration;
	}

	public void setLedgerRepositoryConfiguration(MetadataRepositoryConfiguration ledgerRepositoryConfiguration) {
		this.ledgerRepositoryConfiguration = ledgerRepositoryConfiguration;
		this.ledgerRepositoryConfiguration.initCategoryConfiguration("ledger");
	}

	public MetadataRepositoryConfiguration getCatalogRepositoryConfiguration() {
		return catalogRepositoryConfiguration;
	}

	public void setCatalogRepositoryConfiguration(MetadataRepositoryConfiguration catalogRepositoryConfiguration) {
		this.catalogRepositoryConfiguration = catalogRepositoryConfiguration;
		this.catalogRepositoryConfiguration.initCategoryConfiguration("catalog");
	}

	public MetadataRepositoryConfiguration getControlRepositoryConfiguration() {
		return controlRepositoryConfiguration;
	}

	public void setControlRepositoryConfiguration(MetadataRepositoryConfiguration controlRepositoryConfiguration) {
		this.controlRepositoryConfiguration = controlRepositoryConfiguration;
		this.controlRepositoryConfiguration.initCategoryConfiguration("control");
	}

	public boolean isValid() {
		return valid;
	}

	public void setValid(boolean valid) {
		this.valid = valid;
	}

}