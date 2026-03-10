package com.clementcogo.mtgdeckassistant.dto.request;

import com.clementcogo.mtgdeckassistant.entities.DeckSlot;
import com.clementcogo.mtgdeckassistant.entities.Format;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.Instant;
import java.util.List;

public class CreateDeckRequest {
    @NotBlank
    String name;
    @NotNull
    Format format;

    public CreateDeckRequest(String name, Format format) {
        this.name = name;
        this.format = format;
    }

    public CreateDeckRequest() {}

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
}
