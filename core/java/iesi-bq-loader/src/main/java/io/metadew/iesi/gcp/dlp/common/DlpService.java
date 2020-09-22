package io.metadew.iesi.gcp.dlp.common;

import io.metadew.iesi.gcp.common.configuration.Spec;
import io.metadew.iesi.gcp.spec.dlp.DlpSpec;
import lombok.Getter;

@Getter
public class DlpService {
    private static DlpService INSTANCE;
    private DlpSpec dlpSpec;
    private String projectName;

    public synchronized static DlpService getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new DlpService();
        }
        return INSTANCE;
    }

    private DlpService () {

    }
    public void init (String projectName, String dlpName) {
        this.projectName = projectName;
        dlpSpec = null;
        for (DlpSpec entry : Spec.getInstance().getGcpSpec().getDlp()) {
            if (entry.getName().equalsIgnoreCase(dlpName)) {
                dlpSpec = entry;
            }
        }
    }

}
