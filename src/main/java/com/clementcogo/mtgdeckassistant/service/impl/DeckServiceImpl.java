package com.clementcogo.mtgdeckassistant.service.impl;

import com.clementcogo.mtgdeckassistant.dto.request.AddCardRequest;
import com.clementcogo.mtgdeckassistant.dto.request.CreateDeckRequest;
import com.clementcogo.mtgdeckassistant.dto.response.*;
import com.clementcogo.mtgdeckassistant.entities.Deck;
import com.clementcogo.mtgdeckassistant.entities.DeckSlot;
import com.clementcogo.mtgdeckassistant.entities.Format;
import com.clementcogo.mtgdeckassistant.exception.ConflictException;
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
import java.util.Set;

@Service
@Transactional
public class DeckServiceImpl implements DeckService {

    @Autowired
    private DeckRepository deckRepository;

    @Autowired
    private DeckSlotRepository deckSlotRepository;

    private static final Set<String> BASIC_LANDS = Set.of("plains","island","swamp","mountain","forest","wastes");

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
        boolean mergeDuplicates = !deck.getFormat().equals(Format.COMMANDER);
        boolean alreadyExist = upsertCard(deck, cardName, request.getQty(),mergeDuplicates);
        if(!mergeDuplicates && alreadyExist){
            throw new ConflictException("Impossible d'ajouter cette carte car elle existe déjà dans le deck Commander, la carte est : " + cardName);
        }
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

    @Override
    public DeckValidationResponse validateDeck(Long deckId) {
        DeckValidationResponse response = new DeckValidationResponse(deckId);
        Deck deck = deckRepository.findById(deckId)
                .orElseThrow(() -> new NotFoundException("Deck not found with id " + deckId));
        response.setValid(true);
        List<DeckSlot> slots = deck.getSlots();
        response.setFormat(deck.getFormat());
        int maxSizeLimit = 75;
        int minSizeLimit = 60;
        int totalQty = 0;
        for(DeckSlot deckSlot: slots) {
            totalQty = totalQty + deckSlot.getQty();
            if(deckSlot.getQty() > 1 && !BASIC_LANDS.contains(deckSlot.getCardName().trim().toLowerCase()) ){
              response.addDuplicateCard(deckSlot);
            }
        }
        response.setTotalCards(totalQty);
        boolean uniqueCards = false;
        if(deck.getFormat().equals(Format.COMMANDER)) {
                minSizeLimit = 100;
                maxSizeLimit = 100;
                uniqueCards = true;
                if(deck.getCommander() == null){
                    response.setValid(false);
                    response.addIssue("Commander deck with no commander");
                }else if(!deck.getCommander().getDeck().equals(deck)) {
                    response.setValid(false);
                    response.addIssue("Commander is in the wrong deck , should be " + deck.getId() + " but it is in " + deck.getCommander().getDeck().getId());
                }
        }
        if(totalQty > maxSizeLimit || totalQty < minSizeLimit)
        {
            response.addIssue("deck size is too large or too small , its " + totalQty + " but i should be between " + minSizeLimit + " and " + maxSizeLimit);
            response.setValid(false);
        }
        if(!response.getDuplicateCards().isEmpty() && uniqueCards) {
            response.addIssue("there are duplicates of cards in the deck that are not basic lands and it is not allowed in this format");
            response.setValid(false);
        }
        return response;

    }

    @Override
    public SetCommanderResponse setCommander(Long deckId,String commander) {
        SetCommanderResponse response = new SetCommanderResponse(deckId,commander);
        Deck deck = deckRepository.findById(deckId)
                .orElseThrow(() -> new NotFoundException("Deck not found with id " + deckId));
        if(!deck.getFormat().equals(Format.COMMANDER)) {
            response.setValid(false);
            throw new IllegalArgumentException("This is not a commander deck");
        }
        else
        {
            for (DeckSlot deckSlot : deck.getSlots()) {
                if(deckSlot.getCardName().equalsIgnoreCase(commander) && deckSlot.getQty() == 1) {
                    deck.setCommander(deckSlot);
                    response.setValid(true);
                }
            }
        }
        if(!response.isValid()) {
            throw new IllegalArgumentException("Commander must be a single card present in the deck");
        }
        deckRepository.save(deck);
        return response;
    }

}
