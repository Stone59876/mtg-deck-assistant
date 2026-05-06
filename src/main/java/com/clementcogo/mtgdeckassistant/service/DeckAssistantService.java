package com.clementcogo.mtgdeckassistant.service;

import com.clementcogo.mtgdeckassistant.dto.request.DeckSuggestionRequest;
import com.clementcogo.mtgdeckassistant.dto.response.DeckSuggestionResponse;

/**
 *Orchestrateur du projet
 *
 * Rôle:
 * - Récupérer le deck (et surtout le commandant) via la couche DeckService/Repository.
 * - Construire le contexte (nom/texte du commandant, options utilisateur).
 * - Appeler Gemini pour obtenir un plan de requêtes Scryfall (JSON structuré).
 * - Exécuter ces requêtes sur Scryfall (limité à N cartes) pour enrichir la réponse.
 * - Retourner une réponse prête pour le front (suggestions + preview de cartes).
 *
 * Important :
 * - Ce service ne contient pas la logique HTTP (controller) ni la logique d’accès Scryfall/Gemini “bas niveau”.
 * - Il coordonne les dépendances (DeckService, ScryfallService, GeminiClient, etc.).
 *
 */

public interface DeckAssistantService {
    DeckSuggestionResponse getSuggestion(Long id, DeckSuggestionRequest request);
}
