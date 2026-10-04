package com.clementcogo.mtgdeckassistant.service;

import com.clementcogo.mtgdeckassistant.dto.response.CardPreviewResponse;
import com.clementcogo.mtgdeckassistant.dto.response.SearchPageResponse;
import com.clementcogo.mtgdeckassistant.integration.scryfall.model.ScryfallCardCollection;

import java.util.List;

public interface ScryfallService {
    CardPreviewResponse getCardPreviewByExactName(String name);

    SearchPageResponse searchScryfall(String query, String order, int limit, int page);

    ScryfallCardCollection getCardCollectionByNames(List<String> cardNames);
}
