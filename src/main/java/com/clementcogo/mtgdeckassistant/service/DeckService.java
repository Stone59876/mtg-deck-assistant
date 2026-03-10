package com.clementcogo.mtgdeckassistant.service;

import com.clementcogo.mtgdeckassistant.dto.request.AddCardRequest;
import com.clementcogo.mtgdeckassistant.dto.request.CreateDeckRequest;
import com.clementcogo.mtgdeckassistant.dto.request.ImportDeckListRequest;
import com.clementcogo.mtgdeckassistant.dto.response.DeckResponse;
import com.clementcogo.mtgdeckassistant.dto.response.ImportResultResponse;
import com.clementcogo.mtgdeckassistant.dto.response.SlotResponse;
import com.clementcogo.mtgdeckassistant.entities.Format;
import org.springframework.stereotype.Service;

import java.util.List;


public interface DeckService {
    DeckResponse create(CreateDeckRequest request);
    DeckResponse getById(Long id);
    DeckResponse addCardToDeck(Long id,AddCardRequest request);
    ImportResultResponse importDeckList(Long id, String decklist,boolean mergeDuplicates);
    List<SlotResponse> getCards(Long deckId);
}
