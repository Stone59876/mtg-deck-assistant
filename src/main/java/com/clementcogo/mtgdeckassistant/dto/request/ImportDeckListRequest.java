package com.clementcogo.mtgdeckassistant.dto.request;

import jakarta.validation.constraints.NotBlank;

public class ImportDeckListRequest {
    @NotBlank
    String decklist;
    boolean mergeDuplicates = true;

    public ImportDeckListRequest(String decklist, boolean mergeDuplicates) {
        this.decklist = decklist;
        this.mergeDuplicates = mergeDuplicates;
    }

    public ImportDeckListRequest() {
    }

    public String getDecklist() {
        return decklist;
    }

    public void setDecklist(String decklist) {
        this.decklist = decklist;
    }

    public boolean isMergeDuplicates() {
        return mergeDuplicates;
    }

    public void setMergeDuplicates(boolean mergeDuplicates) {
        this.mergeDuplicates = mergeDuplicates;
    }
}
