package com.clementcogo.mtgdeckassistant.dto.response;

import java.util.List;

public class DeleteCardResponse {
    Long deckId;
    Long slotId;
    String cardName;
    List<SlotResponse> remainingCards;

    public DeleteCardResponse(Long deckId, Long slotId, String cardName, List<SlotResponse> remainingCards) {
        this.deckId = deckId;
        this.slotId = slotId;
        this.cardName = cardName;
        this.remainingCards = remainingCards;
    }

    public Long getDeckId() {
        return deckId;
    }

    public void setDeckId(Long deckId) {
        this.deckId = deckId;
    }

    public Long getSlotId() {
        return slotId;
    }

    public void setSlotId(Long slotId) {
        this.slotId = slotId;
    }

    public String getCardName() {
        return cardName;
    }

    public void setCardName(String cardName) {
        this.cardName = cardName;
    }

    public List<SlotResponse> getRemainingCards() {
        return remainingCards;
    }

    public void setRemainingCards(List<SlotResponse> remainingCards) {
        this.remainingCards = remainingCards;
    }


}
