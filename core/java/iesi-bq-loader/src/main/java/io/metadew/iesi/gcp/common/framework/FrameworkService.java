package io.metadew.iesi.gcp.common.framework;

import io.metadew.iesi.gcp.common.tools.FolderTools;

import java.io.File;
import java.util.Map;

public class FrameworkService {

    private static FrameworkService INSTANCE;

    public synchronized static FrameworkService getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new FrameworkService();
        }
        return INSTANCE;
    }

    private FrameworkService () {

    }

    public void clean(String serviceName) {
        FolderTools.deleteFolder(serviceName,true);
    }

    public void create(String serviceName) {
        for (Map.Entry<String, FrameworkFolder> entry : FrameworkConfiguration.getInstance().getFrameworkFolders().entrySet()) {
            FolderTools.createFolder(serviceName + File.separator + entry.getValue().getPath());
        }
    }
}
