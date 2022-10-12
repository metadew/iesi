package io.metadew.iesi.connection.http.request;

import io.metadew.iesi.connection.http.ProxyConnection;
import io.metadew.iesi.connection.http.response.HttpResponse;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;

public interface IHttpRequestService {

    HttpResponse send(HttpRequest httpRequest, boolean withCertificates) throws IOException, KeyManagementException, NoSuchAlgorithmException, UnrecoverableKeyException, KeyStoreException;

    HttpResponse send(HttpRequest httpRequest, ProxyConnection proxyConnection, boolean withCertificates) throws IOException, KeyManagementException, NoSuchAlgorithmException, UnrecoverableKeyException, KeyStoreException;

}
