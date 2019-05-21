package io.metadew.iesi.connection;

import io.metadew.iesi.connection.http.HttpRequest;
import io.metadew.iesi.connection.http.HttpResponse;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.util.EntityUtils;

import javax.net.ssl.*;
import java.net.Socket;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.util.Iterator;
import java.util.Map;

//import org.apache.http.ssl.TrustStrategy;

/**
 * Connection object for http requests.
 *
 * @author peter.billen
 */
public class HttpConnection {

    private HttpRequest httpRequest;

    // Constructor
    public HttpConnection() {
        super();
    }

    public HttpConnection(HttpRequest httpRequest) {
        super();
        this.setHttpRequest(httpRequest);
    }

    // Methods
    @SuppressWarnings("rawtypes")
    public HttpResponse executeGetRequest() {
        HttpResponse httpResponse = new HttpResponse();
        try {
            // CloseableHttpClient httpclient = HttpClients.createDefault();
            // CloseableHttpClient httpclient = createAcceptSelfSignedCertificateClient();
            CloseableHttpClient httpclient = createOverrideSSLCertificateVerification();
            HttpGet httpget = new HttpGet();

            // Add headers
            Iterator iterator = this.getHttpRequest().getHeaderMap().entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry pair = (Map.Entry) iterator.next();
                httpget.addHeader(pair.getKey().toString(), pair.getValue().toString());
                iterator.remove();
            }
            // Set URI
            httpget.setURI(this.getHttpRequest().getUriBuilder().build());
            // Execute
            CloseableHttpResponse response = httpclient.execute(httpget);

            try {

                httpResponse.setResponse(response);
                httpResponse.setStatusLine(response.getStatusLine());
                HttpEntity entity = response.getEntity();
                if (entity != null) {
                    httpResponse.setEntityString(EntityUtils.toString(entity, "UTF-8"));
                }

                EntityUtils.consume(entity);

            } catch (Exception e) {
                throw new RuntimeException(e);
            } finally {
                response.close();
            }

            return httpResponse;

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @SuppressWarnings("rawtypes")
    public HttpResponse executePostRequest(String json) {
        HttpResponse httpResponse = new HttpResponse();
        try {
            // CloseableHttpClient httpclient = HttpClients.createDefault();
            // CloseableHttpClient httpclient = createAcceptSelfSignedCertificateClient();
            CloseableHttpClient httpclient = createOverrideSSLCertificateVerification();
            HttpPost httpPost = new HttpPost();

            // Add headers
            Iterator iterator = this.getHttpRequest().getHeaderMap().entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry pair = (Map.Entry) iterator.next();
                httpPost.addHeader(pair.getKey().toString(), pair.getValue().toString());
                iterator.remove();
            }
            // Set URI
            httpPost.setURI(this.getHttpRequest().getUriBuilder().build());

            // Add entity
            StringEntity stringEntity = new StringEntity(json);
            httpPost.setEntity(stringEntity);

            // Execute
            CloseableHttpResponse response = httpclient.execute(httpPost);

            try {

                httpResponse.setResponse(response);
                httpResponse.setStatusLine(response.getStatusLine());
                HttpEntity entity = response.getEntity();
                if (entity != null) {
                    httpResponse.setEntityString(EntityUtils.toString(entity, "UTF-8"));
                }

                EntityUtils.consume(entity);

                return httpResponse;

            } catch (Exception e) {
                throw new RuntimeException(e);
            } finally {
                response.close();
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public HttpRequest getHttpRequest() {
        return httpRequest;
    }

    public void setHttpRequest(HttpRequest httpRequest) {
        this.httpRequest = httpRequest;
    }

    // Getters and setters
    @SuppressWarnings("unused")
    private static CloseableHttpClient createAcceptSelfSignedCertificateClient() {
        // use the TrustSelfSignedStrategy to allow Self Signed Certificates
        SSLContext sslContext = null;
        try {
            sslContext = SSLContextBuilder.create().loadTrustMaterial(new TrustSelfSignedStrategy()).build();
            // we can optionally disable hostname verification.
            // if you don't want to further weaken the security, you don't have to include this.
            HostnameVerifier allowAllHosts = new NoopHostnameVerifier();

            // create an SSL Socket Factory to use the SSLContext with the trust self signed certificate strategy
            // and allow all hosts verifier.
            SSLConnectionSocketFactory connectionFactory = new SSLConnectionSocketFactory(sslContext, allowAllHosts);
            // finally create the HttpClient using HttpClient factory methods and assign the ssl socket factory
            return HttpClients.custom().setSSLSocketFactory(connectionFactory).build();
        } catch (KeyManagementException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (KeyStoreException e) {
            e.printStackTrace();
        }

        return null;

    }

    private static CloseableHttpClient createOverrideSSLCertificateVerification() {
        TrustManager[] trustAllCerts = new TrustManager[]{new X509ExtendedTrustManager() {
            @Override
            public void checkClientTrusted(java.security.cert.X509Certificate[] chain, String authType) throws CertificateException {
            }

            @Override
            public void checkServerTrusted(java.security.cert.X509Certificate[] chain, String authType) throws CertificateException {
            }

            @Override
            public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                return null;
            }

            @Override
            public void checkClientTrusted(java.security.cert.X509Certificate[] arg0, String arg1, Socket arg2)
                    throws CertificateException {
            }

            @Override
            public void checkClientTrusted(java.security.cert.X509Certificate[] arg0, String arg1, SSLEngine arg2)
                    throws CertificateException {
            }

            @Override
            public void checkServerTrusted(java.security.cert.X509Certificate[] arg0, String arg1, Socket arg2)
                    throws CertificateException {
            }

            @Override
            public void checkServerTrusted(java.security.cert.X509Certificate[] arg0, String arg1, SSLEngine arg2)
                    throws CertificateException {
            }
        }};

        SSLContext sslContext = null;
        try {
            sslContext = SSLContext.getInstance("SSL");
            sslContext.init(null, trustAllCerts, new java.security.SecureRandom());
        } catch (KeyManagementException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        // we can optionally disable hostname verification.
        // if you don't want to further weaken the security, you don't have to include this.
        HostnameVerifier allowAllHosts = new NoopHostnameVerifier();

        // create an SSL Socket Factory to use the SSLContext with the trust self signed certificate strategy
        // and allow all hosts verifier.
        SSLConnectionSocketFactory connectionFactory = new SSLConnectionSocketFactory(sslContext, allowAllHosts);
        // finally create the HttpClient using HttpClient factory methods and assign the ssl socket factory
        return HttpClients.custom().setSSLSocketFactory(connectionFactory).build();
    }
}
