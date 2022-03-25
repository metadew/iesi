package io.metadew.iesi.server.rest.user.team;

import io.metadew.iesi.metadata.definition.user.Team;

public interface ITeamPutDtoService {
    Team convertToEntity(TeamPutDto teamPutDto);
}
