package org.example;

import com.google.gson.Gson;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class Main {
    public static void main(String[] args) throws Exception {

        Transcript transcript = new Transcript();
        transcript.setAudio_url("https://storage.googleapis.com/aai-web-samples/5_common_sports_injuries.mp3"); // you can change the link, you can use any audio on the wbe
        Gson gson = new Gson();
        String jsonRequest = gson.toJson(transcript);

        // Construct POST request
        HttpRequest postRequest = HttpRequest.newBuilder()
                .uri(new URI("https://api.assemblyai.com/v2/transcript"))
                .header("Authorization", "your API token put it here")  //you can go to assembly ai and create an account and they will give you the token
                .POST(HttpRequest.BodyPublishers.ofString(jsonRequest))
                .build();

        HttpClient httpClient = HttpClient.newHttpClient();

        // Send POST request
        HttpResponse<String> post_response = httpClient.send(postRequest, HttpResponse.BodyHandlers.ofString());

        System.out.println(post_response.body());

        // Parse POST response
        transcript = gson.fromJson(post_response.body(), Transcript.class);

        // Construct GET request for status
        HttpRequest getRequest = HttpRequest.newBuilder()
                .uri(new URI("https://api.assemblyai.com/v2/transcript/" + transcript.getId()))
                .header("Authorization", "b17c5090770748d4b7520adcbb16ebcf")
                .build();

        // Poll for transcript status
        while (true) {
            HttpResponse<String> get_response = httpClient.send(getRequest, HttpResponse.BodyHandlers.ofString());
            transcript = gson.fromJson(get_response.body(), Transcript.class);

            System.out.println(transcript.getStatus());
            if ("completed".equals(transcript.getStatus()) || "error".equals(transcript.getStatus())) {
                break;
            }
            Thread.sleep(1000);
        }
        System.out.println("Transcription completed ");
        System.out.println(transcript.getText());
    }
}
