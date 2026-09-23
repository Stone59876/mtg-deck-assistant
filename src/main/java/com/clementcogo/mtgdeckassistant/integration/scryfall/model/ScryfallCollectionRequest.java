package com.clementcogo.mtgdeckassistant.integration.scryfall.model;

import java.util.List;
import java.util.Map;

public class ScryfallCollectionRequest {
    List<Map<String,String>> identifiers;

    public ScryfallCollectionRequest(List<Map<String, String>> identifiers) {
        this.identifiers = identifiers;
    }
    //TODO
    public ScryfallCollectionRequest() {
        this.identifiers = null;
    }

    public List<Map<String, String>> getIdentifiers() {
        return identifiers;
    }

    public void setIdentifiers(List<Map<String, String>> identifiers) {
        this.identifiers = identifiers;
    }
}
