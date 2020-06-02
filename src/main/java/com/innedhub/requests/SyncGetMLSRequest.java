package com.innedhub.requests;

import com.innedhub.PropsLoader;
import com.innedhub.enums.MLSResource;
import com.innedhub.exceptions.NotValidKeyForServiceModeException;
import lombok.extern.slf4j.Slf4j;
import okhttp3.CertificatePinner;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

@Slf4j
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
        //log.info("Params length is: {}", params.length);
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
        //getting keys for specific resource from corresponding enum because set of get params differs for different resources (there's searchable fields in https://docs.mlsgrid.com/#searchable-fields)
        //appending key=value for each key in specific set of keys to StringBuilder
        if (params.length != 0) {
            requestStringBuilder.append("?$filter=");
            List<String> resourceParams = resource.getResourceParams();
            for (int i = 0; i < params.length; i++) {
                //log.info("Param i is: {}", params[i]);
                if (params[i] != null && !params[i].isEmpty()) {
                    requestStringBuilder.append(resourceParams.get(i));
                    if (resourceParams.get(i).equals("StandardStatus")) {
                        requestStringBuilder.append("+eq+Enums.StandardStatus%27");
                    } else {
                        requestStringBuilder.append("%20eq%20");
                    }
                    if (resourceParams.get(i).equals("MlgCanView")) {
                        requestStringBuilder.append(params[i]);
                    } else if (resourceParams.get(i).equals("StandardStatus")) {
                        requestStringBuilder.append(params[i]);
                        requestStringBuilder.append("%27");
                    } else {
                        requestStringBuilder.append("'");
                        requestStringBuilder.append(params[i]);
                        requestStringBuilder.append("'");
                    }
                    if (i != params.length - 1) {
                        requestStringBuilder.append("%20and%20");
                    }
                }
            }
        }
        String requestString = requestStringBuilder.toString();
        log.info("request string is: {}", requestString);

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
