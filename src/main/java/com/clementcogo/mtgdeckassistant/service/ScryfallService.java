package com.clementcogo.mtgdeckassistant.service;

import com.clementcogo.mtgdeckassistant.dto.response.CardPreviewResponse;

public interface ScryfallService {
    CardPreviewResponse getCardPreviewByExactName(String name);

}
