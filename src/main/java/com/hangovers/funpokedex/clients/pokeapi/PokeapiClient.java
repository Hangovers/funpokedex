package com.hangovers.funpokedex.clients.pokeapi;

import com.hangovers.funpokedex.clients.pokeapi.models.PokeApiResponse;
import io.micronaut.cache.annotation.CacheConfig;
import io.micronaut.cache.annotation.Cacheable;
import io.micronaut.core.async.annotation.SingleResult;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.QueryValue;
import io.micronaut.http.client.annotation.Client;
import org.reactivestreams.Publisher;

@CacheConfig("pokeapi")
@Client("${pokeapi.url}")
public interface PokeapiClient {

  @Get("/pokemon-species/{name}")
  @SingleResult
  @Cacheable
  Publisher<PokeApiResponse> fetchPokemonSpecies(@QueryValue String name);
}
