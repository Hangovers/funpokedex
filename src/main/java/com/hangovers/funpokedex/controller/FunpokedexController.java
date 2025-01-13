package com.hangovers.funpokedex.controller;

import com.hangovers.funpokedex.models.Pokemon;
import com.hangovers.funpokedex.services.FunpokedexService;
import io.micronaut.core.async.annotation.SingleResult;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.PathVariable;
import io.micronaut.retry.annotation.Retryable;
import org.reactivestreams.Publisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Controller("/pokemon")
public class FunpokedexController {

  private final FunpokedexService funpokedexService;
  private static final Logger log = LoggerFactory.getLogger(FunpokedexController.class);

  public FunpokedexController(FunpokedexService funpokedexService) {
    this.funpokedexService = funpokedexService;
  }

  @Get("/{name}")
  @SingleResult
  @Retryable(attempts = "3", delay = "1s")
  Publisher<Pokemon> getPokemon(@PathVariable String name) {
    log.info("Get pokèmon for name {}", name);
    return funpokedexService.getPokemon(name);
  }

  @Get("translated/{name}")
  @SingleResult
  @Retryable(attempts = "3", delay = "1s")
  Publisher<Pokemon> getTranslatedPokemon(@PathVariable String name) {
    log.info("Get translated pokèmon data for name {}", name);
    return funpokedexService.getTranslatedPokemon(name);
  }
}
