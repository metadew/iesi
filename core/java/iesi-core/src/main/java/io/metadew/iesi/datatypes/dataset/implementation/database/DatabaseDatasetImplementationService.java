package io.metadew.iesi.datatypes.dataset.implementation.database;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.metadew.iesi.SpringContext;
import io.metadew.iesi.datatypes.DataType;
import io.metadew.iesi.datatypes.DataTypeHandler;
import io.metadew.iesi.datatypes.IDataTypeService;
import io.metadew.iesi.datatypes.array.Array;
import io.metadew.iesi.datatypes.dataset.Dataset;
import io.metadew.iesi.datatypes.dataset.DatasetConfiguration;
import io.metadew.iesi.datatypes.dataset.DatasetKey;
import io.metadew.iesi.datatypes.dataset.implementation.DatasetImplementation;
import io.metadew.iesi.datatypes.dataset.implementation.DatasetImplementationConfiguration;
import io.metadew.iesi.datatypes.dataset.implementation.DatasetImplementationKey;
import io.metadew.iesi.datatypes.dataset.implementation.DatasetImplementationService;
import io.metadew.iesi.datatypes.dataset.implementation.label.DatasetImplementationLabel;
import io.metadew.iesi.datatypes.dataset.implementation.label.DatasetImplementationLabelKey;
import io.metadew.iesi.datatypes.text.Text;
import io.metadew.iesi.metadata.configuration.security.SecurityGroupConfiguration;
import io.metadew.iesi.metadata.definition.security.SecurityGroup;
import io.metadew.iesi.script.execution.ExecutionRuntime;
import lombok.extern.log4j.Log4j2;

import java.text.MessageFormat;
import java.util.*;
import java.util.stream.Collectors;

@Log4j2
public class DatabaseDatasetImplementationService extends DatasetImplementationService<DatabaseDatasetImplementation> implements IDatabaseDatasetImplementationService, IDataTypeService<DatabaseDatasetImplementation> {

    private static DatabaseDatasetImplementationService instance;

    public static synchronized DatabaseDatasetImplementationService getInstance() {
        if (instance == null) {
            instance = new DatabaseDatasetImplementationService();
        }
        return instance;
    }

    private DatabaseDatasetImplementationService() {
    }

    @Override
    public Optional<DatabaseDatasetImplementation> getDatasetImplementation(String name, List<String> labels) {
        return SpringContext.getBean(DatasetImplementationConfiguration.class).getByNameAndLabels(name, labels)
                .map(DatabaseDatasetImplementation.class::cast);
    }

    @Override
    public Optional<DatabaseDatasetImplementation> getDatasetImplementation(DatasetKey datasetKey, List<String> labels) {
        return SpringContext.getBean(DatasetImplementationConfiguration.class).getByDatasetIdAndLabels(datasetKey, labels)
                .map(DatabaseDatasetImplementation.class::cast);
    }

    @Override
    public DatabaseDatasetImplementation createNewDatasetImplementation(Dataset dataset, List<String> labels, ExecutionRuntime executionRuntime) {
        DatasetImplementationKey datasetImplementationKey = new DatasetImplementationKey();
        DatabaseDatasetImplementation databaseDatasetImplementation = new DatabaseDatasetImplementation(
                datasetImplementationKey,
                dataset.getMetadataKey(),
                dataset.getName(),
                labels.stream()
                        .map(s -> new DatasetImplementationLabel(new DatasetImplementationLabelKey(), datasetImplementationKey, s))
                        .collect(Collectors.toSet()),
                new HashSet<>()
        );
        SpringContext.getBean(DatasetImplementationConfiguration.class).insert(databaseDatasetImplementation);
        return databaseDatasetImplementation;
    }

    @Override
    public DatabaseDatasetImplementation createNewDatasetImplementation(DatasetKey datasetKey, String name, List<String> labels, ExecutionRuntime executionRuntime) {
        DatasetImplementationKey datasetImplementationKey = new DatasetImplementationKey();
        DatabaseDatasetImplementation databaseDatasetImplementation = new DatabaseDatasetImplementation(
                datasetImplementationKey,
                datasetKey,
                name,
                labels.stream()
                        .map(s -> new DatasetImplementationLabel(new DatasetImplementationLabelKey(), datasetImplementationKey, s))
                        .collect(Collectors.toSet()),
                new HashSet<>()
        );
        SpringContext.getBean(DatasetImplementationConfiguration.class).insert(databaseDatasetImplementation);
        return databaseDatasetImplementation;
    }

    @Override
    public DatabaseDatasetImplementation createNewDatasetImplementation(String name, List<String> labels, ExecutionRuntime executionRuntime) {
        SecurityGroup securityGroup = SpringContext.getBean(SecurityGroupConfiguration.class).getByName("PUBLIC")
                .orElseThrow(() -> new RuntimeException("As the dataset doesn't exist, tried to create new one with the security group PUBLIC, but the group doesn't exist"));
        Dataset dataset = SpringContext.getBean(DatasetConfiguration.class).getByName(name)
                .orElseGet(() -> {
                    Dataset newDataset = new Dataset(new DatasetKey(), securityGroup.getMetadataKey(), securityGroup.getName(), name, new HashSet<>());
                    SpringContext.getBean(DatasetConfiguration.class).insert(newDataset);
                    return newDataset;
                });

        DatasetImplementationKey datasetImplementationKey = new DatasetImplementationKey();
        DatabaseDatasetImplementation databaseDatasetImplementation = new DatabaseDatasetImplementation(
                datasetImplementationKey,
                dataset.getMetadataKey(),
                dataset.getName(),
                labels.stream()
                        .map(s -> new DatasetImplementationLabel(new DatasetImplementationLabelKey(), datasetImplementationKey, s))
                        .collect(Collectors.toSet()),
                new HashSet<>()
        );
        SpringContext.getBean(DatasetImplementationConfiguration.class).insert(databaseDatasetImplementation);
        return databaseDatasetImplementation;
    }

    @Override
    public void clean(DatabaseDatasetImplementation datasetImplementation, ExecutionRuntime executionRuntime) {
        DatabaseDatasetImplementationService.getInstance().getDataItems(datasetImplementation, executionRuntime)
                .forEach((s, dataType) -> deleteDataType(dataType, executionRuntime));
        datasetImplementation.setKeyValues(new HashSet<>());
        SpringContext.getBean(DatasetImplementationConfiguration.class).update(datasetImplementation);
    }

    @Override
    public void delete(DatabaseDatasetImplementation datasetImplementation, ExecutionRuntime executionRuntime) {
        DatabaseDatasetImplementationService.getInstance().getDataItems(datasetImplementation, executionRuntime)
                .forEach((s, dataType) -> deleteDataType(dataType, executionRuntime));
        SpringContext.getBean(DatasetImplementationConfiguration.class).delete(datasetImplementation.getMetadataKey());
    }

    private void deleteDataType(DataType dataType, ExecutionRuntime executionRuntime) {
        if (dataType instanceof DatabaseDatasetImplementation) {
            delete((DatabaseDatasetImplementation) dataType, executionRuntime);
        } else if (dataType instanceof Array) {
            ((Array) dataType).getList().forEach(element -> deleteDataType(element, executionRuntime));
        }
    }

    @Override
    public Optional<DataType> getDataItem(DatabaseDatasetImplementation datasetImplementation, String dataItem, ExecutionRuntime executionRuntime) {
        return SpringContext.getBean(DatabaseDatasetImplementationKeyValueConfiguration.class)
                .getByDatasetImplementationIdAndKey(datasetImplementation.getMetadataKey(), dataItem)
                .map(inMemoryDatasetImplementationKeyValue -> SpringContext.getBean(DataTypeHandler.class).resolve(inMemoryDatasetImplementationKeyValue.getValue(), executionRuntime));
    }

    @Override
    public Map<String, DataType> getDataItems(DatabaseDatasetImplementation datasetImplementation, ExecutionRuntime executionRuntime) {
        // https://bugs.openjdk.java.net/browse/JDK-8148463
        return SpringContext.getBean(DatabaseDatasetImplementationKeyValueConfiguration.class)
                .getByDatasetImplementationId(datasetImplementation.getMetadataKey())
                .stream()
                .collect(Collectors.toMap(
                        DatabaseDatasetImplementationKeyValue::getKey,
                        inMemoryDatasetImplementationKeyValue -> SpringContext.getBean(DataTypeHandler.class)
                                .resolve(inMemoryDatasetImplementationKeyValue.getValue(), executionRuntime))
                );
    }

    @Override
    public void setDataItem(DatabaseDatasetImplementation datasetImplementation, String key, DataType value) {
        Optional<DatabaseDatasetImplementationKeyValue> inMemoryDatasetImplementationKeyValue = SpringContext.getBean(DatabaseDatasetImplementationKeyValueConfiguration.class)
                .getByDatasetImplementationIdAndKey(datasetImplementation.getMetadataKey(), key);
        if (inMemoryDatasetImplementationKeyValue.isPresent()) {
            inMemoryDatasetImplementationKeyValue.get().setValue(value.toString());
            SpringContext.getBean(DatabaseDatasetImplementationKeyValueConfiguration.class).update(inMemoryDatasetImplementationKeyValue.get());
        } else {
            DatabaseDatasetImplementationKeyValue newDatasetImplementationKeyValue = new DatabaseDatasetImplementationKeyValue(
                    new DatabaseDatasetImplementationKeyValueKey(UUID.randomUUID()),
                    datasetImplementation.getMetadataKey(),
                    key,
                    value.toString()
            );
            datasetImplementation.getKeyValues().add(newDatasetImplementationKeyValue);
            SpringContext.getBean(DatabaseDatasetImplementationKeyValueConfiguration.class)
                    .insert(newDatasetImplementationKeyValue);
        }
    }

    @Override
    public boolean exists(DatasetImplementationKey datasetImplementationKey) {
        return SpringContext.getBean(DatasetImplementationConfiguration.class)
                .exists(datasetImplementationKey);
    }

    @Override
    public boolean exists(String name, List<String> labels) {
        return SpringContext.getBean(DatasetImplementationConfiguration.class)
                .exists(name, labels);
    }

    @Override
    public Optional<DatasetImplementation> get(DatasetImplementationKey datasetImplementationKey) {
        return SpringContext.getBean(DatasetImplementationConfiguration.class)
                .get(datasetImplementationKey);
    }

    @Override
    public void create(DatabaseDatasetImplementation datasetImplementation) {
        SpringContext.getBean(DatasetImplementationConfiguration.class).insert(datasetImplementation);
    }

    @Override
    public void delete(DatabaseDatasetImplementation datasetImplementation) {
        delete(datasetImplementation.getMetadataKey());
    }

    @Override
    public void delete(DatasetImplementationKey datasetImplementationKey) {
        SpringContext.getBean(DatasetImplementationConfiguration.class).delete(datasetImplementationKey);
    }

    @Override
    public void deleteByDatasetId(DatasetKey datasetKey) {
        SpringContext.getBean(DatasetImplementationConfiguration.class).deleteByDatasetId(datasetKey);
    }

    @Override
    public void update(DatabaseDatasetImplementation datasetImplementation) {
        SpringContext.getBean(DatasetImplementationConfiguration.class).update(datasetImplementation);
    }

    @Override
    public List<DatasetImplementation> getAll() {
        return SpringContext.getBean(DatasetImplementationConfiguration.class)
                .getAll();
    }

    @Override
    public List<DatasetImplementation> getByDatasetId(DatasetKey datasetKey) {
        return SpringContext.getBean(DatasetImplementationConfiguration.class)
                .getByDatasetId(datasetKey);
    }

    @Override
    public boolean isEmpty(DatabaseDatasetImplementation datasetImplementation) {
        return SpringContext.getBean(DatasetImplementationConfiguration.class).isEmpty(datasetImplementation.getMetadataKey());
    }

    public DataType resolve(DatabaseDatasetImplementation dataset, String key, ObjectNode jsonNode, ExecutionRuntime executionRuntime) {
        Iterator<Map.Entry<String, JsonNode>> fields = jsonNode.fields();
        DatabaseDatasetImplementation databaseDatasetImplementation = getObjectDataset(dataset, key, executionRuntime);
        while (fields.hasNext()) {
            Map.Entry<String, JsonNode> field = fields.next();
            DataType object = SpringContext.getBean(DataTypeHandler.class).resolve(databaseDatasetImplementation, field.getKey(), field.getValue(), executionRuntime);
            setDataItem(databaseDatasetImplementation, field.getKey(), object);
        }
        return databaseDatasetImplementation;
    }

    private DatabaseDatasetImplementation getObjectDataset(DatabaseDatasetImplementation databaseDatasetImplementation, String keyPrefix, ExecutionRuntime executionRuntime) {
        if (keyPrefix != null) {
            List<String> labels = databaseDatasetImplementation.getDatasetImplementationLabels().stream()
                    .map(DatasetImplementationLabel::getValue)
                    .collect(Collectors.toList());
            labels.add(UUID.randomUUID().toString());
            return createNewDatasetImplementation(databaseDatasetImplementation.getDatasetKey(), databaseDatasetImplementation.getName(), labels, executionRuntime);
        } else {
            return databaseDatasetImplementation;
        }
    }

    @Override
    public Class<DatabaseDatasetImplementation> appliesTo() {
        return DatabaseDatasetImplementation.class;
    }

    @Override
    public String keyword() {
        return "database";
    }

    @Override
    public DatabaseDatasetImplementation resolve(String arguments, ExecutionRuntime executionRuntime) {
        log.trace(MessageFormat.format("resolving {0} for Dataset Implementation", arguments));
        List<String> splittedArguments = SpringContext.getBean(DataTypeHandler.class).splitInstructionArguments(arguments);
        if (splittedArguments.size() == 3) {
            List<DataType> resolvedArguments = splittedArguments.stream()
                    .map(argument -> SpringContext.getBean(DataTypeHandler.class).resolve(argument, executionRuntime))
                    .collect(Collectors.toList());
            return getDatasetImplementation(
                    convertDatasetName(resolvedArguments.get(0)),
                    convertDatasetLabels(resolvedArguments.get(1), executionRuntime)
            )
                    .orElseGet(() -> {
                        DatasetKey datasetKey = SpringContext.getBean(DatasetConfiguration.class)
                            .getIdByName(convertDatasetName(resolvedArguments.get(0)))
                            .orElseThrow(() -> new RuntimeException(String.format("Cannot find dataset %s", convertDatasetName(resolvedArguments.get(0)))));
                        return createNewDatasetImplementation(
                                datasetKey,
                                convertDatasetName(resolvedArguments.get(0)),
                                convertDatasetLabels(resolvedArguments.get(1), executionRuntime), executionRuntime);
                    });
        } else {
            throw new RuntimeException(MessageFormat.format("Cannot create dataset with arguments ''{0}''", splittedArguments.toString()));
        }
    }

    private String convertDatasetName(DataType datasetName) {
        if (datasetName instanceof Text) {
            return ((Text) datasetName).getString();
        } else {
            log.warn(MessageFormat.format("dataset does not accept {0} as type for datasetDatabase name",
                    datasetName.getClass()));
            return datasetName.toString();
        }
    }


    private List<String> convertDatasetLabels(DataType datasetLabels, ExecutionRuntime executionRuntime) {
        List<String> labels = new ArrayList<>();
        if (datasetLabels instanceof Text) {
            Arrays.stream(datasetLabels.toString().split(","))
                    .forEach(datasetLabel -> labels.add(convertDatasetLabel(SpringContext.getBean(DataTypeHandler.class).resolve(datasetLabel.trim(), executionRuntime), executionRuntime)));
            return labels;
        } else if (datasetLabels instanceof Array) {
            ((Array) datasetLabels).getList()
                    .forEach(datasetLabel -> labels.add(convertDatasetLabel(datasetLabel, executionRuntime)));
            return labels;
        } else {
            log.warn(MessageFormat.format("dataset does not accept {0} as type for datasetDatabase labels",
                    datasetLabels.getClass()));
            return labels;
        }
    }


    private String convertDatasetLabel(DataType datasetLabel, ExecutionRuntime executionRuntime) {
        if (datasetLabel instanceof Text) {
            return executionRuntime.resolveVariables(((Text) datasetLabel).getString());
        } else {
            log.warn(MessageFormat.format("dataset does not accept {0} as type for a datasetDatabase label",
                    datasetLabel.getClass()));
            return executionRuntime.resolveVariables(datasetLabel.toString());
        }
    }

    @Override
    public boolean equals(DatabaseDatasetImplementation _this, DatabaseDatasetImplementation other, ExecutionRuntime executionRuntime) {
        if (_this == null && other == null) {
            return true;
        }
        if (_this == null || other == null) {
            return false;
        }
        if (!_this.getClass().equals(other.getClass())) {
            return false;
        }
        Map<String, DataType> thisDataItems = getDataItems(_this, executionRuntime);
        Map<String, DataType> otherDataItems = getDataItems(other, executionRuntime);
        if (!thisDataItems.keySet().equals(otherDataItems.keySet())) {
            return false;
        }
        for (Map.Entry<String, DataType> thisDataItem : thisDataItems.entrySet()) {
            if (!getDataItem(other, thisDataItem.getKey(), executionRuntime)
                    .map(dataType -> SpringContext.getBean(DataTypeHandler.class).equals(dataType, thisDataItem.getValue(), executionRuntime))
                    .orElse(false)) {
                return false;
            }
        }
        return true;
    }

}
