package io.metadew.iesi.server.rest.pagination;

import io.metadew.iesi.common.configuration.Configuration;
import org.springframework.data.domain.Pageable;

public class LimitAndOffset {

    public static String limitAndOffset(Pageable pageable) {
        String getOracleDb = Configuration.getInstance().getMandatoryProperty("iesi.metadata.repository.coordinator.type").toString();
        if (getOracleDb.equals("oracle")) {
            return pageable == null || pageable.isUnpaged() ? " " : " OFFSET " + pageable.getOffset() + " ROWS FETCH NEXT " + pageable.getPageSize() + " ROWS ONLY ";
        }
        return pageable == null || pageable.isUnpaged() ? " " : " limit " + pageable.getPageSize() + " offset " + pageable.getOffset() + " ";
    }
}