package com.clementcogo.mtgdeckassistant.integration.gemini.model;

import java.util.List;

/**
 * Wrapper du JSON renvoyé par Gemini.
 *
 * Gemini doit répondre sous la forme :
 * {
 *   "queries": [ { ... }, { ... }, ... ]
 * }
 */

public class ScryfallQuerySuggestions {
    List<RawScryfallQuery> queries;

    public ScryfallQuerySuggestions() {
    }

    public ScryfallQuerySuggestions(List<RawScryfallQuery> queries) {
        this.queries = queries;
    }

    public List<RawScryfallQuery> getQueries() {
        return queries;
    }

    public void setQueries(List<RawScryfallQuery> queries) {
        this.queries = queries;
    }

    @Override
    public String toString() {
        return "ScryfallQuerySuggestions{" +
                "queries=" + queries.toString() +
                '}';
    }
}
