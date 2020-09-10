package io.metadew.iesi.script.execution.instruction.data.oltb.olua;

import io.metadew.iesi.script.execution.instruction.data.DataInstruction;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class OltbOlauRequest implements DataInstruction {

    // Transport Header
    private final static String transportHeaderCode = "TR";
    private final static String transportVersion = "00";
    private final static String transportRetryCount = "00";
    private final static String transportRejectCode = "    ";
    private final static String transportServiceCode = "OLAU";
    private final static String transportOriginIndentity = "59666";

    // OLTB Header
    private final static String oltbHeaderCode = "OB";
    private final static String oltbVersion = "00";


    @Override
    public String getKeyword() {
        return "oltb.olau.request";
    }

    @Override
    public String generateOutput(String parameters) {
        List<String> splittedParameters = Arrays.stream(parameters.split(",")).collect(Collectors.toList());
        if (splittedParameters.size() != 27) {
            throw new RuntimeException();
        }

        String transportHeader = transportHeaderCode + transportVersion + transportRetryCount + transportRejectCode +
                transportServiceCode + transportOriginIndentity + splittedParameters.get(0) + "";



        return null;
    }
}