package com.clementcogo.mtgdeckassistant.service.impl;

import com.clementcogo.mtgdeckassistant.dto.response.CardPreviewResponse;
import com.clementcogo.mtgdeckassistant.integration.scryfall.ScryfallClient;
import com.clementcogo.mtgdeckassistant.integration.scryfall.model.ScryfallCardRaw;
import com.clementcogo.mtgdeckassistant.service.ScryfallService;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
public class ScryfallServiceImpl implements ScryfallService {

    private final ScryfallClient scryfallClient;

    public ScryfallServiceImpl(ScryfallClient scryfallClient){
        this.scryfallClient = scryfallClient;
    }

    @Override
    @Cacheable(cacheNames = "scryfallCardByExactNames" , key = "#name.toLowerCase().trim()")
    public CardPreviewResponse getCardPreviewByExactName(String name){
        ScryfallCardRaw card = scryfallClient.getCardByExactName(name.trim());
        return new CardPreviewResponse(card);
    }
}
