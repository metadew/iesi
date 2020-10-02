package io.metadew.iesi.gcp.services.bigquery.common;

import io.metadew.iesi.gcp.common.configuration.Spec;
import io.metadew.iesi.gcp.spec.bigquery.BigquerySpec;
import lombok.Getter;

@Getter
public class BigqueryService {
    private static BigqueryService INSTANCE;
    private BigquerySpec bigquerySpec;
    private String projectName;

    public synchronized static BigqueryService getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new BigqueryService();
        }
        return INSTANCE;
    }

    private BigqueryService () {

    }
    public void init (String projectName, String bigquerylName) {
        this.projectName = projectName;
        bigquerySpec = null;
        for (BigquerySpec entry : Spec.getInstance().getGcpSpec().getBigquery()) {
            if (entry.getName().equalsIgnoreCase(bigquerylName)) {
                bigquerySpec = entry;
            }
        }
    }

}
