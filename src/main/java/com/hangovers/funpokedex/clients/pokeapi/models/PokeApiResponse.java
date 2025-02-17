package com.hangovers.funpokedex.clients.pokeapi.models;

import com.hangovers.funpokedex.Utils;
import com.hangovers.funpokedex.models.Pokemon;
import io.micronaut.serde.annotation.Serdeable;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** Model for response from the pokeapi client. */
@Serdeable
public record PokeApiResponse(
        String name, List<FlavorTextEntry> flavor_text_entries, Habitat habitat, boolean is_legendary) {
    private static final Logger log = LoggerFactory.getLogger(PokeApiResponse.class);

    /** Mapper method to convert pokeapi fetched data into the proper response */
    public Pokemon asPokemon() {
        log.info("Mapping {} into proper response", this.name);

        // There are some pokèmon with no habitat such as porygon-z. This line covers that case.
        String habitat = this.habitat == null ? "Unknown" : this.habitat.name();

        return new Pokemon(this.name(), getEnglishDescription(), habitat, this.is_legendary());
    }

    /**
     * Helper method to get a single english description from the api response
     *
     * @return the english description
     */
    private String getEnglishDescription() {
        log.info("Getting english description for pokemon {}", this.name);
        return this.flavor_text_entries.stream()
                .filter(f -> f.language().name().equalsIgnoreCase("en"))
                .findFirst()
                .map(FlavorTextEntry::flavor_text)
                .map(Utils::handleSpecialCharactersInPokeapiFlavorTexts)
                .orElse("No description available.");
    }
}
