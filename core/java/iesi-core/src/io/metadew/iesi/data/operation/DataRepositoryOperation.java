//package io.metadew.iesi.data.operation;
//
//import java.io.File;
//import java.io.FilenameFilter;
//import java.io.InputStream;
//import java.io.PrintWriter;
//import java.io.StringWriter;
//import java.text.SimpleDateFormat;
//import java.util.Date;
//import java.util.List;
//import java.util.UUID;
//
//import org.apache.commons.io.FilenameUtils;
//import org.apache.logging.log4j.Level;
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//
//import javax.sql.rowset.CachedRowSet;
//
//import io.metadew.iesi.common.list.ListTools;
//import io.metadew.iesi.common.text.ParsingTools;
//import io.metadew.iesi.connection.database.SqliteDatabaseConnection;
//import io.metadew.iesi.connection.tools.FileTools;
//import io.metadew.iesi.data.configuration.DataRepositoryConfiguration;
//import io.metadew.iesi.framework.execution.FrameworkExecution;
//import io.metadew.iesi.metadata.configuration.DataObjectConfiguration;
//import io.metadew.iesi.metadata.configuration.repository.MetadataRepositoryConfigurationBack;
//import io.metadew.iesi.metadata.operation.DataObjectOperation;
//
//public class DataRepositoryOperation {
//
//	private FrameworkExecution frameworkExecution;
//	private DataRepositoryConfiguration dataRepositoryConfiguration;
//	private String action;
//	private boolean generateDdl;
//
//	// Constructors
//
//	public DataRepositoryOperation(FrameworkExecution frameworkExecution,
//			DataRepositoryConfiguration dataRepositoryConfiguration) {
//		this.setFrameworkExecution(frameworkExecution);
//		this.setDataRepositoryConfiguration(dataRepositoryConfiguration);
//	}
//
//	// Methods
//	public void cleanAllTables() {
//		this.getFrameworkExecution().getFrameworkLog().log("data.clean.start", Level.INFO);
//
//		CachedRowSet crsCleanInventory = null;
//		String queryCleanInventory = this.getAllTablesQuery();
//		this.getFrameworkExecution().getFrameworkLog().log("data.clean.query=" + queryCleanInventory, Level.TRACE);
//		crsCleanInventory = this.getDataRepositoryConfiguration().executeQuery(queryCleanInventory);
//		try {
//			String tableName = "";
//			String schemaName = "";
//			while (crsCleanInventory.next()) {
//				schemaName = crsCleanInventory.getString("OWNER");
//				tableName = crsCleanInventory.getString("TABLE_NAME");
//
//				// Exeception for metadata about the data model
//				if (tableName.endsWith("CFG_MTD_TBL") || tableName.endsWith("CFG_MTD_FLD"))
//					continue;
//
//				if (schemaName.equals("")) {
//					this.getFrameworkExecution().getFrameworkLog().log("data.clean.table=" + tableName, Level.INFO);
//				} else {
//					this.getFrameworkExecution().getFrameworkLog()
//							.log("data.clean.table=" + schemaName + "." + tableName, Level.INFO);
//				}
//				this.getDataRepositoryConfiguration().cleanTable(schemaName, tableName);
//			}
//			crsCleanInventory.close();
//		} catch (Exception e) {
//			StringWriter StackTrace = new StringWriter();
//			e.printStackTrace(new PrintWriter(StackTrace));
//		}
//
//		this.getFrameworkExecution().getFrameworkLog().log("data.clean.end", Level.INFO);
//
//	}
//
//	// Drop the metadata data store
//	public void drop() {
//		this.dropAllTables();
//	}
//
//	public void dropAllTables() {
//		this.getFrameworkExecution().getFrameworkLog().log("data.drop.start", Level.INFO);
//
//		CachedRowSet crsDropInventory = null;
//		String queryDropInventory = this.getAllTablesQuery();
//		this.getFrameworkExecution().getFrameworkLog().log("data.drop.query=" + queryDropInventory, Level.TRACE);
//		crsDropInventory = this.getDataRepositoryConfiguration().executeQuery(queryDropInventory);
//		try {
//			String tableName = "";
//			String schemaName = "";
//			while (crsDropInventory.next()) {
//				schemaName = crsDropInventory.getString("OWNER");
//				tableName = crsDropInventory.getString("TABLE_NAME");
//				if (schemaName.equals("")) {
//					this.getFrameworkExecution().getFrameworkLog().log("data.drop.table=" + tableName, Level.INFO);
//				} else {
//					this.getFrameworkExecution().getFrameworkLog()
//							.log("data.drop.table=" + schemaName + "." + tableName, Level.INFO);
//				}
//				this.getDataRepositoryConfiguration().dropTable(schemaName, tableName);
//			}
//			crsDropInventory.close();
//		} catch (Exception e) {
//			StringWriter StackTrace = new StringWriter();
//			e.printStackTrace(new PrintWriter(StackTrace));
//		}
//
//		this.getFrameworkExecution().getFrameworkLog().log("data.drop.end", Level.INFO);
//
//	}
//
//	private String getAllTablesQuery() {
//		String query = "";
//		if (this.getDataRepositoryConfiguration().getDatabaseConnection().getType().toLowerCase().equals("oracle")) {
//			query = "select OWNER, TABLE_NAME from ALL_TABLES where owner = '"
//					+ this.getDataRepositoryConfiguration().getSchema() + "' and TABLE_NAME like '"
//					+ this.getDataRepositoryConfiguration().getRepositoryTableNamePrefix()
//					+ "%' order by TABLE_NAME ASC";
//		} else if (this.getDataRepositoryConfiguration().getDatabaseConnection().getType().toLowerCase()
//				.equals("sqlite")) {
//			query = "select tbl_name 'TABLE_NAME', '' 'OWNER' from sqlite_master where tbl_name like '"
//					+ this.getDataRepositoryConfiguration().getRepositoryTableNamePrefix() + "%' order by tbl_name asc";
//		} else if (this.getDataRepositoryConfiguration().getDatabaseConnection().getType().toLowerCase()
//				.equals("netezza")) {
//			query = "select SCHEMA as \"OWNER\", TABLENAME as \"TABLE_NAME\" from _V_TABLE where OWNER = '"
//					+ this.getDataRepositoryConfiguration().getSchema() + "' and TABLENAME like '"
//					+ this.getDataRepositoryConfiguration().getRepositoryTableNamePrefix()
//					+ "%' order by TABLENAME asc";
//		} else if (this.getDataRepositoryConfiguration().getDatabaseConnection().getType().toLowerCase()
//				.equals("postgresql")) {
//			query = "select table_schema as \"OWNER\", table_name as \"TABLE_NAME\" from information_schema.tables where table_schema = '"
//					+ this.getDataRepositoryConfiguration().getSchema() + "' and table_name like '"
//					+ this.getDataRepositoryConfiguration().getRepositoryTableNamePrefix()
//					+ "%' order by table_name asc";
//		}
//		return query;
//	}
//
//	// Create the metadata data store
//	public void create(boolean generateDdl) {
//		this.setAction("create");
//		this.setGenerateDdl(generateDdl);
//		this.createAllTables();
//	}
//
//	private void createAllTables() {
//		this.getFrameworkExecution().getFrameworkLog().log("data.create.start", Level.INFO);
//
//		final File folder = new File(this.getFrameworkExecution().getFrameworkConfiguration().getFolderConfiguration()
//				.getFolderAbsolutePath("metadata.def"));
//		this.getFrameworkExecution().getFrameworkLog().log("data.create.folder=" + folder.getPath(), Level.INFO);
//
//		// Select appropriate definition file
//		String[] files = null;
//		files = new String[] { "DataTables.json" };
//
//		this.loadConfigurationSelection(this.getFrameworkExecution().getFrameworkConfiguration()
//				.getFolderConfiguration().getFolderAbsolutePath("metadata.def"), "", "", "", files);
//
//		this.getFrameworkExecution().getFrameworkLog().log("data.create.end", Level.INFO);
//
//	}
//
//	public void loadMetadataRepository(String[] files) {
//		this.loadMetadataRepository(files, "");
//	}
//
//	public void loadMetadataRepository(String[] files, String input) {
//		this.getFrameworkExecution().getFrameworkLog().log("data.load.start", Level.INFO);
//
//		// Folder definition
//		String inputFolder = FilenameUtils.normalize(this.getFrameworkExecution().getFrameworkConfiguration()
//				.getFolderConfiguration().getFolderAbsolutePath("data.in.new"));
//		String workFolder = FilenameUtils.normalize(this.getFrameworkExecution().getFrameworkConfiguration()
//				.getFolderConfiguration().getFolderAbsolutePath("data.in.work"));
//		String errorFolder = FilenameUtils.normalize(this.getFrameworkExecution().getFrameworkConfiguration()
//				.getFolderConfiguration().getFolderAbsolutePath("data.in.error"));
//		String archiveFolder = FilenameUtils.normalize(this.getFrameworkExecution().getFrameworkConfiguration()
//				.getFolderConfiguration().getFolderAbsolutePath("data.in.done"));
//
//		// Load files
//		if (input.trim().equals("")) {
//			this.loadConfigurationSelection(inputFolder, workFolder, archiveFolder, errorFolder, ".+\\.json");
//		} else {
//			if (ParsingTools.isRegexFunction(input)) {
//				this.loadConfigurationSelection(inputFolder, workFolder, archiveFolder, errorFolder,
//						ParsingTools.getRegexFunctionValue(input));
//			} else {
//				List<String> fileList = ListTools.convertStringList(input, ",");
//				for (String file : fileList) {
//					this.loadConfigurationItem(inputFolder, workFolder, archiveFolder, errorFolder, file);
//				}
//			}
//		}
//
//		this.getFrameworkExecution().getFrameworkLog().log("data.load.end", Level.INFO);
//
//	}
//
//	// Load a single file
//	private void loadConfigurationItem(String inputFolder, String workFolder, String archiveFolder, String errorFolder,
//			String inputFileName) {
//		File file = new File(FilenameUtils.normalize(inputFolder + File.separator + inputFileName));
//		this.loadConfigurationFile(file, inputFolder, workFolder, archiveFolder, errorFolder);
//	}
//
//	// Load entire folder
//	private void loadConfigurationSelection(String inputFolder, String workFolder, String archiveFolder,
//			String errorFolder, String[] files) {
//		for (String file : files) {
//			File activeFile = new File(FilenameUtils.normalize(inputFolder + File.separator + file));
//			this.loadConfigurationFile(activeFile, inputFolder, workFolder, archiveFolder, errorFolder);
//		}
//	}
//
//	// Load entire folder
//	private void loadConfigurationSelection(String inputFolder, String workFolder, String archiveFolder,
//			String errorFolder, String regex) {
//		final File folder = new File(FilenameUtils.normalize(inputFolder));
//		final String file_filter = regex;
//		final File[] files = folder.listFiles(new FilenameFilter() {
//			@Override
//			public boolean accept(final File dir, final String name) {
//				return name.matches(file_filter);
//			}
//		});
//
//		for (final File file : files) {
//			this.loadConfigurationFile(file, inputFolder, workFolder, archiveFolder, errorFolder);
//		}
//	}
//
//	private void loadConfigurationFile(File file, String inputFolder, String workFolder, String archiveFolder,
//			String errorFolder) {
//
//		UUID uuid = UUID.randomUUID();
//
//		boolean moveToWorkFolder = false;
//		boolean moveToArchiveFolder = false;
//		boolean moveToErrorFolder = false;
//
//		if (!workFolder.trim().equals(""))
//			moveToWorkFolder = true;
//
//		if (moveToWorkFolder) {
//			FileTools.copyFromFileToFile(FilenameUtils.normalize(inputFolder + File.separator + file.getName()),
//					FilenameUtils.normalize(workFolder + File.separator + file.getName()));
//			FileTools.delete((FilenameUtils.normalize(inputFolder + File.separator + file.getName())));
//		} else {
//			workFolder = inputFolder;
//		}
//
//		if (!archiveFolder.trim().equals(""))
//			moveToArchiveFolder = true;
//		if (!errorFolder.trim().equals(""))
//			moveToErrorFolder = true;
//
//		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
//		SimpleDateFormat timeFormat = new SimpleDateFormat("HHmmss");
//
//		File workFile = new File(FilenameUtils.normalize(workFolder + File.separator + file.getName()));
//		if (!workFile.isDirectory()) {
//			try {
//				this.getFrameworkExecution().getFrameworkLog().log("data.file=" + file.getName(), Level.INFO);
//
//				ObjectMapper objectMapper = new ObjectMapper();
//				SqliteDatabaseConnection dc = objectMapper.convertValue(
//						this.getDataRepositoryConfiguration().getDatabaseConnection(), SqliteDatabaseConnection.class);
//				MetadataRepositoryConfigurationBack metadataRepositoryConfiguration = new MetadataRepositoryConfigurationBack(
//						this.getFrameworkExecution().getFrameworkConfiguration(),
//						this.getFrameworkExecution().getFrameworkControl(), dc);
//
//				DataObjectOperation dataObjectOperation = new DataObjectOperation(this.getFrameworkExecution(),
//						workFile.getAbsolutePath());
//				DataObjectConfiguration dataObjectConfiguration = new DataObjectConfiguration(
//						this.getFrameworkExecution(), metadataRepositoryConfiguration,
//						dataObjectOperation.getDataObjects());
//
//				System.out.println(dataObjectConfiguration.getMetadataRepositoryDdl());
//				if (this.isGenerateDdl()) {
//					this.saveMetadataRepositoryDDL(dataObjectConfiguration.getMetadataRepositoryDdl());
//				} else {
//					InputStream inputStream = FileTools.convertToInputStream(
//							dataObjectConfiguration.getMetadataRepositoryDdl(),
//							this.getFrameworkExecution().getFrameworkControl());
//					this.getDataRepositoryConfiguration().executeScript(inputStream);
//					//this.getDataRepositoryConfiguration().executeQuery(dataObjectConfiguration.getMetadataRepositoryDdl());
//				}
//
//				// Move file to archive folder
//				if (moveToArchiveFolder) {
//					String archiveFileName = dateFormat.format(new Date()) + "-" + timeFormat.format(new Date()) + "-"
//							+ uuid + "-" + workFile.getName();
//					FileTools.copyFromFileToFile(workFile.getAbsolutePath(),
//							FilenameUtils.normalize(archiveFolder + File.separator + archiveFileName));
//					FileTools.delete(workFile.getAbsolutePath());
//				}
//
//			} catch (Exception e) {
//
//				// Move file to error folder
//				if (moveToErrorFolder) {
//					String errorFileName = dateFormat.format(new Date()) + "-" + timeFormat.format(new Date()) + "-"
//							+ uuid + "-" + file.getName();
//					FileTools.copyFromFileToFile(workFile.getAbsolutePath(),
//							FilenameUtils.normalize(errorFolder + File.separator + errorFileName));
//					FileTools.delete(workFile.getAbsolutePath());
//				}
//
//			}
//		}
//
//	}
//
//	private void saveMetadataRepositoryDDL(String ddl) {
//		StringBuilder targetFilePath = new StringBuilder();
//		targetFilePath.append(this.getFrameworkExecution().getFrameworkConfiguration().getFolderConfiguration()
//				.getFolderAbsolutePath("data.out.ddl"));
//		targetFilePath.append(File.separator);
//		targetFilePath.append("name");
//		targetFilePath.append("_");
//		targetFilePath.append("category");
//		targetFilePath.append("_");
//		targetFilePath.append("create");
//		targetFilePath.append(".ddl");
//		FileTools.delete(targetFilePath.toString());
//		FileTools.appendToFile(targetFilePath.toString(), "", ddl);
//	}
//
//	// Getters and setters
//	public FrameworkExecution getFrameworkExecution() {
//		return frameworkExecution;
//	}
//
//	public void setFrameworkExecution(FrameworkExecution frameworkExecution) {
//		this.frameworkExecution = frameworkExecution;
//	}
//
//	public String getAction() {
//		return action;
//	}
//
//	public void setAction(String action) {
//		this.action = action;
//	}
//
//	public boolean isGenerateDdl() {
//		return generateDdl;
//	}
//
//	public void setGenerateDdl(boolean generateDdl) {
//		this.generateDdl = generateDdl;
//	}
//
//	public DataRepositoryConfiguration getDataRepositoryConfiguration() {
//		return dataRepositoryConfiguration;
//	}
//
//	public void setDataRepositoryConfiguration(DataRepositoryConfiguration dataRepositoryConfiguration) {
//		this.dataRepositoryConfiguration = dataRepositoryConfiguration;
//	}
//}