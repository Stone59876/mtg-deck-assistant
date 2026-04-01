package com.clementcogo.mtgdeckassistant.integration.scryfall.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class ScryfallCardRaw {
    String id;
    String name;
    String mana_cost;
    Double cmc;
    String type_line;
    String oracle_text;
    List<String> colors;
    List<String> color_identity;
    @JsonProperty("set")
    String setCode;

    public ScryfallCardRaw() {}

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMana_cost() {
        return mana_cost;
    }

    public void setMana_cost(String mana_cost) {
        this.mana_cost = mana_cost;
    }

    public Double getCmc() {
        return cmc;
    }

    public void setCmc(Double cmc) {
        this.cmc = cmc;
    }

    public String getType_line() {
        return type_line;
    }

    public void setType_line(String type_line) {
        this.type_line = type_line;
    }

    public String getOracle_text() {
        return oracle_text;
    }

    public void setOracle_text(String oracle_text) {
        this.oracle_text = oracle_text;
    }

    public List<String> getColors() {
        return colors;
    }

    public void setColors(List<String> colors) {
        this.colors = colors;
    }

    public List<String> getColor_identity() {
        return color_identity;
    }

    public void setColor_identity(List<String> color_identity) {
        this.color_identity = color_identity;
    }

    public String getSetCode() {
        return setCode;
    }

    public void setSetCode(String setCode) {
        this.setCode = setCode;
    }
}
