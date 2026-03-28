package com.clementcogo.mtgdeckassistant.dto.response;

import com.clementcogo.mtgdeckassistant.entities.DeckSlot;
import com.clementcogo.mtgdeckassistant.entities.Format;

import java.util.ArrayList;
import java.util.List;

public class DeckValidationResponse {
    Long deckId;
    Format format;
    int totalCards;
    boolean valid;
    List<String> issues;
    List<DeckSlot> duplicateCards;

    public DeckValidationResponse(Long deckId) {
        this.deckId = deckId;
        this.issues = new ArrayList<>();
        this.duplicateCards = new ArrayList<>();
    }

    public Long getDeckId() {
        return deckId;
    }

    public void setDeckId(Long deckId) {
        this.deckId = deckId;
    }

    public Format getFormat() {
        return format;
    }

    public void setFormat(Format format) {
        this.format = format;
    }

    public int getTotalCards() {
        return totalCards;
    }

    public void setTotalCards(int totalCards) {
        this.totalCards = totalCards;
    }

    public boolean isValid() {
        return valid;
    }

    public void setValid(boolean valid) {
        this.valid = valid;
    }

    public List<String> getIssues() {
        return issues;
    }

    public void setIssues(List<String> issues) {
        this.issues = issues;
    }

    public void addIssue(String issue){
        this.issues.add(issue);
    }

    public List<DeckSlot> getDuplicateCards() {
        return duplicateCards;
    }

    public void addDuplicateCard(DeckSlot duplicate){
        this.duplicateCards.add(duplicate);
    }

    public void setDuplicateCards(List<DeckSlot> duplicateCards) {
        this.duplicateCards = duplicateCards;
    }
}
