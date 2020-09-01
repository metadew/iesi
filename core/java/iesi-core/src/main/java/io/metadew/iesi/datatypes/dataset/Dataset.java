//package io.metadew.iesi.datatypes.dataset;
//
//import io.metadew.iesi.connection.database.Database;
//import io.metadew.iesi.datatypes.DataType;
//import io.metadew.iesi.datatypes.array.Array;
//import io.metadew.iesi.datatypes.dataset.metadata.DatasetMetadata;
//import io.metadew.iesi.datatypes.text.Text;
//import lombok.AllArgsConstructor;
//import lombok.EqualsAndHashCode;
//import lombok.Getter;
//import lombok.extern.log4j.Log4j2;
//
//import java.util.List;
//import java.util.stream.Collectors;
//
//@Getter
//@Log4j2
//@AllArgsConstructor
//@EqualsAndHashCode
//public abstract class Dataset implements DataType {
//
//    private String name;
//    private List<String> labels;
//    private DatasetMetadata datasetMetadata;
//    private Database datasetDatabase;
//    private String tableName;
//
//    public String toString() {
//        return "{{^dataset(" + getNameAsDataType().toString() + ", " + getLabelsAsDataType().toString() + ")}}";
//    }
//
//    private DataType getNameAsDataType() {
//        return new Text(name);
//    }
//
//    private DataType getLabelsAsDataType() {
//        return new Array(labels.stream().map(Text::new).collect(Collectors.toList()));
//    }
//}
//
//
