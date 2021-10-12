package io.metadew.iesi.server.rest.script.audit;

import io.metadew.iesi.metadata.definition.audit.ScriptDesignAudit;
import io.metadew.iesi.metadata.definition.audit.ScriptDesignAuditAction;
import io.metadew.iesi.metadata.definition.audit.key.ScriptDesignAuditKey;
import io.metadew.iesi.metadata.definition.script.Script;
import io.metadew.iesi.server.rest.user.UserDto;
import io.metadew.iesi.server.rest.user.UserDtoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@ConditionalOnWebApplication
public class ScriptDesignAuditService implements IScriptDesignAuditService {

    private final UserDtoRepository userDtoRepository;

    @Autowired
    public ScriptDesignAuditService(UserDtoRepository userDtoRepository) {
        this.userDtoRepository = userDtoRepository;
    }

    @Override
    public ScriptDesignAudit convertToScriptAudit(Script script, ScriptDesignAuditAction scriptDesignAuditAction) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        UserDto userDto = userDtoRepository.get(username)
                .orElseThrow(() -> new RuntimeException("Cannot find user :" + username));
        return new ScriptDesignAudit(new ScriptDesignAuditKey(UUID.randomUUID()),
                username,
                userDto.getId().toString(),
                scriptDesignAuditAction,
                script.getMetadataKey().getScriptId(),
                script.getName(),
                script.getVersion().getNumber(),
                script.getSecurityGroupName(),
                LocalDateTime.now().toString());
    }
}
