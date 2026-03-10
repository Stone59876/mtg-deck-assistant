package com.clementcogo.mtgdeckassistant.entities;

import jakarta.persistence.*;


@Entity
@Table(
        name = "deck_slots",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_deck_card",
                columnNames = {"deck_id", "card_name"}
        )
)
public class DeckSlot {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "deck_id", nullable = false)
    private Deck deck;

    @Column(name = "card_name", nullable = false)
    private String cardName;

    @Column(nullable = false)
    private int qty;

    protected DeckSlot() {
        // JPA
    }

    public DeckSlot(String cardName, int qty) {
        this.cardName = cardName;
        this.qty = qty;
    }

    public Long getId() { return id; }
    public Deck getDeck() { return deck; }
    public String getCardName() { return cardName; }
    public int getQty() { return qty; }

    void setDeck(Deck deck) { this.deck = deck; } // package-private ou public si besoin
    public void setCardName(String cardName) { this.cardName = cardName; }
    public void setQty(int qty) { this.qty = qty; }

}
