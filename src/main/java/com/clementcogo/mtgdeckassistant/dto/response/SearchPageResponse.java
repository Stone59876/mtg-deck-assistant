package com.clementcogo.mtgdeckassistant.dto.response;

import java.util.ArrayList;
import java.util.List;

public class SearchPageResponse {
    int totalCards;
    boolean hasMore;
    String nextPage;
    List<CardPreviewResponse> searchData;

    public SearchPageResponse() {
    }

    public SearchPageResponse(int totalCards,String nextPage,boolean hasMore) {
        this.totalCards = totalCards;
        this.hasMore = hasMore;
        this.nextPage = nextPage;
        this.searchData = new ArrayList<>();
    }

    public int getTotalCards() {
        return totalCards;
    }

    public void setTotalCards(int totalCards) {
        this.totalCards = totalCards;
    }

    public boolean isHasMore() {
        return hasMore;
    }

    public void setHasMore(boolean hasMore) {
        this.hasMore = hasMore;
    }

    public String getNextPage() {
        return nextPage;
    }

    public void setNextPage(String nextPage) {
        this.nextPage = nextPage;
    }

    public List<CardPreviewResponse> getSearchData() {
        return searchData;
    }

    public void setSearchData(List<CardPreviewResponse> searchData) {
        this.searchData = searchData;
    }
}
