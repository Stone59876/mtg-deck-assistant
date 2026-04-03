package com.clementcogo.mtgdeckassistant.service.impl;

import com.clementcogo.mtgdeckassistant.dto.response.CardPreviewResponse;
import com.clementcogo.mtgdeckassistant.dto.response.SearchPageResponse;
import com.clementcogo.mtgdeckassistant.integration.scryfall.ScryfallClient;
import com.clementcogo.mtgdeckassistant.integration.scryfall.model.ScryfallCardRaw;
import com.clementcogo.mtgdeckassistant.integration.scryfall.model.ScryfallSearchResponseRaw;
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

    @Override
    @Cacheable(cacheNames = "scryfallSearch" , key = "#order.trim().toLowerCase() + ':' + #query.trim().toLowerCase() + ':' + #limit+ ':' + #page")
    public SearchPageResponse searchScryfall(String query, String order,int limit,int page) {
            if(limit < 1 || limit > 100) {
                throw new IllegalArgumentException("limit should be more than 0 and less than 100");
            }
            if(page < 1 ){
                throw new IllegalArgumentException("page should be a positive integer ( > 0)");
            }
            ScryfallSearchResponseRaw searchResponseRaw = scryfallClient.searchScryfall(query,order,page);
            SearchPageResponse result = new SearchPageResponse(searchResponseRaw.getTotalCards(),searchResponseRaw.getNextPage(),searchResponseRaw.isHasMore());
            if(searchResponseRaw.getSearchData().size() <= limit) {
                for(ScryfallCardRaw cardRaw : searchResponseRaw.getSearchData()) {
                    result.getSearchData().add(new CardPreviewResponse(cardRaw));
                }
            }
            else {
                for(int i = 0; i<limit;i++) {
                    result.getSearchData().add(new CardPreviewResponse(searchResponseRaw.getSearchData().get(i)));
                }
            }
            return result;
    }

}
