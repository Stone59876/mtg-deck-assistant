package com.clementcogo.mtgdeckassistant.service;

import com.clementcogo.mtgdeckassistant.dto.request.AddCardRequest;
import com.clementcogo.mtgdeckassistant.dto.request.CreateDeckRequest;
import com.clementcogo.mtgdeckassistant.dto.response.*;
import com.clementcogo.mtgdeckassistant.entities.DeckSlot;


import java.util.List;


public interface DeckService {
    DeckResponse create(CreateDeckRequest request);
    DeckResponse getById(Long id);
    DeckResponse addCardToDeck(Long id,AddCardRequest request);
    ImportResultResponse importDeckList(Long id, String decklist,boolean mergeDuplicates);
    List<SlotResponse> getCards(Long deckId);
    DeckValidationResponse validateDeck(Long deckId);
    SetCommanderResponse setCommander(Long deckId, String commander);
}
