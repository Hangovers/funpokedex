package com.hangovers.funpokedex.services.impl;

import static com.hangovers.funpokedex.Utils.badEgg;
import static com.hangovers.funpokedex.Utils.missingno;
import static io.micronaut.http.HttpStatus.NOT_FOUND;

import com.hangovers.funpokedex.clients.funtranslationsapi.FuntranslationsapiClient;
import com.hangovers.funpokedex.clients.funtranslationsapi.models.FuntranslationsapiResponse;
import com.hangovers.funpokedex.clients.funtranslationsapi.models.TranslationRequest;
import com.hangovers.funpokedex.clients.pokeapi.PokeapiClient;
import com.hangovers.funpokedex.clients.pokeapi.models.PokeApiResponse;
import com.hangovers.funpokedex.models.Pokemon;
import com.hangovers.funpokedex.services.FunpokedexService;
import io.micronaut.http.client.exceptions.HttpClientResponseException;
import jakarta.inject.Singleton;
import java.time.Duration;
import java.util.Objects;
import org.reactivestreams.Publisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@Singleton
public class FunpokedexServiceImplementation implements FunpokedexService {

  private final PokeapiClient pokeapiClient;
  private final FuntranslationsapiClient funtranslationsapiClient;

  private static final Logger log = LoggerFactory.getLogger(FunpokedexServiceImplementation.class);

  public FunpokedexServiceImplementation(
      PokeapiClient pokeapiClient, FuntranslationsapiClient funtranslationsapiClient) {
    this.pokeapiClient = pokeapiClient;
    this.funtranslationsapiClient = funtranslationsapiClient;
  }

  /**
   * Get Pokèmon data from pokeapi based on input name
   *
   * @param name pokemon's name
   * @return pokèmon data
   */
  public Mono<Pokemon> getPokemon(String name) {
    log.info("Fetching data from pokeapi or cache for pokèmon name {}", name);
    return Mono.from(pokeapiClient.fetchPokemonSpecies(name))
        .map(PokeApiResponse::asPokemon)
        .onErrorResume(this::handleError);
  }

  /**
   * Get Pokèmon data and then translate its description based on some criteria If something goes
   * wrong, returns pokèmon with normal description.
   *
   * @param name pokèmon's name
   * @return pokèmon data with translated description
   */
  public Publisher<Pokemon> getTranslatedPokemon(String name) {
    return getPokemon(name)
        .publishOn(Schedulers.boundedElastic())
        .map(
            pokemon -> {
              if (pokemon.name().equalsIgnoreCase("MissingNo.")
                  || pokemon.name().equalsIgnoreCase("Bad EGG")) return pokemon;

              log.info("fetching translated description for pokemon name {}", pokemon.name());
              String description = getTranslatedDescription(pokemon).block(Duration.ofSeconds(2L));
              log.info("translated description: {}", description);

              return new Pokemon(
                  pokemon.name(), description, pokemon.habitat(), pokemon.isLegendary());
            });
  }

  private Mono<String> getTranslatedDescription(Pokemon pokemon) {
    if (pokemon.isLegendary() || pokemon.habitat().equalsIgnoreCase("cave"))
      return funtranslationsapiClient
          .fetchYodaTranslations(new TranslationRequest(pokemon.description()))
          .map(FuntranslationsapiResponse::asTranslation);

    return funtranslationsapiClient
        .fetchShakespeareTranslations(new TranslationRequest(pokemon.description()))
        .map(FuntranslationsapiResponse::asTranslation);
  }

  /**
   * Method used to handle reactive errors that comes in throwable form gracefully. Actual error
   * investigations can be done using logs.
   *
   * @param t error throwable that will be logged
   * @return friendly response to the end user
   */
  private Mono<Pokemon> handleError(Throwable t) {
    log.debug(t.getMessage());

    if (Objects.requireNonNull(t) instanceof HttpClientResponseException e
        && e.getStatus() == NOT_FOUND) {

      log.error("Pokèmon not found");
      return Mono.just(missingno());
    }

    log.error("Error while retrieving pokèmon. {}", t.getMessage());
    return Mono.just(badEgg());
  }
}
