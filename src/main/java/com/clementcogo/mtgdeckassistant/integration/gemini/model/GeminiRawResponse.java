package com.clementcogo.mtgdeckassistant.integration.gemini.model;

/**
 * Réponse brute / debug issue d’un appel Gemini.
 */

public class GeminiRawResponse {
    String response;

    public GeminiRawResponse(String response) {
        this.response = response;
    }

    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
    }
}
