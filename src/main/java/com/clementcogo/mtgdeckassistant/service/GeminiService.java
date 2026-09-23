package com.clementcogo.mtgdeckassistant.service;

import com.clementcogo.mtgdeckassistant.integration.gemini.model.ScryfallQuerySuggestions;

public interface GeminiService {
    ScryfallQuerySuggestions getSuggestions(String commanderName,String typeLine,String cmc,String colorIdentity,String oracleText,String prompt);
}
//TODO