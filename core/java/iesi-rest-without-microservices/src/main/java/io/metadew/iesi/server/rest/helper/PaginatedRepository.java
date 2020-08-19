package io.metadew.iesi.server.rest.helper;

import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.NoRepositoryBean;

@NoRepositoryBean
public abstract class PaginatedRepository {

    public String getLimitAndOffsetClause(Pageable pageable) {
        return pageable.isUnpaged() ? " " : " limit " + pageable.getPageSize() + " offset " + pageable.getOffset() + " ";
    }

}
