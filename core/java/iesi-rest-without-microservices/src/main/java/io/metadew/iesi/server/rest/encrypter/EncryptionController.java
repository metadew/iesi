package io.metadew.iesi.server.rest.encrypter;

import io.metadew.iesi.server.rest.encrypter.dto.EncryptDto;
import io.metadew.iesi.server.rest.encrypter.dto.EncryptPostDto;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

/**
 * The encryption controller is used to provide encrypted text.
 * @author Suyash.d.jain
 */

@RestController
@Tag(name = "encrypt", description = "controller to encrypt text")
@RequestMapping("/encryption")
@ConditionalOnWebApplication
public class EncryptionController {

    private final IEncryptionService encryptionService;

    public EncryptionController(IEncryptionService encryptionService) {
        this.encryptionService = encryptionService;
    }

    @PostMapping("/encrypt")
    public EncryptDto getEncryptedPassword(@Valid @RequestBody EncryptPostDto encryptPostDto){
        return new EncryptDto(encryptionService.getEncryptedPassword(encryptPostDto.getText()));
    }
}
