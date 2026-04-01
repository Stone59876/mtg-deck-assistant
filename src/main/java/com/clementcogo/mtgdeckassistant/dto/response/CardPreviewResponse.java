package com.clementcogo.mtgdeckassistant.dto.response;

import com.clementcogo.mtgdeckassistant.integration.scryfall.model.ScryfallCardRaw;

import java.util.List;

public class CardPreviewResponse {
    String name;
    String manaCost;
    Double cmc;
    String typeLine;
    String oracleText;
    List<String> colors;
    List<String> colorIdentity;
    String setCode;
    String scryfallId;

    public CardPreviewResponse(String name,String manaCost, Double cmc, String typeLine, String oracleText, List<String> colors, List<String> colorIdentity, String setCode, String scryfallId) {
        this.name = name;
        this.manaCost = manaCost;
        this.cmc = cmc;
        this.typeLine = typeLine;
        this.oracleText = oracleText;
        this.colors = colors;
        this.colorIdentity = colorIdentity;
        this.setCode = setCode;
        this.scryfallId = scryfallId;
    }

    public CardPreviewResponse(ScryfallCardRaw card) {
        this.name = card.getName();
        this.manaCost = card.getMana_cost();
        this.cmc = card.getCmc();
        this.typeLine = card.getType_line();
        this.oracleText = card.getOracle_text();
        this.colors = card.getColors();
        this.colorIdentity = card.getColor_identity();
        this.setCode = card.getSetCode();
        this.scryfallId = card.getId();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getManaCost() {
        return manaCost;
    }

    public void setManaCost(String manaCost) {
        this.manaCost = manaCost;
    }

    public Double getCmc() {
        return cmc;
    }

    public void setCmc(Double cmc) {
        this.cmc = cmc;
    }

    public String getTypeLine() {
        return typeLine;
    }

    public void setTypeLine(String typeLine) {
        this.typeLine = typeLine;
    }

    public String getOracleText() {
        return oracleText;
    }

    public void setOracleText(String oracleText) {
        this.oracleText = oracleText;
    }

    public List<String> getColors() {
        return colors;
    }

    public void setColors(List<String> colors) {
        this.colors = colors;
    }

    public List<String> getColorIdentity() {
        return colorIdentity;
    }

    public void setColorIdentity(List<String> colorIdentity) {
        this.colorIdentity = colorIdentity;
    }

    public String getSetCode() {
        return setCode;
    }

    public void setSetCode(String setCode) {
        this.setCode = setCode;
    }

    public String getScryfallId() {
        return scryfallId;
    }

    public void setScryfallId(String scryfallId) {
        this.scryfallId = scryfallId;
    }
}
