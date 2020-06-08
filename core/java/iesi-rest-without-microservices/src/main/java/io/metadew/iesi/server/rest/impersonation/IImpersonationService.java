package io.metadew.iesi.server.rest.impersonation;

import io.metadew.iesi.metadata.definition.impersonation.Impersonation;
import io.metadew.iesi.server.rest.impersonation.dto.ImpersonationDto;

import java.util.List;

public interface IImpersonationService {

    public List<Impersonation> getAll();

    public Impersonation getByName(String name);

    public void createImpersonation(ImpersonationDto impersonationDto);

    public void updateImpersonation(ImpersonationDto impersonationDto);

    public void updateImpersonations(List<ImpersonationDto> impersonationDtos);

    public void deleteAll();

    public void deleteByName(String name);

}
