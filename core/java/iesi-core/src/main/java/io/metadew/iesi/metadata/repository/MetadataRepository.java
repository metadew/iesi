package io.metadew.iesi.metadata.repository;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.metadew.iesi.framework.configuration.FrameworkConfiguration;
import io.metadew.iesi.framework.configuration.FrameworkFolderConfiguration;
import io.metadew.iesi.metadata.definition.DataObject;
import io.metadew.iesi.metadata.definition.MetadataObject;
import io.metadew.iesi.metadata.definition.MetadataTable;
import io.metadew.iesi.metadata.operation.DataObjectOperation;
import io.metadew.iesi.metadata.repository.coordinator.RepositoryCoordinator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.sql.rowset.CachedRowSet;
import java.io.File;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public abstract class MetadataRepository {

	// TODO: propagate SQLExcpetion
	private final static Logger LOGGER = LogManager.getLogger();
	private final String tablePrefix;

	RepositoryCoordinator repositoryCoordinator;
	String name;
	String scope;
	private List<MetadataObject> metadataObjects;
	private List<MetadataTable> metadataTables;

	public MetadataRepository(String name, String scope, String instanceName,
							  RepositoryCoordinator repositoryCoordinator) {
		this.tablePrefix = FrameworkConfiguration.getInstance().getFrameworkCode().toUpperCase() + "_" + (instanceName != null ? instanceName + "_" : "");
		this.name = name;
		this.scope = scope;
		this.repositoryCoordinator = repositoryCoordinator;
		metadataObjects = new ArrayList<>();
		metadataTables = new ArrayList<>();

		DataObjectOperation dataObjectOperation = new DataObjectOperation();
		dataObjectOperation.setInputFile(FrameworkFolderConfiguration.getInstance().getFolderAbsolutePath("metadata.def") + File.separator + getObjectDefinitionFileName());
		dataObjectOperation.parseFile();
		ObjectMapper objectMapper = new ObjectMapper();
		//
		for (DataObject dataObject : dataObjectOperation.getDataObjects()) {
			if (dataObject.getType().equalsIgnoreCase("metadataobject")) {
				metadataObjects.add(objectMapper.convertValue(dataObject.getData(), MetadataObject.class));
			}
		}

		dataObjectOperation = new DataObjectOperation();
		dataObjectOperation.setInputFile(FrameworkFolderConfiguration.getInstance().getFolderAbsolutePath("metadata.def") + File.separator + getDefinitionFileName());
		dataObjectOperation.parseFile();
		//
		for (DataObject dataObject : dataObjectOperation.getDataObjects()) {
			if (dataObject.getType().equalsIgnoreCase("metadatatable")) {
				MetadataTable metadataTable = objectMapper.convertValue(dataObject.getData(), MetadataTable.class);
				metadataTable.setName(tablePrefix + metadataTable.getName());
				metadataTables.add(metadataTable);
			}
		}

	}

	public abstract String getDefinitionFileName();

	public abstract String getObjectDefinitionFileName();

	public abstract String getCategory();

	public abstract String getCategoryPrefix();

	public String getTablePrefix() {
		return tablePrefix;
	}

	private void dropTable(MetadataTable metadataTable) {
		repositoryCoordinator.dropTable(metadataTable);
	}

	public void dropAllTables() {
		metadataTables.forEach(this::dropTable);
	}

	private void cleanTable(MetadataTable metadataTable) {
		repositoryCoordinator.cleanTable(metadataTable);
	}

	public void cleanAllTables() {
		metadataTables.forEach(this::cleanTable);
	}

	// TODO: remove because security danger: query can target objects outside of
	public CachedRowSet executeQuery(String query, String logonType) {
		return repositoryCoordinator.executeQuery(query, logonType);
	}

	// TODO: remove because security danger: query can target objects outside of
	public void executeUpdate(String query) {
		repositoryCoordinator.executeUpdate(query);
	}

	public void executeBatch(List<String> queries) {
		repositoryCoordinator.executeBatch(queries);
	}

	// TODO: remove because security danger: query can target objects outside of
	public void executeScript(String fileName, String logonType) {
		repositoryCoordinator.executeScript(fileName, logonType);
	}

	// TODO: remove because security danger: query can target objects outside of
	public void executeScript(InputStream inputStream, String logonType) {
		repositoryCoordinator.executeScript(inputStream, logonType);
	}

	// TODO: remove because security danger: query can target objects outside of
	// repository responsibilities
	public void executeScript(InputStream inputStream) {
		repositoryCoordinator.executeScript(inputStream, "writer");
	}

	private void createTable(MetadataTable metadataTable) {
		this.repositoryCoordinator.createTable(metadataTable);
	}

	public void createAllTables() {
		metadataTables.forEach(this::createTable);
	}

	public String generateDDL() {
		return metadataTables.stream()
				.map(metadataTable -> repositoryCoordinator.getDropStatement(metadataTable))
				.collect(Collectors.joining("\n\n")) + "\n\n" +
				metadataTables.stream()
				.map(metadataTable -> repositoryCoordinator.getCreateStatement(metadataTable))
				.collect(Collectors.joining("\n\n"));
	}

	public String getTableNameByLabel(String label) {
		return metadataTables.stream()
				.filter(metadataTable -> metadataTable.getLabel().equalsIgnoreCase(label))
				.findFirst()
				.map(MetadataTable::getName)
				.orElseThrow(() -> new RuntimeException(MessageFormat.format("No label {0} found for metadata repository {1}", label, getCategory())));
	}

	public abstract void save(DataObject dataObject) throws MetadataRepositorySaveException;

	public void shutdown() {
		repositoryCoordinator.shutdown();
	}
}
