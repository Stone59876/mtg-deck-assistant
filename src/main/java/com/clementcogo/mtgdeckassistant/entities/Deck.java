package com.clementcogo.mtgdeckassistant.entities;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "decks")
public class Deck {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Format format;

    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    @OneToMany(
            mappedBy = "deck",
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = FetchType.LAZY
    )
    private List<DeckSlot> slots = new ArrayList<>();

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "commander_slot_id")
    private DeckSlot commander;

    protected Deck() {
        // JPA
    }

    public Deck(String name, Format format) {
        this.name = name;
        this.format = format;
    }

    @PrePersist
    void prePersist() {
        this.createdAt = Instant.now();
    }

    // Helpers pour maintenir la relation bidirectionnelle correctement
    public void addSlot(DeckSlot slot) {
        slots.add(slot);
        slot.setDeck(this);
    }

    public void removeSlot(DeckSlot slot) {
        slots.remove(slot);
        slot.setDeck(null);
    }

    public void setCommander(DeckSlot commander) {
        this.commander = commander;
    }

    // Getters (et setters si tu veux, mais limite-les)
    public Long getId() { return id; }
    public String getName() { return name; }
    public Format getFormat() { return format; }
    public Instant getCreatedAt() { return createdAt; }
    public List<DeckSlot> getSlots() { return slots; }

    public void setName(String name) { this.name = name; }
    public void setFormat(Format format) { this.format = format; }

    public DeckSlot getCommander() {
        return commander;
    }
}
