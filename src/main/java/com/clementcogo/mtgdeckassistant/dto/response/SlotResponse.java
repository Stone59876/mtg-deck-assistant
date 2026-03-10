package com.clementcogo.mtgdeckassistant.dto.response;

public class SlotResponse {
    Long id;
    String cardName;
    int qty;

    public SlotResponse(Long id, String cardName, int qty) {
        this.id = id;
        this.cardName = cardName;
        this.qty = qty;
    }

    public SlotResponse() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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
