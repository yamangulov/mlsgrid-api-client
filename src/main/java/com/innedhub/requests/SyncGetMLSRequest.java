package com.innedhub.requests;

import com.innedhub.PropsLoader;
import com.innedhub.enums.MLSResource;
import com.innedhub.exceptions.NotValidKeyForServiceModeException;
import okhttp3.CertificatePinner;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

public class SyncGetMLSRequest implements MLSRequest {
    //overloaded method for SINGLE factory mode
    @Override
    public String doRequest(MLSResource resource, String apiUri, String apiKey, String... params) {
        return getResponse(resource, apiUri, apiKey, params);
    }
    //overloaded method for SERVICE factory mode
    @Override
    public String doRequest(MLSResource resource, String apiUri, String apiKey, String apiServiceKey, String... params) {
        String responseString = null;
        try {
            if (checkApiKey(apiKey)) {
                responseString = getResponse(resource, apiUri, apiServiceKey, params);
            } else {
                throw new NotValidKeyForServiceModeException();
            }

        } catch (NotValidKeyForServiceModeException e) {
            e.printStackTrace();
        }
        return responseString;
    }

    private boolean checkApiKey(String apiKey) {
        //TODO
        //realize method checkApiKey for checking apiKey of client in SERVICE mode
        return false;
    }

    private String getResponse(MLSResource resource, String apiUri, String apiServiceKey, String... params) {
        String responseString = null;
        Properties properties = new Properties();
        PropsLoader.load(properties);

        CertificatePinner.Builder certificatePinnerBuilder = new CertificatePinner.Builder();
        properties.forEach((key, value) -> {
            if (key.toString().startsWith("certpinner")) {
                certificatePinnerBuilder.add(apiUri, value.toString());
            }
        });
        CertificatePinner certificatePinner = certificatePinnerBuilder
                .build();
        OkHttpClient.Builder clientBuilder = new OkHttpClient.Builder();
        OkHttpClient client = clientBuilder
                .connectTimeout(Long.parseLong(properties.getProperty("mlsgrid.connectTimeout")), TimeUnit.MILLISECONDS)
                .writeTimeout(Long.parseLong(properties.getProperty("mlsgrid.writeTimeout")), TimeUnit.MILLISECONDS)
                .readTimeout(Long.parseLong(properties.getProperty("mlsgrid.readTimeout")), TimeUnit.MILLISECONDS)
                .certificatePinner(certificatePinner)
                .build();

        StringBuilder requestStringBuilder = new StringBuilder("https://");
        requestStringBuilder.append(apiUri);
        requestStringBuilder.append("/");
        requestStringBuilder.append(resource.getResource());
        //get keys for specific resource from corresponding enum because set of get params differs for different resources (there's searchable fields in https://docs.mlsgrid.com/#searchable-fields)
        //append key=value for each key in specific set of keys to StringBuilder
        if (params.length != 0) {
            requestStringBuilder.append("?filter=");
            List<String> resourceParams = resource.getResourceParams();
            for (int i = 0; i < params.length; i++) {
                if (!params[i].isEmpty()) {
                    requestStringBuilder.append(resourceParams.get(i));
                    requestStringBuilder.append("%20eq%20");
                    requestStringBuilder.append(params[i]);
                    if (i != params.length -1) {
                        requestStringBuilder.append("&");
                    }
                }
            }
        }
        String requestString = requestStringBuilder.toString();

        Request request = new Request.Builder()
                .url(requestString)
                .addHeader("Authorization", "Bearer " + apiServiceKey)
                .build();
        try(Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) throw new IOException("Unexpected code in response from https://api.mls.com: " + response);
            responseString = response.body() == null ? "" : new String(response.body().bytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return responseString;
    }
}
