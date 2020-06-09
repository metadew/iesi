package io.metadew.iesi.metadata.definition.user;

import io.metadew.iesi.metadata.definition.Metadata;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class Authority extends Metadata<AuthorityKey> {

    private String authority;

    public Authority(AuthorityKey authorityKey, String authority) {
        super(authorityKey);
        this.authority = authority;
    }

}