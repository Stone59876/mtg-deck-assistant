package com.clementcogo.mtgdeckassistant.dto.response;

import com.clementcogo.mtgdeckassistant.enumeration.Format;

import java.time.Instant;

public class DeckResponse {
    Long id;
    String name;
    Format format;
    Instant createdAt;
    String commander;
    Instant lastUpdated;

    public DeckResponse(Long id, String name, Format format, Instant createdAt, Instant lastUpdated) {
        this.id = id;
        this.name = name;
        this.format = format;
        this.createdAt = createdAt;
        this.lastUpdated = lastUpdated;
    }

    public DeckResponse(Long id, String name, Format format, Instant createdAt, String commander, Instant lastUpdated) {
        this.id = id;
        this.name = name;
        this.format = format;
        this.createdAt = createdAt;
        this.commander = commander;
        this.lastUpdated = lastUpdated;
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

    public String getCommander() {
        return commander;
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

    public void setCommander(String commander) {
        this.commander = commander;
    }

    public void setLastUpdated(Instant lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    public Instant getLastUpdated() {
        return lastUpdated;
    }

}
