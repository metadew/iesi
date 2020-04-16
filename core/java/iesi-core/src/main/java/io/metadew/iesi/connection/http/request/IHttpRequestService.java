package io.metadew.iesi.connection.http.request;

import io.metadew.iesi.connection.http.ProxyConnection;
import io.metadew.iesi.connection.http.response.HttpResponse;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;

public interface IHttpRequestService {

    public HttpResponse send(HttpRequest httpRequest) throws IOException, KeyManagementException, NoSuchAlgorithmException;

    public HttpResponse send(HttpRequest httpRequest, ProxyConnection proxyConnection) throws IOException, KeyManagementException, NoSuchAlgorithmException;

}
