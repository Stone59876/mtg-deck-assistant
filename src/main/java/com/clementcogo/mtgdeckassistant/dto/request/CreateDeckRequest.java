package com.clementcogo.mtgdeckassistant.dto.request;

import com.clementcogo.mtgdeckassistant.enumeration.Format;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
public class CreateDeckRequest {
    @NotBlank
    @Size(max = 100)
    private String name;
    @NotNull
    private Format format;

    public String getName() {
        return name;
    }

    public Format getFormat() {
        return format;
    }
}
