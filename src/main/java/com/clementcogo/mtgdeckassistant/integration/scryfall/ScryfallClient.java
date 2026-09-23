package com.clementcogo.mtgdeckassistant.integration.scryfall;

import com.clementcogo.mtgdeckassistant.entities.DeckSlot;
import com.clementcogo.mtgdeckassistant.exception.NotFoundException;
import com.clementcogo.mtgdeckassistant.exception.RateLimitException;
import com.clementcogo.mtgdeckassistant.integration.scryfall.model.ScryfallCardCollection;
import com.clementcogo.mtgdeckassistant.integration.scryfall.model.ScryfallCardRaw;
import com.clementcogo.mtgdeckassistant.integration.scryfall.model.ScryfallSearchResponseRaw;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.springframework.http.MediaType.APPLICATION_JSON;

@Service
public class ScryfallClient {

    private final RestClient defaultClient = RestClient.builder().baseUrl("https://api.scryfall.com").build();

    public ScryfallCardRaw getCardByExactName(String name) {
        return defaultClient.get().uri(uriBuilder -> uriBuilder.path("/cards/named").queryParam("exact",name).build()).accept(APPLICATION_JSON).retrieve().onStatus(status -> status.value() == 404, (request, response) -> {
            throw new NotFoundException("Scryfall: card not found: " + name);
        }).onStatus(status -> status.value() == 429, (request, response) -> {
            throw new RateLimitException("Scryfall rate limit exceeded : " + response.getStatusText());
        }).body(ScryfallCardRaw.class);
    }

    public ScryfallSearchResponseRaw searchScryfall(String query, String order,int page){
        ScryfallSearchResponseRaw search = defaultClient.get().uri(uriBuilder -> uriBuilder.path("/cards/search").queryParam("order",order).queryParam("q",query).queryParam("page",page).build()).accept(APPLICATION_JSON).retrieve().onStatus(status -> status.value() == 404, (request, response) -> {
            throw new NotFoundException("Scryfall search endpoint not found");
        }).onStatus(status -> status.value() == 429, (request, response) -> {
            throw new RateLimitException("Scryfall rate limit exceeded : " + response.getStatusText());
        }).body(ScryfallSearchResponseRaw.class);
        if(search == null) {
            throw new NotFoundException("Scryfall search returned 0 result with query :" + query);
        } else {
            if(search.getSearchData() == null || search.getSearchData().isEmpty()){
                throw new NotFoundException("Scryfall search returned 0 result with query :" + query);
            }
            else {
                return search;
            }
        }
    }
    //TODO
    public ScryfallCardCollection getCardCollection(List<String> cardNames) {
        List<Map<String,String>> identifiers = new ArrayList<>();
        cardNames.forEach(s -> {
            Map<String, String> identifier = new HashMap<>();
            identifier.put("name",s);
            identifiers.add(identifier);
        });
        ScryfallCardCollection collection = defaultClient.post().uri(uriBuilder -> uriBuilder.path("/cards/collection").build()).accept(APPLICATION_JSON).body(identifiers).retrieve().onStatus(status -> status.value() == 404,(request, response) -> {
            throw new NotFoundException("Scryfall: card not found: " + identifiers);
        }).onStatus(status -> status.value() == 429, (request, response) -> {
            throw new RateLimitException("Scryfall rate limit exceeded : " + response.getStatusText());
        }).body(ScryfallCardCollection.class);
        if(collection == null) {
            throw new NotFoundException("Scryfall collection search returned 0 result with query :" + identifiers.toString());
        }else{
            if(collection.getCollectionData() == null || collection.getCollectionData().isEmpty()){
                throw new NotFoundException("Scryfall collection search returned 0 result with query :" + identifiers.toString());
            }
            else{
                return collection;
            }
        }
    }

}
