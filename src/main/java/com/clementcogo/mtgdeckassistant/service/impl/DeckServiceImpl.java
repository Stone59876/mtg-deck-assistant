package com.clementcogo.mtgdeckassistant.service.impl;

import com.clementcogo.mtgdeckassistant.dto.request.AddCardRequest;
import com.clementcogo.mtgdeckassistant.dto.request.CreateDeckRequest;
import com.clementcogo.mtgdeckassistant.dto.response.DeckResponse;
import com.clementcogo.mtgdeckassistant.dto.response.ImportResultResponse;
import com.clementcogo.mtgdeckassistant.dto.response.SlotResponse;
import com.clementcogo.mtgdeckassistant.entities.Deck;
import com.clementcogo.mtgdeckassistant.entities.DeckSlot;
import com.clementcogo.mtgdeckassistant.exception.NotFoundException;
import com.clementcogo.mtgdeckassistant.repository.DeckRepository;
import com.clementcogo.mtgdeckassistant.repository.DeckSlotRepository;
import com.clementcogo.mtgdeckassistant.service.DeckService;
import com.clementcogo.mtgdeckassistant.util.DecklistParseResult;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class DeckServiceImpl implements DeckService {

    @Autowired
    private DeckRepository deckRepository;

    @Autowired
    private DeckSlotRepository deckSlotRepository;

    @Override
    public DeckResponse create(CreateDeckRequest request) {
        Deck deck = new Deck(request.getName(), request.getFormat());
        Deck saved = deckRepository.save(deck);
        return new DeckResponse(saved.getId(), saved.getName(), saved.getFormat(), saved.getCreatedAt());
    }

    @Override
    public DeckResponse getById(Long id) {
        Deck deck = deckRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Deck not found with id " + id));
        return new DeckResponse(deck.getId(), deck.getName(), deck.getFormat(), deck.getCreatedAt());
    }

    @Override
    public DeckResponse addCardToDeck(Long id, AddCardRequest request) {
        Deck deck = deckRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Deck not found with id " + id));
        String cardName = request.getCardName().trim();
        upsertCard(deck, cardName, request.getQty(),true);
        Deck saved = deckRepository.save(deck);
        return new DeckResponse(saved.getId(), saved.getName(), saved.getFormat(), saved.getCreatedAt());
    }

    @Override
    public ImportResultResponse importDeckList(Long id, String decklist,boolean mergeDuplicates) {
        Deck deck = deckRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Deck not found with id " + id));
        DecklistParseResult parseResult = parseDeckList(decklist);
        ImportResultResponse response = new ImportResultResponse(id, parseResult.getIgnoredLines(), parseResult.getInvalidLines());
        for (DeckSlot deckslot : parseResult.getSlots()) {
            boolean alreadyExist = upsertCard(deck, deckslot.getCardName(), deckslot.getQty(), mergeDuplicates);
            if (alreadyExist) {
                if (mergeDuplicates) {
                    response.incrementUpdatedSlots();
                }
                else
                {
                    response.incrementDuplicateLines();
                }
            } else {
                response.incrementAddedSlots();
            }

        }
        deckRepository.save(deck);
        return response;
    }

    private boolean upsertCard(Deck deck, String cardName, int qty,boolean mergeDuplicates) throws IllegalArgumentException {
        cardName = cardName.trim();
        boolean alreadyExist;
        if (qty <= 0) {
            throw new IllegalArgumentException("Quantité <= 0");
        } else if (cardName.isBlank()) {
            throw new IllegalArgumentException("Nom de carte vide");
        }
        Optional<DeckSlot> deckSlot = deckSlotRepository.findByDeckIdAndCardName(deck.getId(), cardName);
        if (deckSlot.isPresent()) {
            // carte existante
            alreadyExist = true;
            if (mergeDuplicates) {
                //on augmente la quantité car on merge
                deckSlot.get().setQty(deckSlot.get().getQty() + qty);
            }
        } else {
            // nouvelle carte a ajouter
            DeckSlot newCard = new DeckSlot(cardName, qty);
            deck.addSlot(newCard);
            alreadyExist = false;
        }
        return alreadyExist;
    }

    public DecklistParseResult parseDeckList(String decklist) {
        DecklistParseResult result = new DecklistParseResult();
        for (String line : decklist.lines().toList()) {
            line = line.trim();
            if (!line.isBlank() && !line.startsWith("/") && !line.startsWith("#")) {
                int firstSpace = line.indexOf(" ");
                if (firstSpace <= 0) {
                    result.incrementInvalidLines();
                } else {
                    try {
                        int qty = Integer.parseInt(line.substring(0, firstSpace).trim());
                        if (qty <= 0) {
                            result.incrementInvalidLines();
                            //Erreur : Quantité <= 0
                        } else {
                            String cardName = line.substring(firstSpace + 1).trim();
                            if (!cardName.isBlank()) {
                                result.addSlot(new DeckSlot(cardName, qty));
                            } else {
                                //Erreur : Nom vide
                                result.incrementInvalidLines();
                            }
                        }
                    } catch (NumberFormatException e) {
                        result.incrementInvalidLines();
                    }
                }
            } else {
                result.incrementIgnoredLines();
            }
        }
        return result;
    }

    @Override
    public List<SlotResponse> getCards(Long deckId) {
        Deck deck = deckRepository.findById(deckId)
                .orElseThrow(() -> new NotFoundException("Deck not found with id " + deckId));
        List<DeckSlot> slots = deck.getSlots();
        List<SlotResponse> slotResponses = new ArrayList<>();
        for (DeckSlot card : slots) {
            SlotResponse s = new SlotResponse(card.getId(), card.getCardName(), card.getQty());
            slotResponses.add(s);
        }
        return slotResponses;
    }

}
