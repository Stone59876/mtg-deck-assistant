package com.clementcogo.mtgdeckassistant.controller;

import com.clementcogo.mtgdeckassistant.dto.response.CardPreviewResponse;
import com.clementcogo.mtgdeckassistant.dto.response.DeckResponse;
import com.clementcogo.mtgdeckassistant.integration.scryfall.ScryfallClient;
import com.clementcogo.mtgdeckassistant.integration.scryfall.model.ScryfallCardRaw;
import com.clementcogo.mtgdeckassistant.service.ScryfallService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/scryfall")
public class ScryfallController {

    private final ScryfallService scryfallService;

    public ScryfallController(ScryfallService scryfallService) {
        this.scryfallService = scryfallService;
    }

    @GetMapping("/named")
    public CardPreviewResponse getByExactCardName(@RequestParam String name){
        return scryfallService.getCardPreviewByExactName(name);
    }

}
