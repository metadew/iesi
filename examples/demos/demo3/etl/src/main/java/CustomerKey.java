import io.metadew.iesi.metadata.definition.key.MetadataKey;

public class CustomerKey extends MetadataKey {

    private static Long ID = 0L;
    private Long id;

    public CustomerKey(Long id) {
        this.id = id;
    }

    public CustomerKey() {
        this.id = ID;
        ID++;
    }

    public Long getId() {
        return id;
    }
}
