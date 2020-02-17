//package io.metadew.iesi.framework.configuration;
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//import io.metadew.iesi.framework.definition.FrameworkFolder;
//import io.metadew.iesi.metadata.definition.DataObject;
//import io.metadew.iesi.metadata.operation.DataObjectOperation;
//import org.apache.commons.io.FilenameUtils;
//import org.apache.logging.log4j.ThreadContext;
//
//import java.io.File;
//import java.util.HashMap;
//import java.util.Map;
//
//public class FrameworkFolderConfiguration {
//
//    private String solutionHome;
//
//    private Map<String, FrameworkFolder> folderMap;
//
//    private static FrameworkFolderConfiguration INSTANCE;
//
//    public synchronized static FrameworkFolderConfiguration getInstance() {
//        if (INSTANCE == null) {
//            INSTANCE = new FrameworkFolderConfiguration();
//        }
//        return INSTANCE;
//    }
//
//    private FrameworkFolderConfiguration() {}
//
//    public void init(String solutionHome) {
//        this.setSolutionHome(solutionHome);
//        this.folderMap = new HashMap<>();
//
//        String initFilePath = solutionHome + File.separator + "sys" + File.separator + "init" + File.separator + "FrameworkFolders.json";
//        DataObjectOperation dataObjectOperation = new DataObjectOperation(initFilePath);
//        dataObjectOperation.parseFile();
//        ObjectMapper objectMapper = new ObjectMapper();
////        for (DataObject dataObject : dataObjectOperation.getDataObjects()) {
////            if (dataObject.getType().equalsIgnoreCase("frameworkfolder")) {
////                FrameworkFolder frameworkFolder = objectMapper.convertValue(dataObject.getData(), FrameworkFolder.class);
////                String folderPath = solutionHome + File.separator + frameworkFolder.getPath().replace("/", File.separator);
////                frameworkFolder.setAbsolutePath(FilenameUtils.normalize(folderPath));
////                folderMap.put(frameworkFolder.getName(), frameworkFolder);
////            }
////        }
//        ThreadContext.put("location", getFolderAbsolutePath("logs"));
//    }
//
////    private void initalizeValues() {
////        this.folderMap = new HashMap<>();
////
////        String initFilePath = solutionHome + File.separator + "sys" + File.separator + "init" + File.separator +
////                "FrameworkFolders.json";
////        DataObjectOperation dataObjectOperation = new DataObjectOperation(initFilePath);
////        dataObjectOperation.parseFile();
////        ObjectMapper objectMapper = new ObjectMapper();
////        for (DataObject dataObject : dataObjectOperation.getDataObjects()) {
////            if (dataObject.getType().equalsIgnoreCase("frameworkfolder")) {
////                FrameworkFolder frameworkFolder = objectMapper.convertValue(dataObject.getData(), FrameworkFolder.class);
////                String folderPath = solutionHome + File.separator + frameworkFolder.getPath().replace("/", File.separator);
////                frameworkFolder.setAbsolutePath(FilenameUtils.normalize(folderPath));
////                folderMap.put(frameworkFolder.getScriptName(), frameworkFolder);
////            }
////        }
////    }
//
//    // Create Getters and Setters
//    public String getFolderAbsolutePath(String key) {
//        return this.getFolderMap().get(key).getAbsolutePath();
//    }
//
//    public String getFolderPath(String key) {
//        return this.getFolderMap().get(key).getPath();
//    }
//
//    public String getSolutionHome() {
//        return solutionHome;
//    }
//
//    public void setSolutionHome(String solutionHome) {
//        this.solutionHome = solutionHome;
//    }
//
//    public Map<String, FrameworkFolder> getFolderMap() {
//        return folderMap;
//    }
//
//    public void setFolderMap(HashMap<String, FrameworkFolder> folderMap) {
//        this.folderMap = folderMap;
//    }
//
//}