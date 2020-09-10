package io.metadew.iesi.script.execution.instruction.data.oltb.olua;

public class OltbHeader {

    // OLTB Header
    private final String oltbHeaderCode;
    private final String oltbVersion;
    private final String oltbLacNumber;
    private final String oltbLacIndicator;
    private final String oltbSequenceNumber;
    private final String oltbGeneralTotal;


    public OltbHeader(String oltbHeaderCode, String oltbVersion, String oltbLacNumber, String oltbLacIndicator, String oltbSequenceNumber, String oltbGeneralTotal) {
        this.oltbHeaderCode = oltbHeaderCode;
        this.oltbVersion = oltbVersion;
        this.oltbLacNumber = oltbLacNumber;
        this.oltbLacIndicator = oltbLacIndicator;
        this.oltbSequenceNumber = oltbSequenceNumber;
        this.oltbGeneralTotal = oltbGeneralTotal;
    }

}
