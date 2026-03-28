    package com.clementcogo.mtgdeckassistant.controller;

    import com.clementcogo.mtgdeckassistant.dto.request.AddCardRequest;
    import com.clementcogo.mtgdeckassistant.dto.request.CreateDeckRequest;
    import com.clementcogo.mtgdeckassistant.dto.request.ImportDeckListRequest;
    import com.clementcogo.mtgdeckassistant.dto.request.SetCommanderRequest;
    import com.clementcogo.mtgdeckassistant.dto.response.*;
    import com.clementcogo.mtgdeckassistant.service.DeckService;
    import jakarta.validation.Valid;
    import org.springframework.http.HttpStatus;
    import org.springframework.web.bind.annotation.*;

    import java.util.List;

    @RestController
    @RequestMapping("/decks")
    public class DeckController {

        private final DeckService deckService;

        public DeckController(DeckService deckService) {
            this.deckService = deckService;
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


    }
