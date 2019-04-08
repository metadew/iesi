package io.metadew.iesi.metadata.operation;

import java.io.File;
import java.io.FilenameFilter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

import org.apache.logging.log4j.Level;

import io.metadew.iesi.connection.tools.FileTools;
import io.metadew.iesi.connection.tools.FolderTools;
import io.metadew.iesi.framework.execution.FrameworkExecution;

public class MetadataFileStoreRepositoryImpl {
//
//	private FrameworkExecution frameworkExecution;
//
//	// Constructors
//	public MetadataFileStoreRepositoryImpl(FrameworkExecution frameworkExecution) {
//		this.setFrameworkExecution(frameworkExecution);
//	}
//
//	// Methods
//	public void createStructure() {
//		// Verify path
//		String path = this.getFrameworkExecution().getMetadataControl().getDesignMetadataRepository().getFileStoreConnection().getPath();
//		if (!FolderTools.isFolder(path) ) {
//			this.getFrameworkExecution().getFrameworkLog().log("error:Path value is not a folder", Level.DEBUG);
//			throw new RuntimeException("error:Path value is not a folder");
//		}
//
//		// Get Configuration
//		// final File folder = new File(this.getFrameworkExecution().getFrameworkConfiguration().getFolderConfiguration().getMetadataDef());
//
//		this.loadConfigurationSelection(this.getFrameworkExecution().getFrameworkConfiguration().getFolderConfiguration().getFolderAbsolutePath("metadata.def"), "", "",
//				"",  "MetadataObjects.json");
//
//	}
//
//	 // Load entire folder
//	private void loadConfigurationSelection(String inputFolder, String workFolder, String archiveFolder, String errorFolder, String regex) {
//		// Copy all files to work folder
//		boolean moveToWorkFolder = false;
//
//		if (!workFolder.trim().equals(""))
//			moveToWorkFolder = true;
//
//		if (moveToWorkFolder) {
//			FolderTools.copyFromFolderToFolder(inputFolder, workFolder,false);
//			FolderTools.deleteFolder(inputFolder, false);
//		} else {
//			workFolder = inputFolder;
//		}
//
//		// load configuration
//		UUID uuid = UUID.randomUUID();
//
//		final File folder = new File(workFolder);
//
//		final String file_filter = regex;
//		final File[] files = folder.listFiles(new FilenameFilter() {
//			@Override
//			public boolean accept(final File dir, final String name) {
//				return name.matches(file_filter);
//			}
//		});
//		for (final File file : files) {
//			this.loadConfigurationFile(file, archiveFolder, errorFolder, uuid);
//		}
//	}
//
//	private void loadConfigurationFile(File file, String archiveFolder, String errorFolder, UUID uuid) {
//
//		boolean moveToArchiveFolder = false;
//		boolean moveToErrorFolder = false;
//
//		if (!archiveFolder.trim().equals(""))
//			moveToArchiveFolder = true;
//		if (!errorFolder.trim().equals(""))
//			moveToErrorFolder = true;
//
//		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
//		SimpleDateFormat timeFormat = new SimpleDateFormat("HHmmss");
//
//		if (file.isDirectory()) {
//			// Ignore
//		} else {
//			try {
//				this.getFrameworkExecution().getFrameworkLog().log("file=" + file.getName(),
//						Level.INFO);
//				DataObjectOperation dataObjectOperation = new DataObjectOperation(this.frameworkExecution, file.getAbsolutePath());
//				dataObjectOperation.saveToMetadataFileStore();
//
//				// Move file to archive folder
//				if (moveToArchiveFolder) {
//					String archiveFileName = dateFormat.format(new Date()) + "-" + timeFormat.format(new Date())
//							+ "-" + uuid + "-" + file.getName();
//					FileTools.copyFromFileToFile(file.getAbsolutePath(),
//							archiveFolder + File.separator + archiveFileName);
//					FileTools.delete(file.getAbsolutePath());
//				}
//
//			} catch (Exception e) {
//
//				// Move file to archive folder
//				if (moveToErrorFolder) {
//					String errorFileName = dateFormat.format(new Date()) + "-" + timeFormat.format(new Date()) + "-"
//							+ uuid + "-" + file.getName();
//					FileTools.copyFromFileToFile(file.getAbsolutePath(),
//							errorFolder + File.separator + errorFileName);
//					FileTools.delete(file.getAbsolutePath());
//				}
//
//			}
//		}
//
//	}
//
//
//	public void dropStructure() {
//		FolderTools.deleteFolder(this.getFrameworkExecution().getMetadataControl().getDesignMetadataRepository().getFileStoreConnection().getPath(), false);
//	}
//
//
//	// Getters and setters
//	public FrameworkExecution getFrameworkExecution() {
//		return frameworkExecution;
//	}
//
//	public void setFrameworkExecution(FrameworkExecution frameworkExecution) {
//		this.frameworkExecution = frameworkExecution;
//	}

}