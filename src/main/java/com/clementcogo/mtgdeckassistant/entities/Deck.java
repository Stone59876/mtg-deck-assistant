package com.clementcogo.mtgdeckassistant.entities;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Entity
@Table(name = "decks")
public class Deck {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Format format;

    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    @Column(nullable = false, updatable = true)
    private Instant updatedAt;

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
        Instant now = Instant.now();
        this.createdAt = now;
        this.updatedAt = now;
    }

    // Helpers pour maintenir la relation bidirectionnelle correctement
    public void addSlot(DeckSlot slot) {
        slots.add(slot);
        slot.setDeck(this);
        setUpdatedAt();
    }

    public void removeSlot(DeckSlot slot) {
        slots.remove(slot);
        slot.setDeck(null);
        setUpdatedAt();
    }

    public void setCommander(DeckSlot commander) {
        this.commander = commander; setUpdatedAt();
    }

    // Getters (et setters si tu veux, mais limite-les)
    public Long getId() { return id; }
    public String getName() { return name; }
    public Format getFormat() { return format; }
    public Instant getCreatedAt() { return createdAt; }
    public List<DeckSlot> getSlots() { return slots; }

    public void setName(String name) { this.name = name; setUpdatedAt(); }
    public void setFormat(Format format) { this.format = format; setUpdatedAt(); }

    public DeckSlot getCommander() {
        return commander;
    }

    public List<String> getCardNames() {
        return this.slots.stream().map(DeckSlot::getCardName).toList();
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt() {
        this.updatedAt = Instant.now();
    }
}
