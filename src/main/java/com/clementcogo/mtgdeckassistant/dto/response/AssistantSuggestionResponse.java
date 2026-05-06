package com.clementcogo.mtgdeckassistant.dto.response;

import java.util.ArrayList;
import java.util.List;

/**
 * Représente UNE suggestion “lisible front” issue de l’assistant.
 *
 * Contenu :
 * - title   : titre court de la catégorie (ex: "Cibles d'évocation").
 * - reason  : explication rapide de la synergie avec le commandant.
 * - rawQuery: requête Scryfall brute (compatible /cards/search?q=...).
 * - order   : tri Scryfall attendu (souvent "edhrec").
 * - cards   : liste de cartes “preview” résultant de l’exécution de rawQuery sur Scryfall.
 *
 * Remarque :
 * - cards est initialisé à [] pour éviter des null checks côté front.
 * - Ce DTO est la version “enrichie” par rapport au plan brut renvoyé par Gemini.
 */

public class AssistantSuggestionResponse {
    String title;
    String reason;
    String rawQuery;
    String order;
    List<CardPreviewResponse> cards = new ArrayList<>();

    public AssistantSuggestionResponse(String title, String reason, String rawQuery, String order, List<CardPreviewResponse> cards) {
        this.title = title;
        this.reason = reason;
        this.rawQuery = rawQuery;
        this.order = order.toLowerCase().trim();
        this.cards = cards;
    }

    public AssistantSuggestionResponse(String title, String reason, String rawQuery, String order) {
        this.title = title;
        this.reason = reason;
        this.rawQuery = rawQuery;
        this.order = order.toLowerCase().trim();
    }

    public AssistantSuggestionResponse() {
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getRawQuery() {
        return rawQuery;
    }

    public void setRawQuery(String rawQuery) {
        this.rawQuery = rawQuery;
    }

    public String getOrder() {
        return order;
    }

    public void setOrder(String order) {
        this.order = order.toLowerCase().trim();
    }

    public List<CardPreviewResponse> getCards() {
        return cards;
    }

    public void setCards(List<CardPreviewResponse> cards) {
        this.cards = cards;
    }

}
