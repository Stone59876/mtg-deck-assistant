package com.clementcogo.mtgdeckassistant.integration.scryfall.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;
//TODO
public class ScryfallCardCollection {
    @JsonProperty("not_found")
    List<Map<String, String>> notFound;
    @JsonProperty("data")
    List<ScryfallCardRaw> collectionData;

    public ScryfallCardCollection() {
        this.notFound = null;
        this.collectionData = null;
    }

    public List<ScryfallCardRaw> getCollectionData() {
        return collectionData;
    }

    public void setCollectionData(List<ScryfallCardRaw> collectionData) {
        this.collectionData = collectionData;
    }

    public List<Map<String, String>> getNotFound() {
        return notFound;
    }

    public void setNotFound(List<Map<String, String>> notFound) {
        this.notFound = notFound;
    }
}
