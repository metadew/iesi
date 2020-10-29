package io.metadew.iesi.datatypes._null;

import io.metadew.iesi.datatypes.IDataTypeService;
import io.metadew.iesi.script.execution.ExecutionRuntime;
import lombok.extern.log4j.Log4j2;

import java.text.MessageFormat;

@Log4j2
public class NullService implements IDataTypeService<Null> {

    private static NullService instance;

    public static synchronized NullService getInstance() {
        if (instance == null) {
            instance = new NullService();
        }
        return instance;
    }

    private NullService() {
    }

    @Override
    public Class<Null> appliesTo() {
        return Null.class;
    }

    @Override
    public String keyword() {
        return "null";
    }

    public Null resolve(String arguments, ExecutionRuntime executionRuntime) {
        log.trace(MessageFormat.format("resolving {0} for Null", arguments));
        return new Null();
    }

    @Override
    public boolean equals(Null _this, Null other, ExecutionRuntime executionRuntime) {
        if (_this == null && other == null) {
            return true;
        }
        if (_this == null || other == null) {
            return false;
        }
        return true;
    }

}
