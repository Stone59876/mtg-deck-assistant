package com.clementcogo.mtgdeckassistant.service.impl;

import com.clementcogo.mtgdeckassistant.exception.GeminiException;
import com.clementcogo.mtgdeckassistant.integration.gemini.GeminiClient;
import com.clementcogo.mtgdeckassistant.integration.gemini.model.RawScryfallQuery;
import com.clementcogo.mtgdeckassistant.integration.gemini.model.ScryfallQuerySuggestions;
import com.clementcogo.mtgdeckassistant.service.GeminiService;
import org.springframework.stereotype.Service;

@Service
public class GeminiServiceImpl implements GeminiService {

private final GeminiClient geminiClient;

public GeminiServiceImpl(GeminiClient geminiClient){
    this.geminiClient = geminiClient;
}

    @Override
    public ScryfallQuerySuggestions getSuggestions(String commanderName,String typeLine,String cmc,String colorIdentity,String oracleText) {
            ScryfallQuerySuggestions suggestions = geminiClient.getSuggestions(commanderName, typeLine, cmc,colorIdentity,oracleText);
            if(suggestions == null) {
                throw new GeminiException("Suggestion returned null");
            } else if (suggestions.getQueries() == null || suggestions.getQueries().isEmpty()) {
                throw new GeminiException("Suggestion returned null queries");
            } else if (suggestions.getQueries().size() != 5) {
                throw new GeminiException("Suggestion returned wrong amount of queries");
            } else {
                for(RawScryfallQuery query : suggestions.getQueries()){
                    cleanUpRawQuery(query);
                    validateRawScryfallQuery(query);
                }
            }

            System.out.println("Apres check :" + suggestions);

        return suggestions;
    }

    private void validateRawScryfallQuery(RawScryfallQuery query) {
        String q;
        if (query.getRawQuery() == null || query.getRawQuery().isBlank()) {
            throw new GeminiException("Suggestion returned query with empty raw query");
        }
        else {
            q = query.getRawQuery().trim().toLowerCase();
        }
        if (q.startsWith("http") || q.contains("http") || q.contains("scryfall") || q.contains("www")) {
            throw new GeminiException("rawQuery must be Scryfall DSL, not a URL, was :" + q);
        } else if (!q.contains("o:") && !q.contains("t:") && !q.contains("ci:") && !q.contains("is:") && !q.contains("mv") && !q.contains("cmc") && !q.contains("pow") && !q.contains("id")) {
            throw new GeminiException("Suggestion returned query with too little filter (no oracle or type or color identity or mana value or cumulative mana cost or power  , got :" + q);
        } else if (query.getReason() == null || query.getReason().isBlank()) {
            throw new GeminiException("Suggestion returned query with empty reason");
        }else if (query.getTitle() == null || query.getTitle().isBlank()) {
            throw new GeminiException("Suggestion returned query with empty title");
        }
    }

    private void cleanUpRawQuery(RawScryfallQuery query){
        if (query.getOrder() == null || query.getOrder().isBlank()){
            query.setOrder("edhrec");
        } else {
            query.setOrder(query.getOrder().trim().toLowerCase());
        }
        if(query.getRawQuery() != null) {
            // On vérifie que c'est des cartes légales en commander soit avec f:edh soit avec legal:commander (les deux marche)
            if(!query.getRawQuery().trim().toLowerCase().contains("f:edh") && !query.getRawQuery().trim().toLowerCase().contains("legal:commander")) {
                query.setRawQuery(query.getRawQuery() + " f:edh");
            }
            query.setRawQuery(query.getRawQuery().trim());
        }else {
            throw new GeminiException("Suggestion returned null query");
        }
        if (query.getReason() != null) {
            query.setReason(query.getReason().trim());
        }
        if (query.getTitle() != null) {
            query.setTitle(query.getTitle().trim());
        }
    }

}
