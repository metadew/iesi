package io.metadew.iesi.server.rest.executionrequest.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.hateoas.RepresentationModel;

import java.util.List;

@Getter
@Setter
@Builder
public class TotalPages<T> extends RepresentationModel {

    private int totalPages;
    private List<T> list;
}
