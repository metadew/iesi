package io.metadew.iesi.gcp.services.bqloader.common;

import io.metadew.iesi.gcp.common.configuration.Spec;
import io.metadew.iesi.gcp.spec.bql.BqlSpec;
import lombok.Getter;

@Getter
public class BqlService {
    private static BqlService INSTANCE;
    private BqlSpec bqlSpec;
    private String projectName;

    public synchronized static BqlService getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new BqlService();
        }
        return INSTANCE;
    }

    private BqlService () {

    }
    public void init (String projectName, String bqlName) {
        this.projectName = projectName;
        bqlSpec = null;
        for (BqlSpec entry : Spec.getInstance().getGcpSpec().getBql()) {
            if (entry.getName().equalsIgnoreCase(bqlName)) {
                bqlSpec = entry;
            }
        }
    }

}
