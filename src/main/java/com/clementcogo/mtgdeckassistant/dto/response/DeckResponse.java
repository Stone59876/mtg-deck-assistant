package com.clementcogo.mtgdeckassistant.dto.response;

import com.clementcogo.mtgdeckassistant.entities.Deck;
import com.clementcogo.mtgdeckassistant.entities.DeckSlot;
import com.clementcogo.mtgdeckassistant.entities.Format;

import java.time.Instant;
import java.util.List;

public class DeckResponse {
    Long id;
    String name;
    Format format;
    Instant createdAt;
    DeckSlot commander;

    public DeckResponse(Long id, String name, Format format, Instant createdAt) {
        this.id = id;
        this.name = name;
        this.format = format;
        this.createdAt = createdAt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Format getFormat() {
        return format;
    }

    public void setFormat(Format format) {
        this.format = format;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public void setCommander(DeckSlot commander) {
        this.commander = commander;
    }

}
