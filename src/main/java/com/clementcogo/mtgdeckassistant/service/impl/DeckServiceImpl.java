package com.clementcogo.mtgdeckassistant.service.impl;

import com.clementcogo.mtgdeckassistant.dto.request.AddCardRequest;
import com.clementcogo.mtgdeckassistant.dto.request.CreateDeckRequest;
import com.clementcogo.mtgdeckassistant.dto.request.UpdateCardRequest;
import com.clementcogo.mtgdeckassistant.dto.request.UpdateDeckRequest;
import com.clementcogo.mtgdeckassistant.dto.response.*;
import com.clementcogo.mtgdeckassistant.entities.Deck;
import com.clementcogo.mtgdeckassistant.entities.DeckSlot;
import com.clementcogo.mtgdeckassistant.enumeration.Format;
import com.clementcogo.mtgdeckassistant.exception.BadRequestException;
import com.clementcogo.mtgdeckassistant.exception.ConflictException;
import com.clementcogo.mtgdeckassistant.exception.NotFoundException;
import com.clementcogo.mtgdeckassistant.repository.DeckRepository;
import com.clementcogo.mtgdeckassistant.repository.DeckSlotRepository;
import com.clementcogo.mtgdeckassistant.service.DeckService;
import com.clementcogo.mtgdeckassistant.util.DecklistParseResult;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@Transactional
public class DeckServiceImpl implements DeckService {

    private final DeckRepository deckRepository;

    private final DeckSlotRepository deckSlotRepository;

    public DeckServiceImpl(DeckRepository deckRepository,
                           DeckSlotRepository deckSlotRepository) {
        this.deckRepository = deckRepository;
        this.deckSlotRepository = deckSlotRepository;
    }

    private static final Set<String> BASIC_LANDS = Set.of("plains", "island", "swamp", "mountain", "forest", "wastes");

    @Override
    public DeckResponse create(CreateDeckRequest request) {
        Deck deck = new Deck(request.getName(), request.getFormat());
        Deck saved = deckRepository.save(deck);
        return new DeckResponse(saved.getId(), saved.getName(), saved.getFormat(), saved.getCreatedAt(), saved.getUpdatedAt());
    }

    @Override
    public DeckResponse getById(Long id) {
        Deck deck = getEntityByDeckId(id);
        return toDeckResponse(deck);
    }

    @Override
    public DeckResponse addCardToDeck(Long id, AddCardRequest request) {
        Deck deck = getEntityByDeckId(id);
        String cardName = request.getCardName().trim();
        boolean mergeDuplicates = !deck.getFormat().equals(Format.COMMANDER);
        boolean alreadyExist = upsertCard(deck, cardName, request.getQty(), mergeDuplicates);
        if (!mergeDuplicates && alreadyExist) {
            throw new ConflictException("Impossible d'ajouter cette carte car elle existe déjà dans le deck Commander, la carte est : " + cardName);
        }
        Deck saved = deckRepository.save(deck);
        return toDeckResponse(saved);
    }

    @Override
    public ImportResultResponse importDeckList(Long id, String decklist, boolean mergeDuplicates) {
        Deck deck = getEntityByDeckId(id);
        DecklistParseResult parseResult = parseDeckList(decklist);
        ImportResultResponse response = new ImportResultResponse(id, parseResult.getIgnoredLines(), parseResult.getInvalidLines());
        for (DeckSlot deckslot : parseResult.getSlots()) {
            boolean alreadyExist = upsertCard(deck, deckslot.getCardName(), deckslot.getQty(), mergeDuplicates);
            if (alreadyExist) {
                if (mergeDuplicates) {
                    response.incrementUpdatedSlots();
                } else {
                    response.incrementDuplicateLines();
                }
            } else {
                response.incrementAddedSlots();
            }

        }

        deckRepository.save(deck);
        return response;
    }

    private boolean upsertCard(Deck deck, String cardName, int qty, boolean mergeDuplicates) {
        cardName = cardName.trim();
        boolean alreadyExist;
        if (cardName.isBlank()) {
            throw new IllegalArgumentException("Nom de carte vide");
        }
        Optional<DeckSlot> deckSlot = deckSlotRepository.findByDeckIdAndCardName(deck.getId(), cardName);
        if (deckSlot.isPresent()) {
            // carte existante
            alreadyExist = true;
            DeckSlot slot = deckSlot.get();
            if (mergeDuplicates) {
                //on augmente la quantité car on merge
                int newQty = slot.getQty() + qty;
                validateCardQuantity(deck, slot, newQty);
                slot.setQty(newQty);
                deck.setUpdatedAt();
            }
        } else {
            // nouvelle carte a ajouter
            DeckSlot newCard = new DeckSlot(cardName, qty);
            validateCardQuantity(deck, newCard, qty);
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
        Deck deck = getEntityByDeckId(deckId);
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
        Deck deck = getEntityByDeckId(deckId);
        response.setValid(true);
        List<DeckSlot> slots = deck.getSlots();
        response.setFormat(deck.getFormat());
        int maxSizeLimit = 75;
        int minSizeLimit = 60;
        int totalQty = 0;
        for (DeckSlot deckSlot : slots) {
            totalQty = totalQty + deckSlot.getQty();
            if (deckSlot.getQty() > 1 && !isBasicLand(deckSlot)) {
                response.addDuplicateCard(new SlotResponse(deckSlot.getId(), deckSlot.getCardName(), deckSlot.getQty()));
            }
        }
        response.setTotalCards(totalQty);
        boolean uniqueCards = false;
        if (deck.getFormat().equals(Format.COMMANDER)) {
            minSizeLimit = 100;
            maxSizeLimit = 100;
            uniqueCards = true;
            if (deck.getCommander() == null) {
                response.setValid(false);
                response.addIssue("Commander deck with no commander");
            } else if (!deck.getCommander().getDeck().equals(deck)) {
                response.setValid(false);
                response.addIssue("Commander is in the wrong deck , should be " + deck.getId() + " but it is in " + deck.getCommander().getDeck().getId());
            }
        }
        if (totalQty > maxSizeLimit || totalQty < minSizeLimit) {
            response.addIssue("deck size is too large or too small , its " + totalQty + " but i should be between " + minSizeLimit + " and " + maxSizeLimit);
            response.setValid(false);
        }
        if (!response.getDuplicateCards().isEmpty() && uniqueCards) {
            response.addIssue("there are duplicates of cards in the deck that are not basic lands and it is not allowed in this format");
            response.setValid(false);
        }
        return response;

    }

    @Override
    public SetCommanderResponse setCommander(Long deckId, String commander) {
        SetCommanderResponse response = new SetCommanderResponse(deckId, commander);
        Deck deck = getEntityByDeckId(deckId);
        if (!deck.getFormat().equals(Format.COMMANDER)) {
            response.setValid(false);
            throw new IllegalArgumentException("This is not a commander deck");
        } else {
            for (DeckSlot deckSlot : deck.getSlots()) {
                if (deckSlot.getCardName().equalsIgnoreCase(commander) && deckSlot.getQty() == 1) {
                    deck.setCommander(deckSlot);
                    response.setValid(true);
                }
            }
        }
        if (!response.isValid()) {
            throw new IllegalArgumentException("Commander must be a single card present in the deck");
        }
        deckRepository.save(deck);
        return response;
    }

    @Override
    public CommanderResponse getCommander(Long deckId) {
        CommanderResponse response = new CommanderResponse(deckId);
        Deck deck = getEntityByDeckId(deckId);
        if (!deck.getFormat().equals(Format.COMMANDER)) {
            throw new IllegalArgumentException("This is not a commander deck");
        } else {
            if (deck.getCommander() != null) {
                response = new CommanderResponse(deckId, deck.getCommander().getCardName(), true);
            }
        }
        return response;
    }

    private DeckResponse toDeckResponse(Deck deck) {
        if (deck.getCommander() != null) {
            return new DeckResponse(deck.getId(), deck.getName(), deck.getFormat(), deck.getCreatedAt(), deck.getCommander().getCardName(), deck.getUpdatedAt());
        }
        return new DeckResponse(deck.getId(), deck.getName(), deck.getFormat(), deck.getCreatedAt(), deck.getUpdatedAt());
    }

    @Override
    public Deck getEntityByDeckId(Long deckId) {
        return deckRepository.findById(deckId)
                .orElseThrow(() -> new NotFoundException("Deck not found with id " + deckId));
    }

    @Override
    public DeleteCardResponse deleteCardFromDeck(Long id, Long slotId) {
        Deck deck = getEntityByDeckId(id);
        Iterator<DeckSlot> iterator = deck.getSlots().iterator();
        DeckSlot commander = deck.getCommander();
        boolean found = false;
        String name = "";
        while (iterator.hasNext()) {
            DeckSlot slot = iterator.next();
            if (slot.getId().equals(slotId)) {
                if (slot.equals(commander)) {
                    deck.setCommander(null);
                }
                slot.setDeck(null);
                name = slot.getCardName();
                iterator.remove();
                found = true;
                break;
            }
        }
        if (!found) {
            throw new NotFoundException(
                    "Slot was not found in deck: " + deck.getName()
                            + " with id: " + id
                            + " and deckslot id: " + slotId
            );
        }
        deck.setUpdatedAt();
        List<SlotResponse> remainingCards = getCards(id);
        return new DeleteCardResponse(id, slotId, name, remainingCards);
    }

    private boolean isBasicLand(DeckSlot slot) {
        return BASIC_LANDS.contains(slot.getCardName().trim().toLowerCase());
    }

    private void validateCardQuantity(Deck deck, DeckSlot slot, int qty) {
        if (qty <= 0) {
            throw new BadRequestException("Quantity should be above 0 but is : " + qty);
        }
        if (deck.getFormat().equals(Format.COMMANDER) && qty > 1) {
            if (Objects.equals(deck.getCommander(), slot)) {
                throw new IllegalArgumentException("Commander can only be a single card in the deck for : " + slot.getCardName());
            }
            if (!isBasicLand(slot)) {
                throw new IllegalArgumentException("Commander decks can only contains single version of each non basic lands for : " + slot.getCardName());
            }
        }
    }

    @Override
    public SlotResponse updateCardFromDeck(Long id, Long slotId, UpdateCardRequest request) {
        Deck deck = getEntityByDeckId(id);
        List<DeckSlot> deckSlots = deck.getSlots();
        boolean found = false;
        String name = "";
        for (DeckSlot slot : deckSlots) {
            if (slot.getId().equals(slotId)) {
                found = true;
                name = slot.getCardName();
                validateCardQuantity(deck, slot, request.getQty());
                if (slot.getQty() != request.getQty()) {
                    slot.setQty(request.getQty());
                    deck.setUpdatedAt();
                }
                break;
            }
        }
        if (!found) {
            throw new NotFoundException(
                    "Slot was not found in deck: " + deck.getName()
                            + " with id: " + id
                            + " and deckslot id: " + slotId
            );
        }
        return new SlotResponse(slotId, name, request.getQty());
    }

    @Override
    public CommanderResponse unsetCommander(Long deckId) {
        Deck deck = getEntityByDeckId(deckId);
        String commander = null;
        if (deck.getCommander() != null) {
            commander = deck.getCommander().getCardName();
            deck.setCommander(null);
        }
        return new CommanderResponse(deckId, commander, false);
    }

    @Override
    public DeckResponse updateDeck(Long deckId, UpdateDeckRequest request) {
        Deck deck = getEntityByDeckId(deckId);
        String name = request.getName();
        Format format = request.getFormat();
        if (name != null && !name.isBlank() && !deck.getName().equals(name)) {
            deck.setName(name);
        }
        if (format != null && !deck.getFormat().equals(format)) {
            if (deck.getFormat().equals(Format.COMMANDER) && !format.equals(Format.COMMANDER)) {
                unsetCommander(deckId);
            }
            deck.setFormat(format);
        }
        return toDeckResponse(deck);
    }

    @Override
    public void deleteDeck(Long deckId) {
        Deck deck = getEntityByDeckId(deckId);
        deck.setCommander(null);
        deckRepository.delete(deck);
    }

}
