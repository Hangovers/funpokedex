package com.hangovers.funpokedex.service;

import com.hangovers.funpokedex.clients.pokeapi.PokeapiClient;
import com.hangovers.funpokedex.clients.pokeapi.models.PokeApiResponse;
import com.hangovers.funpokedex.model.Pokemon;
import jakarta.inject.Singleton;
import org.reactivestreams.Publisher;
import reactor.core.publisher.Mono;

@Singleton

public class FunpokedexServiceImplementation implements FunpokedexService {

    private final PokeapiClient pokeApiClient;

    public FunpokedexServiceImplementation(PokeapiClient pokeApiClient) {
        this.pokeApiClient = pokeApiClient;
    }

    /**
     * Get Pokèmon data from pokeapi based on input name
     * @param name pokemon's name
     * @return pokemon data
     */
    public Publisher<Pokemon> getPokemon(String name) {
        return Mono.from(pokeApiClient.fetchPokemonSpecies(name)).map(
                PokeApiResponse::asPokemon
        );
    }
}
