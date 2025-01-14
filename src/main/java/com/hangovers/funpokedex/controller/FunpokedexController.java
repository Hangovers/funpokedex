package com.hangovers.funpokedex.controller;

import com.hangovers.funpokedex.models.Pokemon;
import com.hangovers.funpokedex.services.FunpokedexService;
import io.micronaut.core.async.annotation.SingleResult;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.PathVariable;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.reactivestreams.Publisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Controller("/pokemon")
@OpenAPIDefinition(
        info =
                @Info(
                        title = "Funpokedex",
                        version = "0.1",
                        description = "Returns pokemon normal data or with translated description."))
public class FunpokedexController {

    private final FunpokedexService funpokedexService;
    private static final Logger log = LoggerFactory.getLogger(FunpokedexController.class);

    public FunpokedexController(FunpokedexService funpokedexService) {
        this.funpokedexService = funpokedexService;
    }

    @Get(uri = "/{name}", produces = MediaType.APPLICATION_JSON)
    @SingleResult
    @Operation(
            summary = "Get pokèmon details",
            description = "Given a pokèmon name, calls pokeapi and returns brief data about it.",
            responses = {
                @ApiResponse(
                        responseCode = "200",
                        description = "Correct pokèmon details or explanatory errors under the form of a Pokèmon.",
                        content =
                                @Content(
                                        mediaType = "application/json",
                                        schema = @Schema(implementation = Pokemon.class))),
                @ApiResponse(responseCode = "500", description = "Internal Server Error")
            })
    Publisher<Pokemon> getPokemon(
            @PathVariable @Parameter(description = "Name of the Pokémon", example = "mewtwo") String name) {
        log.info("Get pokèmon for name {}", name);
        return funpokedexService.getPokemon(name);
    }

    @Get(uri = "translated/{name}", produces = MediaType.APPLICATION_JSON)
    @SingleResult
    @Operation(
            summary = "Get pokèmon details with translated description",
            description =
                    "Given a pokèmon name, calls pokeapi and fetch data about it. After that, calls funtranslationsapi based on pokèmon's data and returns the pokèmon with a translated description.",
            responses = {
                @ApiResponse(
                        responseCode = "200",
                        description =
                                "Correct pokèmon details with translated description or explanatory errors under the form of a Pokèmon.",
                        content =
                                @Content(
                                        mediaType = "application/json",
                                        schema = @Schema(implementation = Pokemon.class))),
                @ApiResponse(responseCode = "500", description = "Internal Server Error")
            })
    Publisher<Pokemon> getTranslatedPokemon(@PathVariable @Parameter(description = "Name of the Pokémon") String name) {
        log.info("Get translated pokèmon data for name {}", name);
        return funpokedexService.getTranslatedPokemon(name);
    }
}
