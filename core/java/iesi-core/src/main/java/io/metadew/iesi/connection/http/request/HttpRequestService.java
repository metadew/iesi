package io.metadew.iesi.connection.http.request;

import io.metadew.iesi.common.configuration.Configuration;
import io.metadew.iesi.connection.http.ProxyConnection;
import io.metadew.iesi.connection.http.response.HttpResponse;
import lombok.extern.log4j.Log4j2;
import org.apache.http.HttpHost;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.DefaultProxyRoutePlanner;
import org.apache.http.ssl.SSLContexts;
import org.springframework.stereotype.Service;

import javax.net.ssl.*;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.*;
import java.security.cert.CertificateException;
import java.time.LocalDateTime;

@Service
@Log4j2
public class HttpRequestService implements IHttpRequestService {

    private final Configuration configuration;

    public HttpRequestService(Configuration configuration) {
        this.configuration = configuration;
    }

    public HttpResponse send(HttpRequest httpRequest, boolean withCertificate) throws IOException, KeyManagementException, NoSuchAlgorithmException, UnrecoverableKeyException, KeyStoreException {
        CloseableHttpClient httpClient = withCertificate ? withSSLCertificateVerification() :  noSSLCertificateVerification();
        return send(httpRequest, httpClient);
    }

    public HttpResponse send(HttpRequest httpRequest, ProxyConnection proxyConnection, boolean withCertificate) throws IOException, KeyManagementException, NoSuchAlgorithmException, UnrecoverableKeyException, KeyStoreException {
        CloseableHttpClient httpClient = withCertificate ? withSSLCertificateVerification() : noSSLCertificateVerification(proxyConnection);
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

    private KeyStore readKeyStore() {
        String keystoreLocationProp = (String) configuration.getProperty("iesi.security.keystore.location")
                .orElse("");
        log.info(String.format("Keystore location is: %s", keystoreLocationProp));
        String keyStoreTypeProp = (String) configuration.getProperty("iesi.security.keystore.type")
                        .orElse("");
        log.info(String.format("The keystore type is: %s", keyStoreTypeProp));
        log.info("Fetching keystore password ...");
        String keyStorePasswordProp = (String) configuration.getProperty("iesi.security.keystore.password")
                .orElse("");

        log.info(String.format("Fetching keystore located at %s", keystoreLocationProp));
        Path keyStorePath = Paths.get(keystoreLocationProp);


        try (InputStream keyStoreStream = Files.newInputStream(keyStorePath.toFile().toPath())) {
            KeyStore keyStore = KeyStore.getInstance(keyStoreTypeProp);
            keyStore.load(keyStoreStream,  keyStorePasswordProp.toCharArray());

            return keyStore;
        } catch (IOException | KeyStoreException | NoSuchAlgorithmException | CertificateException e ) {
            throw new RuntimeException(e.getMessage());
        }
    }

    private CloseableHttpClient withSSLCertificateVerification() throws UnrecoverableKeyException, NoSuchAlgorithmException, KeyStoreException {
         try {
             SSLContext sslContext = SSLContexts.custom()
                     .loadKeyMaterial(readKeyStore(), "changeit".toCharArray())
                     .build();
             return HttpClients.custom().setSSLContext(sslContext).build();
         } catch (UnrecoverableKeyException | NoSuchAlgorithmException | KeyStoreException | KeyManagementException e ) {
             throw new RuntimeException(e.getMessage());
         }
    }
    private static CloseableHttpClient noSSLCertificateVerification() throws NoSuchAlgorithmException, KeyManagementException {
        CloseableHttpClient closeableHttpClient = HttpClients.createDefault();

        return closeableHttpClient;
    }

    private static CloseableHttpClient noSSLCertificateVerification(ProxyConnection proxyConnection) throws NoSuchAlgorithmException, KeyManagementException {
        TrustManager[] trustAllCerts = new TrustManager[]{new X509ExtendedTrustManager() {
            @Override
            public void checkClientTrusted(java.security.cert.X509Certificate[] chain, String authType) {
            }

            @Override
            public void checkServerTrusted(java.security.cert.X509Certificate[] chain, String authType) {
            }

            @Override
            public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                return null;
            }

            @Override
            public void checkClientTrusted(java.security.cert.X509Certificate[] arg0, String arg1, Socket arg2) {
            }

            @Override
            public void checkClientTrusted(java.security.cert.X509Certificate[] arg0, String arg1, SSLEngine arg2) {
            }

            @Override
            public void checkServerTrusted(java.security.cert.X509Certificate[] arg0, String arg1, Socket arg2) {
            }

            @Override
            public void checkServerTrusted(java.security.cert.X509Certificate[] arg0, String arg1, SSLEngine arg2) {
            }
        }};

        SSLContext sslContext = SSLContext.getInstance("SSL");
        sslContext.init(null, trustAllCerts, new java.security.SecureRandom());

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
