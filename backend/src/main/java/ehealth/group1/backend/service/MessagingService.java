package ehealth.group1.backend.service;

import okhttp3.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.net.ssl.HttpsURLConnection;
import java.io.DataOutputStream;
import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.net.URL;
import java.net.URLEncoder;

@Component
public class MessagingService {
    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private static final String API_KEY = "OCB1GooMCbJJN6mQDxFkCSSV";
    private static final String API_SECRET = "03*w!0je)Ao.dJ7WW&LDkAQjX3l0Nnv@uSnrKZBz";
    private static final String API_SENDER = "Heart Guard";
    private static final String API_BASE_URL = "https://api.gatewayapi.com/rest";


    public void sendSMS(String recipient, String message) throws IOException {
        // TODO: Remove following if in production version
        if(true) {
            LOGGER.info("MOCK-sending EMERGENCY SMS to " + recipient + " (message: " + message + ")");
            return;
        }

        URL url = new URL("https://gatewayapi.com/rest/mtsms");
        HttpsURLConnection con = (HttpsURLConnection) url.openConnection();
        con.setDoOutput(true);

        DataOutputStream wr = new DataOutputStream(con.getOutputStream());
        wr.writeBytes(
            "token=dRqKDAmySTepkA8RZAkklfeJpyWFAVOekrqKfAGkj4pWd5W-QAB8VIZLvhKEuRkg"
                + "&sender=" + URLEncoder.encode(API_SENDER, "UTF-8")
                + "&message=" + URLEncoder.encode(message, "UTF-8")
                + "&recipients.0.msisdn="+recipient
        );
        wr.close();

        int responseCode = con.getResponseCode();
        System.out.println("Got response " + responseCode);
    }
}
