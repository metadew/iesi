package io.metadew.iesi.script.execution.instruction.data.oltb.olua;

public class OltbHeader {

    private final String transportHeaderCode;
    private final String transportVersion;
    private final String transportRetryCount;
    private final String transportRejectCode;
    private final String transportServiceCode;
    private final String transportOriginIndentity;
    private final String transportDestinyIndentity;
    private final String messageLengthDestinyIndentity;
    private final String messageNumberDestinyIndentity;


    private OltbHeader(String transportHeaderCode, String transportVersion, String transportRetryCount, String transportRejectCode, String transportServiceCode, String transportOriginIndentity, String transportDestinyIndentity, String messageLengthDestinyIndentity, String messageNumberDestinyIndentity) {
        this.transportHeaderCode = transportHeaderCode;
        this.transportVersion = transportVersion;
        this.transportRetryCount = transportRetryCount;
        this.transportRejectCode = transportRejectCode;

        this.transportServiceCode = transportServiceCode;
        this.transportOriginIndentity = transportOriginIndentity;
        this.transportDestinyIndentity = transportDestinyIndentity;
        this.messageLengthDestinyIndentity = messageLengthDestinyIndentity;
        this.messageNumberDestinyIndentity = messageNumberDestinyIndentity;
    }
}
