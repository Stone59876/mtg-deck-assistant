package com.clementcogo.mtgdeckassistant.dto.request;

import com.clementcogo.mtgdeckassistant.enumeration.Format;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class UpdateDeckRequest {
    @Size(max = 100)
    @Pattern(regexp = ".*\\S.*", message = "Deck name cannot be blank")
    String name;
    Format format;

    public UpdateDeckRequest(String name, Format format) {
        this.name = name;
        this.format = format;
    }

    public UpdateDeckRequest() {
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
}
