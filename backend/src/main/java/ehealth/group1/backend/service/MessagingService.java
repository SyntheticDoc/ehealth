package ehealth.group1.backend.service;

import okhttp3.*;

import java.io.IOException;

public class MessagingService {
    private static final String API_KEY = "your-api-key";
    private static final String API_SECRET = "your-api-secret";
    private static final String API_SENDER = "your-sender-name";
    private static final String API_BASE_URL = "https://api.gatewayapi.com/rest";

    public static void sendSMS(String recipient, String message) throws IOException {
        OkHttpClient client = new OkHttpClient();

        // creating requestBody as JSON-String
        MediaType mediaType = MediaType.get("application/json");
        String jsonBody = "{\"sender\":\"" + API_SENDER + "\",\"message\":\"" + message + "\",\"recipients\":[\"" + recipient + "\"]}";
        RequestBody requestBody = RequestBody.create(jsonBody, mediaType);

        // creating request with the authentication header
        Request request = new Request.Builder()
                .url(API_BASE_URL + "/mtsms")
                .post(requestBody)
                .addHeader("gateway-api-key", API_KEY)
                .addHeader("gateway-api-secret", API_SECRET)
                .build();

        // Call via the okhttp client
        try {
            Response response = client.newCall(request).execute();

            if (response.isSuccessful()) {
                System.out.println("Message sent successfully to " + recipient);
                System.out.println(response.body().string());
            } else {
                System.err.println("Failed to send Message to " + recipient + ", response code: " + response.code());
            }

        }   catch (IOException e) {
                System.err.println("Failed to send Message to " + recipient + ", error message: " + e.getMessage());
        }
    }
}