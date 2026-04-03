package com.clementcogo.mtgdeckassistant.service;

import com.clementcogo.mtgdeckassistant.dto.response.CardPreviewResponse;
import com.clementcogo.mtgdeckassistant.dto.response.SearchPageResponse;

public interface ScryfallService {
    CardPreviewResponse getCardPreviewByExactName(String name);
    SearchPageResponse searchScryfall(String query, String order,int limit,int page);

}
