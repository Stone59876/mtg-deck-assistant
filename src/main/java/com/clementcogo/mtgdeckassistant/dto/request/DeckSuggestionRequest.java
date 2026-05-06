package com.clementcogo.mtgdeckassistant.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

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
    String lang = "EN";
    String order = "edhrec";

    public DeckSuggestionRequest(int limit, String lang, String order) {
        this.limit = limit;
        this.lang = lang;
        this.order = order;
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
}
