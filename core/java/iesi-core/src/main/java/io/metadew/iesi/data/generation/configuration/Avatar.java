package io.metadew.iesi.data.generation.configuration;

import io.metadew.iesi.data.generation.execution.GenerationComponentExecution;
import io.metadew.iesi.data.generation.execution.GenerationDataExecution;

import java.util.Arrays;
import java.util.List;

public class Avatar extends GenerationComponentExecution {

    public static final List<String> SUPPORTED_FORMATS = Arrays.asList("png", "jpg", "bmp");

    private static final String AVATAR_URL = "https://robohash.org/";

    public Avatar(GenerationDataExecution execution) {
        super(execution);
    }

    public String image() {
        return image(null);
    }

    public String image(String slug) {
        return image(slug, "300x300");
    }

    public String image(String slug, String size) {
        return image(slug, size, "png");
    }

    public String image(String slug, String size, String format) {
        return image(slug, size, format, "set1");
    }

    public String image(String slug, String size, String format, String set) {
        return image(slug, size, format, set, null);
    }

    public String image(String slug, String size, String format, String set, String bgset) {
        if (!size.matches("^[0-9]+x[0-9]+$")) {
            throw new IllegalArgumentException("Size should be specified in format 300x300");
        }

        if (!SUPPORTED_FORMATS.contains(format)) {
            throw new IllegalArgumentException("Supported formats are "
                    + this.getGenerationTools().getStringTools().join(SUPPORTED_FORMATS, ","));
        }

        if (slug == null) {
            slug = "image";
        }

        String bgset_query = (bgset != null ? "&bgset=" + bgset : "");

        return AVATAR_URL + slug + "." + format + "?size=" + size + "&set=" + set + bgset_query;
    }
}
