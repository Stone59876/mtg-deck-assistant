package com.clementcogo.mtgdeckassistant.dto.response;

public class SetCommanderResponse {
    Long deckId;
    boolean valid;
    String cardName;

    public SetCommanderResponse(Long deckId,String cardName) {
        this.deckId = deckId;
        this.cardName = cardName;
        this.valid = false;
    }

    public void setValid(boolean valid) {
        this.valid = valid;
    }

    public Long getDeckId() {
        return deckId;
    }

    public void setDeckId(Long deckId) {
        this.deckId = deckId;
    }

    public boolean isValid() {
        return valid;
    }

    public String getCardName() {
        return cardName;
    }

    public void setCardName(String cardName) {
        this.cardName = cardName;
    }
}
