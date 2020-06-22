package io.metadew.iesi.server.rest.impersonation;

import io.metadew.iesi.metadata.configuration.exception.MetadataDoesNotExistException;
import io.metadew.iesi.metadata.configuration.impersonation.ImpersonationConfiguration;
import io.metadew.iesi.metadata.definition.impersonation.Impersonation;
import io.metadew.iesi.metadata.definition.impersonation.key.ImpersonationKey;
import io.metadew.iesi.server.rest.impersonation.dto.ImpersonationDto;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ImpersonationService implements IImpersonationService {

    private ImpersonationConfiguration impersonationConfiguration;

    private ImpersonationService(ImpersonationConfiguration impersonationConfiguration) {
        this.impersonationConfiguration = impersonationConfiguration;
    }

    public List<Impersonation> getAll() {
        return impersonationConfiguration.getAllImpersonations();
    }

    public Impersonation getByName(String name) {
        return impersonationConfiguration.getImpersonation(name)
                .orElseThrow(() -> new MetadataDoesNotExistException(new ImpersonationKey(name)));
    }

    public void createImpersonation(ImpersonationDto impersonationDto) {
        impersonationConfiguration.insertImpersonation(impersonationDto.convertToEntity());
    }

    public void updateImpersonation(ImpersonationDto impersonationDto) {
        impersonationConfiguration.updateImpersonation(impersonationDto.convertToEntity());
    }

    public void updateImpersonations(List<ImpersonationDto> impersonationDtos) {
        impersonationDtos.forEach(this::updateImpersonation);
    }

    public void deleteAll() {
        impersonationConfiguration.deleteAllImpersonations();
    }

    public void deleteByName(String name) {
        impersonationConfiguration.deleteImpersonation(new ImpersonationKey(name));
    }

}
