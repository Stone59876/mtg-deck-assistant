package com.clementcogo.mtgdeckassistant.service.impl;

import com.clementcogo.mtgdeckassistant.dto.request.DeckSuggestionRequest;
import com.clementcogo.mtgdeckassistant.dto.response.AssistantSuggestionResponse;
import com.clementcogo.mtgdeckassistant.dto.response.CardPreviewResponse;
import com.clementcogo.mtgdeckassistant.dto.response.DeckStatsResponse;
import com.clementcogo.mtgdeckassistant.dto.response.DeckSuggestionResponse;
import com.clementcogo.mtgdeckassistant.entities.Deck;
import com.clementcogo.mtgdeckassistant.entities.DeckSlot;
import com.clementcogo.mtgdeckassistant.enumeration.Format;
import com.clementcogo.mtgdeckassistant.integration.gemini.model.RawScryfallQuery;
import com.clementcogo.mtgdeckassistant.integration.gemini.model.ScryfallQuerySuggestions;
import com.clementcogo.mtgdeckassistant.integration.scryfall.model.ScryfallCardCollection;
import com.clementcogo.mtgdeckassistant.integration.scryfall.model.ScryfallCardRaw;
import com.clementcogo.mtgdeckassistant.service.DeckAssistantService;
import com.clementcogo.mtgdeckassistant.service.DeckService;
import com.clementcogo.mtgdeckassistant.service.GeminiService;
import com.clementcogo.mtgdeckassistant.service.ScryfallService;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@Transactional
public class DeckAssistantServiceImpl implements DeckAssistantService {

    private final DeckService deckService;

    private final GeminiService geminiService;

    private final ScryfallService scryfallService;

    public DeckAssistantServiceImpl(DeckService deckService, GeminiService geminiService, ScryfallService scryfallService) {
        this.deckService = deckService;
        this.geminiService = geminiService;
        this.scryfallService = scryfallService;
    }

    @Override
    public DeckSuggestionResponse getSuggestion(Long deckId, DeckSuggestionRequest request) {
        Deck deck = deckService.getEntityByDeckId(deckId);
        if (!deck.getFormat().equals(Format.COMMANDER)) {
            throw new IllegalArgumentException("This is not a commander deck");
        }
        if (deck.getCommander() == null) {
            throw new IllegalArgumentException("This commander deck does not have a commander");
        }
        CardPreviewResponse commander = scryfallService.getCardPreviewByExactName(deck.getCommander().getCardName());
        ScryfallQuerySuggestions suggestions = geminiService.getSuggestions(commander.getName(), commander.getTypeLine(), commander.getCmc().toString(), commander.getColorIdentityClean(), commander.getOracleText(), request.getPrompt());
        List<AssistantSuggestionResponse> queries = new ArrayList<>();
        Set<String> existingCards = new HashSet<>();
        for (DeckSlot d : deck.getSlots()) {
            existingCards.add(d.getCardName().trim().toLowerCase());
        }
        for (RawScryfallQuery query : suggestions.getQueries()) {
            int fetchLimit = Math.min(175, request.getLimit() + request.getDuplicateBuffer());
            AssistantSuggestionResponse assistantSuggestionResponse = getQueryCards(query, fetchLimit, request.getPage(), request.getOrder());
            removeDuplicates(assistantSuggestionResponse, existingCards);
            assistantSuggestionResponse.setCards(assistantSuggestionResponse.getCards().stream().limit(request.getLimit()).toList());
            queries.add(assistantSuggestionResponse);
        }
        return new DeckSuggestionResponse(deckId, commander.getName(), queries);
    }

    private AssistantSuggestionResponse getQueryCards(RawScryfallQuery query, int limit, int page, String order) {
        return new AssistantSuggestionResponse(query.getTitle(), query.getReason(), query.getRawQuery(), order, scryfallService.searchScryfall(query.getRawQuery(), order, limit, page).getSearchData());
    }

    private void removeDuplicates(AssistantSuggestionResponse query, Set<String> existingCards) {
        List<CardPreviewResponse> newCards = new ArrayList<>();
        for (CardPreviewResponse card : query.getCards()) {
            if (!existingCards.contains(card.getName().trim().toLowerCase())) {
                newCards.add(card);
            }
        }
        query.setCards(newCards);
    }

    private Boolean containsType(ScryfallCardRaw cardRaw, String type) {
        return cardRaw.getType_line().trim().toLowerCase().contains(type);
    }

    @Override
    public DeckStatsResponse getDeckStats(Long deckId) {
        Deck deck = deckService.getEntityByDeckId(deckId);
        String commanderName = "";
        if (deck.getCommander() != null) {
            commanderName = deck.getCommander().getCardName();
        }
        DeckStatsResponse response = new DeckStatsResponse(deckId, deck.getName(), deck.getCreatedAt(), 0, commanderName, deck.getFormat());
        List<String> cardNames = deck.getCardNames();
        ScryfallCardCollection result = scryfallService.getCardCollectionByNames(cardNames);
        Map<String, ScryfallCardRaw> cardsByName = new HashMap<>();
        List<String> notFound = new ArrayList<>();
        for (ScryfallCardRaw scryfallCardRaw : result.getCollectionData()) {
            cardsByName.put(scryfallCardRaw.getName().trim().toLowerCase(), scryfallCardRaw);
        }
        if (result.getNotFound() != null) {
            for (Map<String, String> failed : result.getNotFound()) {
                notFound.add(failed.get("name"));
            }
        }
        double averageCmc = 0;
        double weightedCmcSum = 0;
        int totalCardsWithDetails = 0;
        Map<Integer, Integer> manaCurve = new HashMap<>();

//        List<ScryfallCardRaw> collectionData = result.getCollectionData();

        for (DeckSlot card : deck.getSlots()) {
            response.setTotalCards(response.getTotalCards() + card.getQty());
            ScryfallCardRaw cardDetails = cardsByName.get(card.getCardName().trim().toLowerCase());
            if (cardDetails != null) {
                String cardType = cardDetails.getType_line().trim().toLowerCase();
                int qty = card.getQty();
                if (cardType.contains("artifact")) {
                    response.setArtifacts(response.getArtifacts() + qty);
                }
                if (cardType.contains("creature")) {
                    response.setCreatures(response.getCreatures() + qty);
                }
                if (cardType.contains("sorcery")) {
                    response.setSorceries(response.getSorceries() + qty);
                }
                if (cardType.contains("enchantment")) {
                    response.setEnchantments(response.getEnchantments() + qty);
                }
                if (cardType.contains("instant")) {
                    response.setInstants(response.getInstants() + qty);
                }
                if (cardType.contains("planeswalker")) {
                    response.setPlaneswalkers(response.getPlaneswalkers() + qty);
                }
                if (cardType.contains("land")) {
                    response.setLands(response.getLands() + qty);
                } else {
                    totalCardsWithDetails += qty;
                    int cmc = cardDetails.getCmc().intValue();
                    weightedCmcSum += card.getQty() * cardDetails.getCmc();
                    manaCurve.put(cmc, manaCurve.getOrDefault(cmc, 0) + card.getQty());
                }
            }
        }
        if (totalCardsWithDetails > 0) {
            averageCmc = weightedCmcSum / totalCardsWithDetails;
        }
        response.setAverageCmc(averageCmc);
        response.setManaCurve(manaCurve);
        response.setNotFound(notFound);
        return response;
    }


}
