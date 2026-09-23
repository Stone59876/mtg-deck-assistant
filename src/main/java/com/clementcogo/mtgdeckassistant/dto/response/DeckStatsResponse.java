package com.clementcogo.mtgdeckassistant.dto.response;

import com.clementcogo.mtgdeckassistant.entities.Format;
//TODO
import java.time.Instant;
import java.util.HashMap;

public class DeckStatsResponse {
    Long id;
    String name;
    Format format;
    Instant createdAt;
    String commander;
    int totalCards;
    double averageCmc;
    int lands;
    int sorceries;
    int instants;
    int creatures;
    int enchantments;
    int artifacts;
    int planeswalkers;
    HashMap<Integer, Integer> manaCurve;

    public DeckStatsResponse(Long id,String name,Instant createdAt,int totalCards,String commander) {
        this.id = id;
        this.name = name;
        this.createdAt = createdAt;
        this.totalCards = totalCards;
        if(commander != null) {
            this.commander = commander;
        } else {
            this.commander = "";
        }
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Format getFormat() {
        return format;
    }

    public void setFormat(Format format) {
        this.format = format;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public String getCommander() {
        return commander;
    }

    public void setCommander(String commander) {
        this.commander = commander;
    }

    public int getTotalCards() {
        return totalCards;
    }

    public void setTotalCards(int totalCards) {
        this.totalCards = totalCards;
    }

    public double getAverageCmc() {
        return averageCmc;
    }

    public void setAverageCmc(double averageCmc) {
        this.averageCmc = averageCmc;
    }

    public int getLands() {
        return lands;
    }

    public void setLands(int lands) {
        this.lands = lands;
    }

    public int getSorceries() {
        return sorceries;
    }

    public void setSorceries(int sorceries) {
        this.sorceries = sorceries;
    }

    public int getInstants() {
        return instants;
    }

    public void setInstants(int instants) {
        this.instants = instants;
    }

    public int getCreatures() {
        return creatures;
    }

    public void setCreatures(int creatures) {
        this.creatures = creatures;
    }

    public int getEnchantments() {
        return enchantments;
    }

    public void setEnchantments(int enchantments) {
        this.enchantments = enchantments;
    }

    public int getArtifacts() {
        return artifacts;
    }

    public void setArtifacts(int artifacts) {
        this.artifacts = artifacts;
    }

    public int getPlaneswalkers() {
        return planeswalkers;
    }

    public void setPlaneswalkers(int planeswalkers) {
        this.planeswalkers = planeswalkers;
    }

    public HashMap<Integer, Integer> getManaCurve() {
        return manaCurve;
    }

    public void setManaCurve(HashMap<Integer, Integer> manaCurve) {
        this.manaCurve = manaCurve;
    }
}
