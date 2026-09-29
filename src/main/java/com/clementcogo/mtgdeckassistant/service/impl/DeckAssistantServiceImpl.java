package com.clementcogo.mtgdeckassistant.service.impl;

import com.clementcogo.mtgdeckassistant.dto.request.DeckSuggestionRequest;
import com.clementcogo.mtgdeckassistant.dto.response.AssistantSuggestionResponse;
import com.clementcogo.mtgdeckassistant.dto.response.CardPreviewResponse;
import com.clementcogo.mtgdeckassistant.dto.response.DeckStatsResponse;
import com.clementcogo.mtgdeckassistant.dto.response.DeckSuggestionResponse;
import com.clementcogo.mtgdeckassistant.entities.Deck;
import com.clementcogo.mtgdeckassistant.entities.DeckSlot;
import com.clementcogo.mtgdeckassistant.entities.Format;
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
import java.util.stream.Collectors;

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
        if(!deck.getFormat().equals(Format.COMMANDER)) {
            throw new IllegalArgumentException("This is not a commander deck");
        }
        if(deck.getCommander() == null){
            throw new IllegalArgumentException("This commander deck does not have a commander");
        }
        CardPreviewResponse commander = scryfallService.getCardPreviewByExactName(deck.getCommander().getCardName());
        ScryfallQuerySuggestions suggestions = geminiService.getSuggestions(commander.getName(),commander.getTypeLine(),commander.getCmc().toString(),commander.getColorIdentityClean(),commander.getOracleText(),request.getPrompt());
        List<AssistantSuggestionResponse> queries = new ArrayList<>();
        Set<String> existingCards = new HashSet<>();
        for(DeckSlot d: deck.getSlots()) {
            existingCards.add(d.getCardName().trim().toLowerCase());
        }
        for (RawScryfallQuery query : suggestions.getQueries()) {
            int fetchLimit = Math.min(175, request.getLimit() + request.getDuplicateBuffer());
            AssistantSuggestionResponse assistantSuggestionResponse = getQueryCards(query, fetchLimit, request.getPage(), request.getOrder());
            removeDuplicates(assistantSuggestionResponse,existingCards);
            assistantSuggestionResponse.setCards(assistantSuggestionResponse.getCards().stream().limit(request.getLimit()).collect(Collectors.toList()));
            queries.add(assistantSuggestionResponse);
        }
        return new DeckSuggestionResponse(deckId, commander.getName(),queries);
    }

    private AssistantSuggestionResponse getQueryCards(RawScryfallQuery query,int limit,int page, String order){
        return new AssistantSuggestionResponse(query.getTitle(), query.getReason(), query.getRawQuery(), order,scryfallService.searchScryfall(query.getRawQuery(), order, limit,page).getSearchData());
    }

    private void removeDuplicates(AssistantSuggestionResponse query,Set<String> existingCards) {
        List<CardPreviewResponse> newCards = new ArrayList<>();
        for(CardPreviewResponse card:query.getCards()) {
            if(!existingCards.contains(card.getName().trim().toLowerCase())) {
                newCards.add(card);
            }
        }
        query.setCards(newCards);
    }

    @Override
    public DeckStatsResponse getDeckStats(Long deckId) {
        Deck deck = deckService.getEntityByDeckId(deckId);
        String commanderName = "";
        if (deck.getCommander() != null){
            commanderName = deck.getCommander().getCardName();
        }
        DeckStatsResponse response = new DeckStatsResponse(deckId,deck.getName(),deck.getCreatedAt(),0,commanderName,deck.getFormat());
        List<String> cardNames = deck.getCardNames();
        ScryfallCardCollection result = scryfallService.getCardCollectionByNames(cardNames);
        Map<String, ScryfallCardRaw> cardsByName = new HashMap<>();
        List<String> notFound = new ArrayList<>();
         for (ScryfallCardRaw scryfallCardRaw: result.getCollectionData()) {
             cardsByName.put(scryfallCardRaw.getName().trim().toLowerCase(),scryfallCardRaw);
         }
         if (result.getNotFound() != null) {
             for (Map<String,String> failed:result.getNotFound()) {
                 notFound.add(failed.get("name"));
             }
         }
         int totalArtifacts = 0;
         int totalSorceries = 0;
         int totalCreatures = 0;
         int totalLands = 0;
         int totalInstants = 0;
         int totalPlaneswalkers = 0;
         int totalEnchantments = 0;
         int totalCards = 0;
         double averageCmc = 0;
         double weightedCmcSum = 0;
         int totalCardsWithDetails = 0;
         Map<Integer,Integer> manaCurve = new HashMap<>();
         for(DeckSlot card:deck.getSlots()){
             totalCards += card.getQty();
             ScryfallCardRaw cardDetails = cardsByName.get(card.getCardName().trim().toLowerCase());
             if(cardDetails != null) {
                 String cardType = cardDetails.getType_line().trim().toLowerCase();
                 if(cardType.contains("artifact")) {
                     totalArtifacts += card.getQty();
                 }
                 if(cardType.contains("creature")) {
                     totalCreatures += card.getQty();
                 }
                 if(cardType.contains("sorcery")) {
                     totalSorceries += card.getQty();
                 }
                 if(cardType.contains("enchantment")) {
                     totalEnchantments += card.getQty();
                 }
                 if(cardType.contains("instant")) {
                     totalInstants += card.getQty();
                 }
                 if(cardType.contains("planeswalker")) {
                     totalPlaneswalkers += card.getQty();
                 }
                 if(cardType.contains("land")) {
                     totalLands += card.getQty();
                 }
                 else {
                     totalCardsWithDetails += card.getQty();
                     int cmc = cardDetails.getCmc().intValue();
                     weightedCmcSum += card.getQty() * cardDetails.getCmc();
                     manaCurve.put(cmc,manaCurve.getOrDefault(cmc,0) + card.getQty());
                 }
             }
         }
         if (totalCardsWithDetails > 0) {
             averageCmc = weightedCmcSum / totalCardsWithDetails;
         }
         response.setArtifacts(totalArtifacts);
         response.setCreatures(totalCreatures);
         response.setEnchantments(totalEnchantments);
         response.setSorceries(totalSorceries);
         response.setInstants(totalInstants);
         response.setLands(totalLands);
         response.setPlaneswalkers(totalPlaneswalkers);
         response.setTotalCards(totalCards);
         response.setAverageCmc(averageCmc);
         response.setManaCurve(manaCurve);
         response.setNotFound(notFound);
        return response;
    }


}
