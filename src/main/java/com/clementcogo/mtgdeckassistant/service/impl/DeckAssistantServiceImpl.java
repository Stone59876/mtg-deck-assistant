package com.clementcogo.mtgdeckassistant.service.impl;

import com.clementcogo.mtgdeckassistant.dto.request.DeckSuggestionRequest;
import com.clementcogo.mtgdeckassistant.dto.response.CardPreviewResponse;
import com.clementcogo.mtgdeckassistant.dto.response.DeckSuggestionResponse;
import com.clementcogo.mtgdeckassistant.entities.Deck;
import com.clementcogo.mtgdeckassistant.entities.Format;
import com.clementcogo.mtgdeckassistant.exception.NotFoundException;
import com.clementcogo.mtgdeckassistant.service.DeckAssistantService;
import com.clementcogo.mtgdeckassistant.service.DeckService;
import com.clementcogo.mtgdeckassistant.service.GeminiService;
import com.clementcogo.mtgdeckassistant.service.ScryfallService;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

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
        geminiService.getSuggestions(commander.getName(),commander.getTypeLine(),commander.getCmc().toString(),commander.getColorIdentity().toString(),commander.getOracleText());
        return null;
    }

}
