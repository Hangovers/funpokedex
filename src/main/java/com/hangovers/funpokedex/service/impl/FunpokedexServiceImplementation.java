package com.hangovers.funpokedex.service.impl;

import static com.hangovers.funpokedex.Utils.badEgg;
import static com.hangovers.funpokedex.Utils.missingno;
import static io.micronaut.http.HttpStatus.NOT_FOUND;

import com.hangovers.funpokedex.clients.pokeapi.PokeapiClient;
import com.hangovers.funpokedex.clients.pokeapi.models.PokeApiResponse;
import com.hangovers.funpokedex.model.Pokemon;
import com.hangovers.funpokedex.service.FunpokedexService;
import io.micronaut.http.client.exceptions.HttpClientResponseException;
import jakarta.inject.Singleton;
import java.util.Objects;
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
   *
   * @param name pokemon's name
   * @return pokèmon data
   */
  public Publisher<Pokemon> getPokemon(String name) {
    log.info("Fetching data from pokeapi or cache for pokèmon name {}", name);
    return Mono.from(pokeapiClient.fetchPokemonSpecies(name))
        .map(PokeApiResponse::asPokemon)
        .onErrorResume(this::handleError);
  }

  /**
   * Method used to handle reactive errors that comes in throwable form gracefully. Actual error
   * investigations can be done using logs.
   *
   * @param t error throwable that will be logged
   * @return friendly response to the end user
   */
  private Mono<Pokemon> handleError(Throwable t) {
    if (Objects.requireNonNull(t) instanceof HttpClientResponseException e
        && e.getStatus() == NOT_FOUND) {
      log.error("Pokèmon not found");
      return Mono.just(missingno());
    }

    log.error("Error while retrieving pokèmon. {}", t.getMessage());
    return Mono.just(badEgg());
  }
}
