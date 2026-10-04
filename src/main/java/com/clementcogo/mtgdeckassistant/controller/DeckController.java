    package com.clementcogo.mtgdeckassistant.controller;

    import com.clementcogo.mtgdeckassistant.dto.request.*;
    import com.clementcogo.mtgdeckassistant.dto.response.*;
    import com.clementcogo.mtgdeckassistant.service.DeckAssistantService;
    import com.clementcogo.mtgdeckassistant.service.DeckService;
    import jakarta.validation.Valid;
    import org.springframework.http.HttpStatus;
    import org.springframework.web.bind.annotation.*;

    import java.util.List;

    @RestController
    @RequestMapping("/decks")
    public class DeckController {

        private final DeckService deckService;

        private final DeckAssistantService deckAssistantService;

        public DeckController(DeckService deckService,DeckAssistantService deckAssistantService) {
            this.deckService = deckService;
            this.deckAssistantService = deckAssistantService;
        }

        @PostMapping()
        @ResponseStatus(HttpStatus.CREATED)
        public DeckResponse createDeck(@Valid @RequestBody CreateDeckRequest request){
            return deckService.create(request);
        }

        @GetMapping("/{id}")
        public DeckResponse getById(@PathVariable Long id){
            return deckService.getById(id);
        }


        @PostMapping("/{id}/import")
        @ResponseStatus(HttpStatus.CREATED)
        public ImportResultResponse importDeck(@PathVariable Long id, @Valid @RequestBody ImportDeckListRequest request){
            return deckService.importDeckList(id,request.getDecklist(),request.isMergeDuplicates());
        }

        @PostMapping("/{id}/cards")
        @ResponseStatus(HttpStatus.CREATED)
        public DeckResponse addCardToDeck(@PathVariable Long id,@Valid @RequestBody AddCardRequest request){
            return deckService.addCardToDeck(id,request);
        }

        @GetMapping("/{id}/cards")
        public List<SlotResponse> getCards(@PathVariable Long id){
            return deckService.getCards(id);
        }


        @PostMapping(value = "/{id}/import-text",consumes = "text/plain")
        @ResponseStatus(HttpStatus.CREATED)
        public ImportResultResponse importDeckPlainText(@PathVariable Long id, @RequestBody String decklist){
            return deckService.importDeckList(id,decklist,true);
        }

        @GetMapping("/{id}/validate")
        public DeckValidationResponse getDeckValidation(@PathVariable Long id){
            return deckService.validateDeck(id);
        }

        @PutMapping("/{id}/commander")
        @ResponseStatus(HttpStatus.ACCEPTED)
        public SetCommanderResponse setCommander(@PathVariable Long id, @RequestBody SetCommanderRequest request){
            return deckService.setCommander(id, request.getCardName());
        }

        @GetMapping("/{id}/commander")
        public CommanderResponse getCommander(@PathVariable Long id) { return deckService.getCommander(id);}

        @PostMapping("/{id}/suggestions")
        @ResponseStatus(HttpStatus.OK)
        public DeckSuggestionResponse getDeckSuggestion(@PathVariable Long id,@Valid @RequestBody DeckSuggestionRequest request){
            return deckAssistantService.getSuggestion(id,request);
        }

        @GetMapping("/{id}/stats")
        public DeckStatsResponse getDeckStats(@PathVariable Long id) { return deckAssistantService.getDeckStats(id);}

        @DeleteMapping("/{id}/cards/{slotId}")
        @ResponseStatus(HttpStatus.OK)
        public DeleteCardResponse deleteCardFromDeck(@PathVariable Long id,@PathVariable Long slotId){
            return deckService.deleteCardFromDeck(id,slotId);
        }

        @PatchMapping("/{id}/cards/{slotId}")
        @ResponseStatus(HttpStatus.OK)
        public SlotResponse updateCardFromDeck(@PathVariable Long id,@PathVariable Long slotId,@Valid @RequestBody UpdateCardRequest request){
            return deckService.updateCardFromDeck(id,slotId,request);
        }

        @DeleteMapping("/{id}/commander")
        @ResponseStatus(HttpStatus.OK)
        public CommanderResponse unsetCommander(@PathVariable Long id){
            return deckService.unsetCommander(id);
        }

        @PatchMapping("/{id}")
        @ResponseStatus(HttpStatus.OK)
        public DeckResponse updateDeck(@PathVariable Long id,@Valid @RequestBody UpdateDeckRequest request){
            return deckService.updateDeck(id,request);
        }

        @DeleteMapping("/{id}")
        @ResponseStatus(HttpStatus.NO_CONTENT)
        public void deleteDeck(@PathVariable Long id){
            deckService.deleteDeck(id);
        }

    }
