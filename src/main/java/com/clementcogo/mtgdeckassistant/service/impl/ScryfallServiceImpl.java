package com.clementcogo.mtgdeckassistant.service.impl;

import com.clementcogo.mtgdeckassistant.dto.response.CardPreviewResponse;
import com.clementcogo.mtgdeckassistant.dto.response.SearchPageResponse;
import com.clementcogo.mtgdeckassistant.integration.scryfall.ScryfallClient;
import com.clementcogo.mtgdeckassistant.integration.scryfall.model.ScryfallCardCollection;
import com.clementcogo.mtgdeckassistant.integration.scryfall.model.ScryfallCardRaw;
import com.clementcogo.mtgdeckassistant.integration.scryfall.model.ScryfallCollectionRequest;
import com.clementcogo.mtgdeckassistant.integration.scryfall.model.ScryfallSearchResponseRaw;
import com.clementcogo.mtgdeckassistant.service.ScryfallService;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static java.lang.Integer.min;


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
            if(limit < 1 || limit > 175) {
                throw new IllegalArgumentException("limit should be at least 1 and less than or equal to 175");
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

    @Override
    @Cacheable(cacheNames = "scryfallCollection" , key = "#cardNames")
    public ScryfallCardCollection getCardCollectionByNames(List<String> cardNames){
        List<ScryfallCardRaw> allCards = new ArrayList<>();
        List<Map<String,String>> notFound = new ArrayList<>();
        ScryfallCardCollection collection = new ScryfallCardCollection();
        for(int i = 0; i < cardNames.size(); i += 75){
            int end = min(i + 75,cardNames.size());
            List<String> batch = cardNames.subList(i,end);
            List<Map<String,String>> identifiers = new ArrayList<>();
            batch.forEach(s -> {
                Map<String, String> identifier = new HashMap<>();
                identifier.put("name",s.trim());
                identifiers.add(identifier);
            });
            ScryfallCollectionRequest scryfallCollectionRequest = new ScryfallCollectionRequest(identifiers);
            ScryfallCardCollection batchResult = scryfallClient.getCardCollection(scryfallCollectionRequest);
            allCards.addAll(batchResult.getCollectionData());
            if(batchResult.getNotFound() != null) {
                notFound.addAll(batchResult.getNotFound());
            }
        }
        collection.setCollectionData(allCards);
        collection.setNotFound(notFound);
        return collection;
    }
}
