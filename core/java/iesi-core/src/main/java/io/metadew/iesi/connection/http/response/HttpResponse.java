package io.metadew.iesi.connection.http.response;

import lombok.Data;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.ProtocolVersion;
import org.apache.http.StatusLine;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

@Data
public class HttpResponse {

    private final CloseableHttpResponse response;
    private final StatusLine statusLine;
    private final ProtocolVersion protocolVersion;
    private final List<Header> headers;
    private final Locale locale;
    private final HttpEntity httpEntity;
    private final byte[] entityContent;

    public HttpResponse(CloseableHttpResponse response) throws IOException {
        this.response = response;
        this.statusLine = response.getStatusLine();
        this.httpEntity = response.getEntity();
        this.protocolVersion = response.getProtocolVersion();
        this.locale = response.getLocale();
        this.headers = Arrays.asList(response.getAllHeaders());
        if (this.httpEntity != null) {
            this.entityContent = EntityUtils.toByteArray(this.httpEntity);
            EntityUtils.consume(httpEntity);
        } else {
            this.entityContent = null;
        }
    }

    public Optional<byte[]> getEntityContent() {
        return Optional.ofNullable(entityContent);
    }

}