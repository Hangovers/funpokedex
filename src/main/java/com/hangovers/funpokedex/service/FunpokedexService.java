package com.hangovers.funpokedex.service;

import com.hangovers.funpokedex.model.Pokemon;
import org.reactivestreams.Publisher;

public interface FunpokedexService {
  Publisher<Pokemon> getPokemon(String name);
}
