package com.clementcogo.mtgdeckassistant.integration.scryfall;

import com.clementcogo.mtgdeckassistant.exception.NotFoundException;
import com.clementcogo.mtgdeckassistant.exception.RateLimitException;
import com.clementcogo.mtgdeckassistant.integration.scryfall.model.ScryfallCardRaw;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import static org.springframework.http.MediaType.APPLICATION_JSON;

@Service
public class ScryfallClient {

    RestClient defaultClient = RestClient.create();


    public ScryfallCardRaw getCardByExactName(String name) {
        return defaultClient.get().uri("https://api.scryfall.com/cards/named?exact={name}",name).accept(APPLICATION_JSON).retrieve().onStatus(status -> status.value() == 404, (request, response) -> {
            throw new NotFoundException("Scryfall: card not found: " + name);
        }).onStatus(status -> status.value() == 429, (request, response) -> {
            throw new RateLimitException("Scryfall rate limit exceeded : " + response.getStatusText());
        }).body(ScryfallCardRaw.class);
    }

}
