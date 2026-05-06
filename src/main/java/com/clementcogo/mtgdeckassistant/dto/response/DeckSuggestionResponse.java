package com.clementcogo.mtgdeckassistant.dto.response;

import java.time.Instant;
import java.util.List;

/**
 * Réponse globale renvoyée par l’endpoint /decks/{id}/suggestions.
 *
 * Objectif : fournir au front tout ce qu’il faut pour afficher un écran “Suggestions”.
 *
 * Contenu :
 * - deckId      : deck ciblé
 * - commander   : nom du commandant utilisé pour générer les suggestions
 * - queries     : liste de suggestions (catégories), chacune avec une requête + des cartes preview
 * - generatedAt : timestamp de génération (utile pour debug/caching/UI "dernier refresh")
 */

public class DeckSuggestionResponse {
    Long deckId;
    String commander;
    List<AssistantSuggestionResponse> queries;
    Instant generatedAt;

    public DeckSuggestionResponse(Long deckId, String commander, List<AssistantSuggestionResponse> queries, Instant generatedAt) {
        this.deckId = deckId;
        this.commander = commander;
        this.queries = queries;
        this.generatedAt = generatedAt;
    }

    public DeckSuggestionResponse() {
    }

    public Long getDeckId() {
        return deckId;
    }

    public void setDeckId(Long deckId) {
        this.deckId = deckId;
    }

    public String getCommander() {
        return commander;
    }

    public void setCommander(String commander) {
        this.commander = commander;
    }

    public List<AssistantSuggestionResponse> getQueries() {
        return queries;
    }

    public void setQueries(List<AssistantSuggestionResponse> queries) {
        this.queries = queries;
    }

    public Instant getGeneratedAt() {
        return generatedAt;
    }

    public void setGeneratedAt(Instant generatedAt) {
        this.generatedAt = generatedAt;
    }
}
