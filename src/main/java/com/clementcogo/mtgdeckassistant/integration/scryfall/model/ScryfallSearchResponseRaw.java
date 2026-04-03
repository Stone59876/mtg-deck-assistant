    package com.clementcogo.mtgdeckassistant.integration.scryfall.model;

    import com.fasterxml.jackson.annotation.JsonProperty;

    import java.util.List;

    public class ScryfallSearchResponseRaw {
        @JsonProperty("total_cards")
        int totalCards;
        @JsonProperty("has_more")
        boolean hasMore;
        @JsonProperty("next_page")
        String nextPage;
        @JsonProperty("data")
        List<ScryfallCardRaw> searchData;

        public ScryfallSearchResponseRaw() {
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

        public List<ScryfallCardRaw> getSearchData() {
            return searchData;
        }

        public void setSearchData(List<ScryfallCardRaw> searchData) {
            this.searchData = searchData;
        }
    }
