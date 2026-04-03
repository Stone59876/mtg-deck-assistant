package com.clementcogo.mtgdeckassistant.controller;

import com.clementcogo.mtgdeckassistant.dto.response.CardPreviewResponse;
import com.clementcogo.mtgdeckassistant.dto.response.SearchPageResponse;
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

    @GetMapping("/search")
    public SearchPageResponse searchScryfall(@RequestParam String query, @RequestParam String order,@RequestParam int limit,@RequestParam int page){
        return scryfallService.searchScryfall(query,order,limit,page);
    }

}
