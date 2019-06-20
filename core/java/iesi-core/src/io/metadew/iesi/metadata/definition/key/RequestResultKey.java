package io.metadew.iesi.metadata.definition.key;

public class RequestResultKey extends MetadataKey {

    private String requestId;

    public RequestResultKey(String requestId) {
    	this.setRequestId(requestId);
    }

	public String getRequestId() {
		return requestId;
	}

	public void setRequestId(String requestId) {
		this.requestId = requestId;
	}

}
