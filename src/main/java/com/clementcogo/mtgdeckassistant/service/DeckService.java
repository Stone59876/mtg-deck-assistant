package com.clementcogo.mtgdeckassistant.service;

import com.clementcogo.mtgdeckassistant.dto.request.AddCardRequest;
import com.clementcogo.mtgdeckassistant.dto.request.CreateDeckRequest;
import com.clementcogo.mtgdeckassistant.dto.request.UpdateCardRequest;
import com.clementcogo.mtgdeckassistant.dto.request.UpdateDeckRequest;
import com.clementcogo.mtgdeckassistant.dto.response.*;
import com.clementcogo.mtgdeckassistant.entities.Deck;


import java.util.List;


public interface DeckService {
    DeckResponse create(CreateDeckRequest request);
    DeckResponse getById(Long id);
    DeckResponse addCardToDeck(Long id,AddCardRequest request);
    ImportResultResponse importDeckList(Long id, String decklist,boolean mergeDuplicates);
    List<SlotResponse> getCards(Long deckId);
    DeckValidationResponse validateDeck(Long deckId);
    SetCommanderResponse setCommander(Long deckId, String commander);
    CommanderResponse getCommander(Long deckId);
    Deck getEntityByDeckId(Long deckId);
    DeleteCardResponse deleteCardFromDeck(Long id, Long slotId);
    SlotResponse updateCardFromDeck(Long id, Long slotId, UpdateCardRequest request);
    CommanderResponse unsetCommander(Long deckId);
    DeckResponse updateDeck(Long deckId, UpdateDeckRequest request);
    void deleteDeck(Long deckId);
}