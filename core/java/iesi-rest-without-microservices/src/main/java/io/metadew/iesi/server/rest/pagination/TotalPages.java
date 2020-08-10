package io.metadew.iesi.server.rest.pagination;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.hateoas.RepresentationModel;

@Getter
@Setter
@Builder
public class TotalPages<T extends Object> extends RepresentationModel {

    private int totalPages;
    private T payload;
}