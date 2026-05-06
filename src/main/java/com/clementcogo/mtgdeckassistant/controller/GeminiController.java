package com.clementcogo.mtgdeckassistant.controller;

import com.clementcogo.mtgdeckassistant.dto.response.AssistantSuggestionResponse;
import com.clementcogo.mtgdeckassistant.integration.gemini.model.ScryfallQuerySuggestions;
import com.clementcogo.mtgdeckassistant.service.GeminiService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/gemini")
public class GeminiController {

    private final GeminiService geminiService;

    public GeminiController(GeminiService geminiService) {
        this.geminiService = geminiService;
    }


    @PostMapping()
    @ResponseStatus(HttpStatus.ACCEPTED)
    public ScryfallQuerySuggestions getSuggestions(@Valid @RequestBody Long deckId){
        return geminiService.getSuggestions("The Ur-Dragon","Legendary Creature — Dragon Avatar ","9","{W}{U}{B}{R}{G}","Eminence — As long as The Ur-Dragon is in the command zone or on the battlefield, other Dragon spells you cast cost {1} less to cast.\\nFlying\\nWhenever one or more Dragons you control attack, draw that many cards, then you may put a permanent card from your hand onto the battlefield.");
    }


}
