package com.innedhub;

import okhttp3.CertificatePinner;
import okhttp3.OkHttpClient;
import okhttp3.Request;

import java.io.IOException;

//class had used for getting actual sha-256 hashes for pinner of api.mlsgrid.com by https://square.github.io/okhttp/4.x/okhttp/okhttp3/-certificate-pinner/ . It'l be deleted after development
public class Main {
    public static void main(String[] args) {
        String hostname = "api.mlsgrid.com";
        CertificatePinner certificatePinner = new CertificatePinner.Builder()
                .add(hostname, "sha256/CeYwfsI1wJzuFLe2wdPgAMKCigCHL3hV3jCBoy65DbQ=")
                .add(hostname, "sha256/4a6cPehI7OG6cuDZka5NDZ7FR8a60d3auda+sKfg4Ng=")
                .add(hostname, "sha256/x4QzPSC810K5/cMjb05Qm4k3Bw5zBn4lTdO/nEW/Td4=")
                .build();
        OkHttpClient client = new OkHttpClient.Builder()
                .certificatePinner(certificatePinner)
                .build();

        Request request = new Request.Builder()
                .url("https://" + hostname)
                .build();
        try {
            client.newCall(request).execute();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
