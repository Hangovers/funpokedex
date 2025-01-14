package com.hangovers.funpokedex.services;

import com.hangovers.funpokedex.models.Pokemon;
import org.reactivestreams.Publisher;

public interface FunpokedexService {
    Publisher<Pokemon> getPokemon(String name);

    Publisher<Pokemon> getTranslatedPokemon(String name);
}
