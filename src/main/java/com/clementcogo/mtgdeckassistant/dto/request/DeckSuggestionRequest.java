package com.clementcogo.mtgdeckassistant.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;

/**
 * Paramètres de la demande de suggestions pour un deck.
 *
 * Ce DTO représente ce que le client (front/Postman) peut contrôler :
 * - limit : nombre de cartes max à afficher par requête Scryfall (1..100).
 * - lang  : langue souhaitée (ex: "EN", "FR") -> à utiliser plus tard (Scryfall supporte le multi-langue via d’autres endpoints/params).
 * - order : tri Scryfall (ex: "edhrec") pour prioriser des cartes populaires en EDH.
 *
 * Remarque :
 * - Les valeurs par défaut sont définies directement dans les champs.
 * - Les @Min/@Max sécurisent l’API côté validation.
 */

public class DeckSuggestionRequest {
    @Max(100) @Min(1)
    int limit= 5;
    String lang = "EN"; //Unused TODO
    String order = "edhrec";
    @Max(10) @Min(1)
    int page=1;
    @Size(max = 1000)
    String prompt;

    public DeckSuggestionRequest(int limit, String lang, String order,int page,String prompt) {
        this.limit = limit;
        this.lang = lang;
        this.order = order;
        this.page = page;
        setPrompt(prompt);
    }

    public DeckSuggestionRequest() {}

    public int getLimit() {
        return limit;
    }

    public void setLimit(int limit) {
        this.limit = limit;
    }

    public String getLang() {
        return lang;
    }

    public void setLang(String lang) {
        this.lang = lang;
    }

    public String getOrder() {
        return order;
    }

    public void setOrder(String order) {
        this.order = order;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public String getPrompt() {
        return prompt;
    }

    public void setPrompt(String prompt) {
        if(prompt != null && !prompt.trim().isEmpty()) {
            this.prompt = prompt.trim();
        } else {
            this.prompt = "Aucune préférence";
        }
    }
}
