package com.innedhub.odata;

import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicHeader;
import org.apache.olingo.client.core.http.AbstractHttpClientFactory;
import org.apache.olingo.commons.api.http.HttpMethod;

import java.net.URI;
import java.util.Arrays;

/**
 * Implementation for working with Basic Bearer Authentication.
 */
public class BearerAuthHttpClientFactory extends AbstractHttpClientFactory {

    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String BEARER_FORMAT = "Bearer %s";
    private final String authToken;

    public BearerAuthHttpClientFactory(final String authToken) {
        this.authToken = authToken;
    }

    @Override
    public HttpClient create(final HttpMethod method, final URI uri) {
        BasicHeader authHeader = new BasicHeader(AUTHORIZATION_HEADER, String.format(BEARER_FORMAT, authToken));
        HttpClientBuilder httpclient = HttpClientBuilder.create().setDefaultHeaders(Arrays.asList(authHeader));
        return httpclient.build();
    }

    @Override
    public void close(final HttpClient httpClient) {
        httpClient.getConnectionManager().shutdown();
    }
}
