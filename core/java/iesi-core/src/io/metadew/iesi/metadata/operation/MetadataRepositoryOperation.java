package io.metadew.iesi.metadata.operation;

import java.io.File;
import java.io.FilenameFilter;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import io.metadew.iesi.metadata_repository.MetadataRepository;
import org.apache.commons.io.FilenameUtils;
import org.apache.logging.log4j.Level;

import javax.sql.rowset.CachedRowSet;

import io.metadew.iesi.common.list.ListTools;
import io.metadew.iesi.common.text.ParsingTools;
import io.metadew.iesi.connection.tools.FileTools;
import io.metadew.iesi.framework.execution.FrameworkExecution;
import io.metadew.iesi.metadata.configuration.MetadataRepositoryCategoryConfiguration;

public class MetadataRepositoryOperation {

	private FrameworkExecution frameworkExecution;
	private MetadataRepository metadataRepository;
	private String action;
	private boolean generateDdl;

	// Constructors

	public MetadataRepositoryOperation(FrameworkExecution frameworkExecution,
			MetadataRepository metadataRepository) {
		this.setFrameworkExecution(frameworkExecution);
		this.setMetadataRepository(metadataRepository);
	}

	// Methods
	public void cleanAllTables() {
		this.getFrameworkExecution().getFrameworkLog().log("metadata.clean.start", Level.INFO);

		this.getFrameworkExecution().getFrameworkLog().log("metadata.clean.query=" + "", Level.TRACE);
		this.getMetadataRepository().cleanAllTables(frameworkExecution.getFrameworkLog());

		this.getFrameworkExecution().getFrameworkLog().log("metadata.clean.end", Level.INFO);

	}

	// Drop the metadata data store
	public void drop() {
//		if (this.getMetadataRepository().getGroup().equalsIgnoreCase("filestore")) {
//			MetadataFileStoreRepositoryImpl metadataFileStoreRepositoryImpl = new MetadataFileStoreRepositoryImpl(
//					this.getFrameworkExecution());
//			metadataFileStoreRepositoryImpl.dropStructure();
//		}
//		else if (this.getMetadataRepository().getGroup().equalsIgnoreCase("database")) {
//			this.dropAllTables();
//		} else {
//			throw new RuntimeException("metadata.repository.group.invalid");
//		}
		this.dropAllTables();
	}

	public void dropAllTables() {
		this.getFrameworkExecution().getFrameworkLog().log("metadata.drop.start", Level.INFO);
		this.getMetadataRepository().dropAllTables(frameworkExecution.getFrameworkLog());
//		CachedRowSet crsDropInventory;
//		String queryDropInventory = this.getAllTablesQuery();
//		this.getFrameworkExecution().getFrameworkLog().log("metadata.drop.query=" + queryDropInventory, Level.TRACE);
//		crsDropInventory = this.getMetadataRepository().executeQuery(queryDropInventory, "owner");
//		try {
//			String tableName;
//			String schemaName;
//			while (crsDropInventory.next()) {
//				schemaName = crsDropInventory.getString("OWNER");
//				tableName = crsDropInventory.getString("TABLE_NAME");
//				if (schemaName.equals("")) {
//					this.getFrameworkExecution().getFrameworkLog().log("metadata.drop.table=" + tableName, Level.INFO);
//				} else {
//					this.getFrameworkExecution().getFrameworkLog()
//							.log("metadata.drop.table=" + schemaName + "." + tableName, Level.INFO);
//				}
//				this.getMetadataRepository().dropTable(schemaName, tableName, "owner");
//			}
//			crsDropInventory.close();
//		} catch (Exception e) {
//			StringWriter StackTrace = new StringWriter();
//			e.printStackTrace(new PrintWriter(StackTrace));
//		}

		this.getFrameworkExecution().getFrameworkLog().log("metadata.drop.end", Level.INFO);

	}

//	private String getAllTablesQuery() {
//		String query = "";
//		if (this.getMetadataRepository().getDatabaseConnection().getType().toLowerCase()
//				.equals("oracle")) {
//			query = "select OWNER, TABLE_NAME from ALL_TABLES where owner = '"
//					+ this.getMetadataRepository().getMetadataTableConfiguration().getSchema() + "' and TABLE_NAME like '"
//					+ this.getMetadataRepository().getMetadataTableConfiguration().getTableNamePrefix()
//					+ this.getMetadataRepository().getMetadataRepositoryCategoryConfiguration().getPrefix()
//					+ "%' order by TABLE_NAME ASC";
//		} else if (this.getMetadataRepository().getDatabaseConnection().getType().toLowerCase()
//				.equals("sqlite")) {
//			query = "select tbl_name 'TABLE_NAME', '' 'OWNER' from sqlite_master where tbl_name like '"
//					+ this.getMetadataRepository().getMetadataTableConfiguration().getTableNamePrefix()
//					+ this.getMetadataRepository().getMetadataRepositoryCategoryConfiguration().getPrefix()
//					+ "%' order by tbl_name asc";
//		} else if (this.getMetadataRepository().getDatabaseConnection().getType().toLowerCase()
//				.equals("netezza")) {
//			query = "select SCHEMA as \"OWNER\", TABLENAME as \"TABLE_NAME\" from _V_TABLE where OWNER = '"
//					+ this.getFrameworkExecution().getFrameworkControl().getProperty(
//							this.getFrameworkExecution().getFrameworkConfiguration().getSettingConfiguration()
//									.getSettingPath("metadata.repository.netezza.schema.user"))
//					+ "' and TABLENAME like '"
//					+ this.getMetadataRepository().getMetadataTableConfiguration().getTableNamePrefix()
//					+ this.getMetadataRepository().getMetadataRepositoryCategoryConfiguration().getPrefix()
//					+ "%' order by TABLENAME asc";
//		} else if (this.getMetadataRepository().getDatabaseConnection().getType().toLowerCase()
//				.equals("postgresql")) {
//			query = "select table_schema as \"OWNER\", table_name as \"TABLE_NAME\" from information_schema.tables where table_schema = '"
//					+ this.getFrameworkExecution().getFrameworkControl()
//							.getProperty(this.getFrameworkExecution().getFrameworkConfiguration()
//									.getSettingConfiguration().getSettingPath("metadata.repository.postgresql.schema"))
//					+ "' and table_name like '"
//					+ this.getMetadataRepository().getMetadataTableConfiguration().getTableNamePrefix().toLowerCase()
//					+ this.getMetadataRepository().getMetadataRepositoryCategoryConfiguration().getPrefix()
//					+ "%' order by table_name asc";
//		}
//		return query;
//	}

	// Create the metadata data store
	public void create(boolean generateDdl) {
		this.setAction("create");
		this.setGenerateDdl(generateDdl);
		this.createAllTables();
//		if (this.getMetadataRepository().getGroup().equalsIgnoreCase("filestore")) {
//			MetadataFileStoreRepositoryImpl metadataFileStoreRepositoryImpl = new MetadataFileStoreRepositoryImpl(
//					this.getFrameworkExecution());
//			metadataFileStoreRepositoryImpl.createStructure();
//		}
//		else if (this.getMetadataRepository().getGroup().equalsIgnoreCase("database")) {
//			this.createAllTables();
//		} else {
//			throw new RuntimeException("metadata.repository.group.invalid");
//		}
	}

	@SuppressWarnings({ "unused", "unchecked", "rawtypes" })
	private void createAllTables() {
		this.getFrameworkExecution().getFrameworkLog().log("metadata.create.start", Level.INFO);

		final File folder = new File(this.getFrameworkExecution().getFrameworkConfiguration().getFolderConfiguration()
				.getFolderAbsolutePath("metadata.def"));
		this.getFrameworkExecution().getFrameworkLog().log("metadata.create.folder=" + folder.getPath(), Level.INFO);

		List<MetadataRepository> metadataRepositories = new ArrayList();
		metadataRepositories.add(this.getMetadataRepository());
		
		// Select appropriate definition file
		String[] files = null;
		MetadataRepositoryCategoryConfiguration[] metadataRepositoryCategoryConfigurations = null;
		String metadataRepositoryCategory = this.getMetadataRepository().getCategory();
		if (metadataRepositoryCategory == null)
			metadataRepositoryCategory = "";
		if (metadataRepositoryCategory.equals("metadew")) {
			files = new String[] { "MetadewTables.json" };
		} else if (metadataRepositoryCategory.equals("connectivity")) {
			files = new String[] { "ConnectivityTables.json" };
		} else if (metadataRepositoryCategory.equals("control")) {
			files = new String[] { "ControlTables.json" };
		} else if (metadataRepositoryCategory.equals("design")) {
			files = new String[] { "DesignTables.json" };
		} else if (metadataRepositoryCategory.equals("trace")) {
			files = new String[] { "TraceTables.json" };
		} else if (metadataRepositoryCategory.equals("result")) {
			files = new String[] { "ResultTables.json" };
		} else if (metadataRepositoryCategory.equals("general")) {
			files = new String[] { "ConnectivityTables.json", "ControlTables.json", "DesignTables.json",
					"ResultTables.json", "TraceTables.json" };
			metadataRepositoryCategoryConfigurations = new MetadataRepositoryCategoryConfiguration[] {
					this.getFrameworkExecution().getFrameworkControl().getMetadataRepositoryConfig().getConnectivityMetadataRepository(),
					this.getFrameworkExecution().getFrameworkControl().getMetadataRepositoryConfig().getControlMetadataRepository(),
					this.getFrameworkExecution().getFrameworkControl().getMetadataRepositoryConfig().getDesignMetadataRepository(),
					this.getFrameworkExecution().getFrameworkControl().getMetadataRepositoryConfig().getResultMetadataRepository(),
					this.getFrameworkExecution().getFrameworkControl().getMetadataRepositoryConfig().getTraceMetadataRepository() };
		} else {
			files = new String[] { "ConnectivityTables.json", "DesignTables.json", "ResultTables.json",
					"TraceTables.json" };
		}

		this.loadConfigurationSelection(metadataRepositories, this.getFrameworkExecution().getFrameworkConfiguration()
				.getFolderConfiguration().getFolderAbsolutePath("metadata.def"), "", "", "", files);

		this.getFrameworkExecution().getFrameworkLog().log("metadata.create.end", Level.INFO);

	}

	public void loadMetadataRepository(List<MetadataRepository> metadataRepositoryList) {
		this.loadMetadataRepository(metadataRepositoryList, "");
	}

	public void loadMetadataRepository(List<MetadataRepository> metadataRepositories, String input) {
		this.getFrameworkExecution().getFrameworkLog().log("metadata.load.start", Level.INFO);

		// Folder definition
		String inputFolder = FilenameUtils.normalize(this.getFrameworkExecution().getFrameworkConfiguration()
				.getFolderConfiguration().getFolderAbsolutePath("metadata.in.new"));
		String workFolder = FilenameUtils.normalize(this.getFrameworkExecution().getFrameworkConfiguration()
				.getFolderConfiguration().getFolderAbsolutePath("metadata.in.work"));
		String errorFolder = FilenameUtils.normalize(this.getFrameworkExecution().getFrameworkConfiguration()
				.getFolderConfiguration().getFolderAbsolutePath("metadata.in.error"));
		String archiveFolder = FilenameUtils.normalize(this.getFrameworkExecution().getFrameworkConfiguration()
				.getFolderConfiguration().getFolderAbsolutePath("metadata.in.done"));

		// Load files
		if (input.trim().equals("")) {
			this.loadConfigurationSelection(metadataRepositories, inputFolder, workFolder, archiveFolder, errorFolder, ".+\\.json");
		} else {
			if (ParsingTools.isRegexFunction(input)) {
				this.loadConfigurationSelection(metadataRepositories, inputFolder, workFolder, archiveFolder, errorFolder,
						ParsingTools.getRegexFunctionValue(input));
			} else {
				List<String> fileList = ListTools.convertStringList(input, ",");
				for (String file : fileList) {
					this.loadConfigurationItem(metadataRepositories, inputFolder, workFolder, archiveFolder, errorFolder, file);
				}
			}
		}

		this.getFrameworkExecution().getFrameworkLog().log("metadata.load.end", Level.INFO);

	}

	// Load a single file
	private void loadConfigurationItem(List<MetadataRepository> metadataRepositoryList, String inputFolder, String workFolder, String archiveFolder, String errorFolder,
									   String inputFileName) {
		File file = new File(FilenameUtils.normalize(inputFolder + File.separator + inputFileName));
		this.loadConfigurationFile(metadataRepositoryList, file, inputFolder, workFolder, archiveFolder, errorFolder);
	}

	// Load entire folder
	private void loadConfigurationSelection(List<MetadataRepository> metadataRepositories, String inputFolder, String workFolder, String archiveFolder,
											String errorFolder, String[] files) {
		for (String file : files) {
			System.out.println(file);
			File activeFile = new File(FilenameUtils.normalize(inputFolder + File.separator + file));
			this.loadConfigurationFile(metadataRepositories, activeFile, inputFolder, workFolder, archiveFolder, errorFolder);
		}
	}

	// Load entire folder
	private void loadConfigurationSelection(List<MetadataRepository> metadataRepositories, String inputFolder, String workFolder, String archiveFolder,
											String errorFolder, String regex) {
		final File folder = new File(FilenameUtils.normalize(inputFolder));
		final String file_filter = regex;
		final File[] files = folder.listFiles(new FilenameFilter() {
			@Override
			public boolean accept(final File dir, final String name) {
				return name.matches(file_filter);
			}
		});

		for (final File file : files) {
			this.loadConfigurationFile(metadataRepositories, file, inputFolder, workFolder, archiveFolder, errorFolder);
		}
	}

	private void loadConfigurationFile(List<MetadataRepository> metadataRepositories, File file, String inputFolder, String workFolder, String archiveFolder,
									   String errorFolder) {
		
		UUID uuid = UUID.randomUUID();

		boolean moveToWorkFolder = false;
		boolean moveToArchiveFolder = false;
		boolean moveToErrorFolder = false;

		if (!workFolder.trim().equals(""))
			moveToWorkFolder = true;

		if (moveToWorkFolder) {
			FileTools.copyFromFileToFile(FilenameUtils.normalize(inputFolder + File.separator + file.getName()),
					FilenameUtils.normalize(workFolder + File.separator + file.getName()));
			FileTools.delete((FilenameUtils.normalize(inputFolder + File.separator + file.getName())));
		} else {
			workFolder = inputFolder;
		}

		if (!archiveFolder.trim().equals(""))
			moveToArchiveFolder = true;
		if (!errorFolder.trim().equals(""))
			moveToErrorFolder = true;

		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
		SimpleDateFormat timeFormat = new SimpleDateFormat("HHmmss");

		File workFile = new File(FilenameUtils.normalize(workFolder + File.separator + file.getName()));
		if (!workFile.isDirectory()) {
			try {
				this.getFrameworkExecution().getFrameworkLog().log("metadata.file=" + file.getName(), Level.INFO);
				DataObjectOperation dataObjectOperation = new DataObjectOperation(this.getFrameworkExecution(),metadataRepositories, workFile.getAbsolutePath());
				if (this.isGenerateDdl()) {
					this.saveMetadataRepositoryDDL(dataObjectOperation.getMetadataRepositoryDdl());
				} else {
					System.out.println("DataObjectOperation size: " + dataObjectOperation.getDataObjects().size());
					dataObjectOperation.saveToMetadataRepository();
				}

				// Move file to archive folder
				if (moveToArchiveFolder) {
					String archiveFileName = dateFormat.format(new Date()) + "-" + timeFormat.format(new Date()) + "-"
							+ uuid + "-" + workFile.getName();
					FileTools.copyFromFileToFile(workFile.getAbsolutePath(),
							FilenameUtils.normalize(archiveFolder + File.separator + archiveFileName));
					FileTools.delete(workFile.getAbsolutePath());
				}

			} catch (Exception e) {

				// Move file to error folder
				if (moveToErrorFolder) {
					String errorFileName = dateFormat.format(new Date()) + "-" + timeFormat.format(new Date()) + "-"
							+ uuid + "-" + file.getName();
					FileTools.copyFromFileToFile(workFile.getAbsolutePath(),
							FilenameUtils.normalize(errorFolder + File.separator + errorFileName));
					FileTools.delete(workFile.getAbsolutePath());
				}

			}
		}

	}

	@SuppressWarnings("unused")
	private void createMetadataRepository(File file, String archiveFolder, String errorFolder, UUID uuid) {

		boolean moveToArchiveFolder = false;
		boolean moveToErrorFolder = false;

		if (!archiveFolder.trim().equals(""))
			moveToArchiveFolder = true;
		if (!errorFolder.trim().equals(""))
			moveToErrorFolder = true;

		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
		SimpleDateFormat timeFormat = new SimpleDateFormat("HHmmss");

		if (file.isDirectory()) {
			// Ignore
		} else {
			try {
				this.getFrameworkExecution().getFrameworkLog().log("metadata.file=" + file.getName(), Level.INFO);
				DataObjectOperation dataObjectOperation = new DataObjectOperation(this.getFrameworkExecution(),
						this.getMetadataRepository(), file.getAbsolutePath());
				if (this.isGenerateDdl()) {
					this.saveMetadataRepositoryDDL(dataObjectOperation.getMetadataRepositoryDdl());
				} else {
					dataObjectOperation.saveToMetadataRepository();
				}

				// Move file to archive folder
				if (moveToArchiveFolder) {
					String archiveFileName = dateFormat.format(new Date()) + "-" + timeFormat.format(new Date()) + "-"
							+ uuid + "-" + file.getName();
					FileTools.copyFromFileToFile(file.getAbsolutePath(),
							archiveFolder + File.separator + archiveFileName);
					FileTools.delete(file.getAbsolutePath());
				}

			} catch (Exception e) {

				// Move file to archive folder
				if (moveToErrorFolder) {
					String errorFileName = dateFormat.format(new Date()) + "-" + timeFormat.format(new Date()) + "-"
							+ uuid + "-" + file.getName();
					FileTools.copyFromFileToFile(file.getAbsolutePath(), errorFolder + File.separator + errorFileName);
					FileTools.delete(file.getAbsolutePath());
				}

			}
		}

	}

	private void saveMetadataRepositoryDDL(String ddl) {
		StringBuilder targetFilePath = new StringBuilder();
		targetFilePath.append(this.getFrameworkExecution().getFrameworkConfiguration().getFolderConfiguration()
				.getFolderAbsolutePath("metadata.out.ddl"));
		targetFilePath.append(File.separator);
		targetFilePath.append(this.getMetadataRepository().getName());
		targetFilePath.append("_");
		targetFilePath.append(this.getMetadataRepository().getCategory());
		targetFilePath.append("_");
		targetFilePath.append("create");
		targetFilePath.append(".ddl");
		FileTools.delete(targetFilePath.toString());
		FileTools.appendToFile(targetFilePath.toString(), "", ddl);
	}

	// Getters and setters
	public FrameworkExecution getFrameworkExecution() {
		return frameworkExecution;
	}

	public void setFrameworkExecution(FrameworkExecution frameworkExecution) {
		this.frameworkExecution = frameworkExecution;
	}

	public MetadataRepository getMetadataRepository() {
		return metadataRepository;
	}

	public void setMetadataRepository(MetadataRepository metadataRepository) {
		this.metadataRepository = metadataRepository;
	}

	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}

	public boolean isGenerateDdl() {
		return generateDdl;
	}

	public void setGenerateDdl(boolean generateDdl) {
		this.generateDdl = generateDdl;
	}
}