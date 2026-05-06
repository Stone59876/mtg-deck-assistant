package com.clementcogo.mtgdeckassistant.integration.gemini.model;

/**
 * Modèle “brut” représentant UNE requête Scryfall suggérée par Gemini.
 *
 * Important :
 * - Cette classe vit côté integration.gemini.model car elle reflète le JSON attendu de Gemini.
 * - Elle ne contient pas la liste de cartes (cards) : Gemini ne doit renvoyer que le plan de recherche,
 *   l’enrichissement se fait ensuite via Scryfall.
 *
 * Champs :
 * - title   : titre lisible
 * - reason  : explication
 * - rawQuery: requête Scryfall brute
 * - order   : tri souhaité (souvent "edhrec")
 */

public class RawScryfallQuery {


        String title;
        String reason;
        String rawQuery;
        String order;

        public RawScryfallQuery(String title, String reason, String rawQuery, String order) {
            this.title = title;
            this.reason = reason;
            this.rawQuery = rawQuery;
            this.order = order;
        }

        public RawScryfallQuery() {
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getReason() {
            return reason;
        }

        public void setReason(String reason) {
            this.reason = reason;
        }

        public String getRawQuery() {
            return rawQuery;
        }

        public void setRawQuery(String rawQuery) {
            this.rawQuery = rawQuery;
        }

        public String getOrder() {
            return order;
        }

        public void setOrder(String order) {
            this.order = order;
        }

    @Override
    public String toString() {
        return "RawScryfallQuery{" +
                "title='" + title + '\'' +
                ", reason='" + reason + '\'' +
                ", rawQuery='" + rawQuery + '\'' +
                ", order='" + order + '\'' +
                '}';
    }
}

