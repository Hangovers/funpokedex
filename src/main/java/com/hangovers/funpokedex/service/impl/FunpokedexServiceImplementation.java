package com.hangovers.funpokedex.service.impl;

import com.hangovers.funpokedex.clients.pokeapi.PokeapiClient;
import com.hangovers.funpokedex.clients.pokeapi.models.PokeApiResponse;
import com.hangovers.funpokedex.model.Pokemon;
import com.hangovers.funpokedex.service.FunpokedexService;
import jakarta.inject.Singleton;
import org.reactivestreams.Publisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Mono;

@Singleton
public class FunpokedexServiceImplementation implements FunpokedexService {

    private final PokeapiClient pokeapiClient;

    private static final Logger log = LoggerFactory.getLogger(FunpokedexServiceImplementation.class);

    public FunpokedexServiceImplementation(PokeapiClient pokeapiClient) {
        this.pokeapiClient = pokeapiClient;
    }

    /**
     * Get Pokèmon data from pokeapi based on input name
     * @param name pokemon's name
     * @return pokemon data
     */
    public Publisher<Pokemon> getPokemon(String name) {
        log.info("Fetching data from pokeapi or cache for pokemon name {}", name);
        return Mono.from(pokeapiClient.fetchPokemonSpecies(name)).map(PokeApiResponse::asPokemon);
    }
}
