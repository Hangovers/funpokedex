package com.hangovers.funpokedex.models;

import io.micronaut.serde.annotation.Serdeable;
import io.swagger.v3.oas.annotations.media.Schema;

@Serdeable
@Schema(name = "Pokemon", description = "Represents brief details about a Pokémon.")
public record Pokemon(
        @Schema(description = "Pokèmon's name", example = "mewtwo") String name,
        @Schema(
                        description = "Pokèmon's description",
                        example =
                                "It was created by a scientist after years of horrific gene splicing and DNA engineering experiments.")
                String description,
        @Schema(description = "Pokèmon's habitat", example = "rare") String habitat,
        @Schema(description = "Pokèmon's legendary status", example = "true") boolean isLegendary) {}
