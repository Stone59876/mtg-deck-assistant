package com.clementcogo.mtgdeckassistant.dto.request;

import jakarta.validation.constraints.NotBlank;

public class SetCommanderRequest {
    @NotBlank
    String cardName;

    public SetCommanderRequest(String cardName) {
        this.cardName = cardName;
    }

    public SetCommanderRequest() {
    }

    public String getCardName() {
        return cardName;
    }

    public void setCardName(String cardName) {
        this.cardName = cardName;
    }
}
