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
import com.clementcogo.mtgdeckassistant.integration.scryfall.ScryfallClient;
import com.clementcogo.mtgdeckassistant.integration.scryfall.model.ScryfallCardCollection;
import com.clementcogo.mtgdeckassistant.integration.scryfall.model.ScryfallCollectionRequest;
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
            }else{
                //System.out.println("DUPLICATE FOUND: " + card.getName() + " in QUERY :" + query.getTitle());
            }
        }
        query.setCards(newCards);
    }
    //TODO
    @Override
    public DeckStatsResponse getDeckStats(Long deckId) {
        Deck deck = deckService.getEntityByDeckId(deckId);
        DeckStatsResponse response = new DeckStatsResponse(deckId,deck.getName(),deck.getCreatedAt(),deck.getSlots().size(),deck.getCommander().getCardName());
        List<String> cardNames = deck.getCardNames();
        ScryfallCardCollection result = scryfallService.getCardCollectionByNames(cardNames);
        return response;
    }


}
