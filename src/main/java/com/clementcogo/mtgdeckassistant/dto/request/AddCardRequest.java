package com.clementcogo.mtgdeckassistant.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public class AddCardRequest {
    @NotBlank
    String cardName;
    @Min(1)
    int qty;

    public AddCardRequest(String cardName, int qty) {
        this.cardName = cardName;
        this.qty = qty;
    }

    public AddCardRequest() {
    }

    public String getCardName() {
        return cardName;
    }

    public void setCardName(String cardName) {
        this.cardName = cardName;
    }

    public int getQty() {
        return qty;
    }

    public void setQty(int qty) {
        this.qty = qty;
    }
}
