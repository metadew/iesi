package io.metadew.iesi.connection.http.request;

import io.metadew.iesi.common.configuration.Configuration;
import io.metadew.iesi.connection.http.ProxyConnection;
import io.metadew.iesi.connection.http.response.HttpResponse;
import lombok.extern.log4j.Log4j2;
import org.apache.http.HttpHost;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustAllStrategy;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.DefaultProxyRoutePlanner;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.ssl.SSLContexts;
import org.springframework.stereotype.Service;

import javax.net.ssl.*;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.*;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.time.LocalDateTime;
import java.util.Enumeration;

@Service
@Log4j2
public class HttpRequestService implements IHttpRequestService {

    private final Configuration configuration;

    public HttpRequestService(Configuration configuration) {
        this.configuration = configuration;
    }

    public HttpResponse send(HttpRequest httpRequest, boolean mutualTLS) throws IOException, KeyManagementException, NoSuchAlgorithmException, UnrecoverableKeyException, KeyStoreException, CertificateException {
        CloseableHttpClient httpClient = noSSLCertificateVerification(mutualTLS);
        return send(httpRequest, httpClient);
    }

    public HttpResponse send(HttpRequest httpRequest, ProxyConnection proxyConnection, boolean mutualTLS) throws IOException, KeyManagementException, NoSuchAlgorithmException, UnrecoverableKeyException, KeyStoreException, CertificateException {
        CloseableHttpClient httpClient = noSSLCertificateVerification(proxyConnection, mutualTLS);
        return send(httpRequest, httpClient);
    }

    private HttpResponse send(HttpRequest httpRequest, CloseableHttpClient httpClient) throws IOException {
        LocalDateTime startTimestamp = LocalDateTime.now();
        CloseableHttpResponse closeableHttpResponse = httpClient.execute(httpRequest.getHttpRequest());
        LocalDateTime endTimestamp = LocalDateTime.now();
        HttpResponse httpResponse = new HttpResponse(closeableHttpResponse, startTimestamp, endTimestamp);
        httpClient.close();
        return httpResponse;
    }

    private CloseableHttpClient noSSLCertificateVerification(boolean mutualTLS) throws NoSuchAlgorithmException, KeyManagementException, KeyStoreException, UnrecoverableKeyException, CertificateException, IOException {
        SSLContextBuilder sslContextBuilder = SSLContexts.custom()
                .loadTrustMaterial(new TrustAllStrategy());

        if (mutualTLS) {
            String keystoreLocation = (String) configuration.getProperty("iesi.security.keystore.location")
                    .orElse("");
            String storePassword = (String) configuration.getProperty("iesi.security.keystore.store-password")
                    .orElse("");
            String keyPassword = (String) configuration.getProperty("iesi.security.keystore.key-password")
                    .orElse(storePassword);

            sslContextBuilder.loadKeyMaterial(new File(keystoreLocation), storePassword.toCharArray(), keyPassword.toCharArray());
        }

        SSLContext sslContext= sslContextBuilder.build();

        // we can optionally disable hostname verification.
        // if you don't want to further weaken the security, you don't have to include this.
        HostnameVerifier allowAllHosts = new NoopHostnameVerifier();

        // create an SSL Socket Factory to use the SSLContext with the trust self signed certificate strategy
        // and allow all hosts verifier.
        SSLConnectionSocketFactory connectionFactory = new SSLConnectionSocketFactory(sslContext, allowAllHosts);
        // finally create the HttpClient using HttpClient factory methods and assign the ssl socket factory
        return HttpClients.custom().setSSLSocketFactory(connectionFactory).build();
    }

    private CloseableHttpClient noSSLCertificateVerification(ProxyConnection proxyConnection, boolean mutualTLS) throws NoSuchAlgorithmException, KeyManagementException, KeyStoreException, UnrecoverableKeyException, CertificateException, IOException {
        SSLContextBuilder sslContextBuilder = SSLContexts.custom()
                .loadTrustMaterial(new TrustAllStrategy());

        if (mutualTLS) {
            String keystoreLocation = (String) configuration.getProperty("iesi.security.keystore.location")
                    .orElse("");
            String storePassword = (String) configuration.getProperty("iesi.security.keystore.store-password")
                    .orElse("");
            String keyPassword = (String) configuration.getProperty("iesi.security.keystore.key-password")
                    .orElse(storePassword);

            sslContextBuilder.loadKeyMaterial(new File(keystoreLocation), storePassword.toCharArray(), keyPassword.toCharArray());
        }

        SSLContext sslContext= sslContextBuilder.build();

        // we can optionally disable hostname verification.
        // if you don't want to further weaken the security, you don't have to include this.
        HostnameVerifier allowAllHosts = new NoopHostnameVerifier();

        // create an SSL Socket Factory to use the SSLContext with the trust self signed certificate strategy
        // and allow all hosts verifier.
        SSLConnectionSocketFactory connectionFactory = new SSLConnectionSocketFactory(sslContext, allowAllHosts);
        log.debug("Adding proxy: " + proxyConnection.getHostName() + ":" + proxyConnection.getPort());
        HttpHost proxy = new HttpHost(proxyConnection.getHostName(), proxyConnection.getPort());
        DefaultProxyRoutePlanner routePlanner = new DefaultProxyRoutePlanner(proxy);
        // finally create the HttpClient using HttpClient factory methods and assign the ssl socket factory
        return HttpClients.custom()
                .setSSLSocketFactory(connectionFactory)
                .setRoutePlanner(routePlanner)
                .build();
    }
}
