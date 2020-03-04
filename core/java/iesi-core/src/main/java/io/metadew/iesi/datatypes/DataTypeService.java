package io.metadew.iesi.datatypes;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.*;
import io.metadew.iesi.datatypes.array.ArrayService;
import io.metadew.iesi.datatypes.dataset.keyvalue.KeyValueDataset;
import io.metadew.iesi.datatypes.dataset.keyvalue.KeyValueDatasetService;
import io.metadew.iesi.datatypes.text.Text;
import io.metadew.iesi.datatypes.text.TextService;
import io.metadew.iesi.script.execution.ExecutionRuntime;
import lombok.extern.log4j.Log4j2;

import java.io.IOException;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public interface DataTypeService<T extends DataType> {

    public Class<T> appliesTo();
    public String keyword();
    public T resolve(String input, ExecutionRuntime executionRuntime);

}
