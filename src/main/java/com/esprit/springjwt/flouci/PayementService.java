package com.esprit.springjwt.flouci;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.squareup.okhttp.*;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.HashMap;

@Service
public class PayementService {

    @Value("${flouci.app_token}")
    private String appToken;

    @Value("${flouci.app_secret}")
    private String appSecret;

    @Value("${flouci.developer_tracking_id}")
    private String developerTrackingId;
    @Value("${site.base.url.https}")
    private String baseurl;

    private final OkHttpClient client = new OkHttpClient();

    public ResponsePayment generatePayment(Integer amount,String studyplace,Long id, String email,String period,String paiementType) throws IOException {
        // Defining the media type
        MediaType mediaType = MediaType.parse("application/json");
        String successLink = String.format("http://localhost:8094/api/payment/success?amount=%d&studyplace=%s&id=%d&email=%s&period=%s&paiementType=%s",
                amount, studyplace, id,email, period, paiementType);
        // Creating a HashMap to store request data
        HashMap<String, Object> requestHashMap = new HashMap<>();
        requestHashMap.put("app_token", appToken);
        requestHashMap.put("app_secret", appSecret);
        requestHashMap.put("accept_card", "true"); // Note: OkHttp 2.x accepts String for boolean values
        requestHashMap.put("amount", String.valueOf(amount));
        requestHashMap.put("studyplace", String.valueOf(studyplace));
        requestHashMap.put("id", String.valueOf(id));
        requestHashMap.put("email", String.valueOf(email));
        requestHashMap.put("period", String.valueOf(period));
        requestHashMap.put("paiementType", String.valueOf(paiementType));
        requestHashMap.put("success_link", successLink);
        requestHashMap.put("fail_link", "http://localhost:8094/api/payment/error");
        requestHashMap.put("session_timeout_secs", 1200);
        requestHashMap.put("developer_tracking_id", developerTrackingId);

        // Convert the HashMap to JSON using ObjectMapper
        ObjectMapper mapper = new ObjectMapper();
        String json = mapper.writeValueAsString(requestHashMap);

        // Creating the request body with the JSON
        RequestBody body = RequestBody.create(mediaType, json);

        // Creating the POST request with the Flouci API URL, request body, and appropriate headers
        Request request = new Request.Builder()
                .url("https://developers.flouci.com/api/generate_payment")
                .post(body)
                .addHeader("Content-Type", "application/json")
                .build();

        // Executing the request
        Response response = client.newCall(request).execute();

        // Handling the response
        try {
            // Checking for successful response
            if (!response.isSuccessful()) {
                throw new IOException("Unexpected code " + response.code());
            }

            // Processing the JSON response
            String responseBody = response.body().string();
            JsonNode jsonNode = mapper.readTree(responseBody);

            // Constructing the ResponsePayment object from JSON response data
            ResponsePayment responsePayment = new ResponsePayment();
            responsePayment.setPayment_id(jsonNode.path("result").path("payment_id").asText()); // Payment ID
            responsePayment.setLink(jsonNode.path("result").path("link").asText()); // Payment link
            responsePayment.setSuccess(jsonNode.path("result").path("success").asBoolean());
            responsePayment.setDeveloper_tracking_id(developerTrackingId);

            // Returning the ResponsePayment object
            return responsePayment;
        } finally {
            if (response.body() != null) {
                response.body().close();
            }
        }
    }

    public boolean verifyPayment(String paymentId) throws IOException {
        String apiUrl = "https://developers.flouci.com/api/verify_payment/" + paymentId;

        Request request = new Request.Builder()
                .url(apiUrl)
                .addHeader("apppublic", appToken)
                .addHeader("appsecret", appSecret)
                .build();

        boolean result = false;
        Response response = client.newCall(request).execute();
        try {
            if (response.isSuccessful()) {
                ObjectMapper objectMapper = new ObjectMapper();
                String responseBody = response.body().string();
                JsonNode jsonNode = objectMapper.readTree(responseBody);
                String statusPayment = jsonNode.path("result").path("status").asText();
                result = "SUCCESS".equals(statusPayment);
            } else {
                System.err.println("Erreur lors de la vérification du paiement. Code de statut : " + response.code());
            }
        } finally {
            if (response.body() != null) {
                response.body().close();
            }
        }
        return result;
    }
}
