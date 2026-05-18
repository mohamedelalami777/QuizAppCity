package com.example.quizapp_elalami;

import com.google.gson.annotations.SerializedName;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface OllamaApi {
    @POST("api/generate")
    Call<OllamaResponse> generateResponse(@Body OllamaRequest request);

    class OllamaRequest {
        private String model;
        private String prompt;
        private boolean stream;

        public OllamaRequest(String model, String prompt) {
            this.model = model;
            this.prompt = prompt;
            this.stream = false;
        }
    }

    class OllamaResponse {
        @SerializedName("response")
        private String response;

        public String getResponse() {
            return response;
        }
    }
}