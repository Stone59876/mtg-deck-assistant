package com.clementcogo.mtgdeckassistant.dto.request;

import jakarta.validation.constraints.Min;

public class UpdateCardRequest {
    @Min(1)
    int qty;

    public UpdateCardRequest(int qty) {
        this.qty = qty;
    }

    public int getQty() {
        return qty;
    }

    public void setQty(int qty) {
        this.qty = qty;
    }
}
