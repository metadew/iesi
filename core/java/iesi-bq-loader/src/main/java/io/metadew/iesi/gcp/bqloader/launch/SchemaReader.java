package io.metadew.iesi.gcp.bqloader.launch;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.metadew.iesi.gcp.bqloader.bigquery.Field;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class SchemaReader {
    public static void main( String[] args ) {
        List<Field> fields = new ArrayList<>();
        try {
            File file = new File("schema.json");
            ObjectMapper objectMapper = new ObjectMapper();
                fields = objectMapper.readValue(file, new TypeReference<List<Field>>() {});
        } catch (IOException e) {
            e.printStackTrace();
        }
        for (Field field : fields) {
            System.out.println(field.getName());
        }

    }
}
