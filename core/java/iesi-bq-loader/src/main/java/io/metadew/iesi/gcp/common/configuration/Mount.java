package io.metadew.iesi.gcp.common.configuration;

public class Mount {
    private static Mount INSTANCE;

    public synchronized static Mount getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new Mount();
        }
        return INSTANCE;
    }

    private Mount() {

    }

    public String getProjectName(String cliProject) {
        String whichProject = "";
        if (cliProject != null && !cliProject.equalsIgnoreCase("")) {
            whichProject = cliProject;
        } else if (Configuration.getInstance().getProperty("iesi.gcp.project").isPresent()) {
            whichProject = Configuration.getInstance().getProperty("iesi.gcp.project").get().toString();
        } else if (Spec.getInstance().getSpec("project").isPresent()) {
            whichProject = Spec.getInstance().getSpec("project").get().toString();
        }

        //Ensure that a project is available
        if (whichProject.equalsIgnoreCase("")) {
            throw new RuntimeException("No project provided");
        }

        return whichProject;
    }

}
