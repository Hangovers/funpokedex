package com.hangovers.funpokedex.controller;

import static com.hangovers.funpokedex.TestConstants.*;
import static com.hangovers.funpokedex.TestUtils.*;
import static java.nio.charset.StandardCharsets.UTF_8;
import static org.junit.jupiter.api.Assertions.assertEquals;

import ch.qos.logback.classic.LoggerContext;
import com.hangovers.funpokedex.clients.funtranslationsapi.models.TranslationRequest;
import com.hangovers.funpokedex.models.Pokemon;
import io.micronaut.context.ApplicationContext;
import io.micronaut.context.annotation.Requires;
import io.micronaut.core.io.ResourceLoader;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.Post;
import io.micronaut.http.client.BlockingHttpClient;
import io.micronaut.http.client.HttpClient;
import io.micronaut.http.server.exceptions.InternalServerException;
import io.micronaut.http.server.exceptions.NotFoundException;
import io.micronaut.runtime.server.EmbeddedServer;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import jakarta.inject.Inject;
import java.io.IOException;
import java.util.Map;
import java.util.Optional;
import org.junit.jupiter.api.*;
import org.slf4j.LoggerFactory;

/**
 * The purpose of this test class is to test overall application behaviour as if it was a real world
 * run. Embedded servers are created to allow tests to response with specific json(s) and avoid
 * loading real servers and incur in issues such as rate limiting. Caching is also tested.
 */
@MicronautTest(startApplication = false, environments = "test")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class FunpokedexControllerTest {

    @Inject
    static EmbeddedServer pokeapi;

    @Inject
    static EmbeddedServer funtranslationsapi;

    @Inject
    static EmbeddedServer embeddedServer;

    @BeforeAll
    public static void startServer() {

        // Set logback to use test configuration
        System.setProperty("logback.configurationFile", "logback-test.xml");
        LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();
        loggerContext.reset();

        pokeapi = ApplicationContext.run(EmbeddedServer.class, Map.of("spec.name", "PokeapiClientTest"));

        funtranslationsapi =
                ApplicationContext.run(EmbeddedServer.class, Map.of("spec.name", "FuntranslationsapiClientTest"));

        embeddedServer = ApplicationContext.run(
                EmbeddedServer.class,
                Map.of(
                        "pokeapi.url",
                        "http://localhost:" + pokeapi.getPort(),
                        "funtranslationsapi.url",
                        "http://localhost:" + funtranslationsapi.getPort()));
    }

    @AfterAll
    public static void stopServer() {
        funtranslationsapi.close();
        embeddedServer.close();
        pokeapi.close();
    }

    /**
     * This test is used to see a response in normal circumstances and proper input. Caching is also
     * tested by repeating the test twice.
     */
    @RepeatedTest(2)
    @Order(1)
    void verifyMewtwoCanBeFetchedWithHttpClientNoCacheHit() {
        try (HttpClient httpClient =
                embeddedServer.getApplicationContext().createBean(HttpClient.class, embeddedServer.getURL())) {
            BlockingHttpClient client = httpClient.toBlocking();
            pokeapiAssertions(client, MEWTWO);
        }
    }

    /** Testing behaviour of application when pokeapi responds with 404 */
    @Test
    @Order(2)
    void verifyPokeApiPokemonNotFound() {
        try (HttpClient httpClient =
                embeddedServer.getApplicationContext().createBean(HttpClient.class, embeddedServer.getURL())) {
            BlockingHttpClient client = httpClient.toBlocking();
            pokeapiAssertions(client, DOES_NOT_EXIST);
        }
    }

    /** Testing behaviour of application when something unexpected happens */
    @Test
    @Order(3)
    void verifyPokeApiInternalServerError() {
        try (HttpClient httpClient =
                embeddedServer.getApplicationContext().createBean(HttpClient.class, embeddedServer.getURL())) {
            BlockingHttpClient client = httpClient.toBlocking();
            pokeapiAssertions(client, BAD_EGG);
        }
    }

    /**
     * Testing translations endpoint with proper input for shakespeare endpoint. caching is tested by
     * repeating the test twice.
     */
    @RepeatedTest(2)
    @Order(4)
    void fetchTranslatedDescriptionForFlygonUsingShakespeare() {
        try (HttpClient httpClient =
                embeddedServer.getApplicationContext().createBean(HttpClient.class, embeddedServer.getURL())) {
            BlockingHttpClient client = httpClient.toBlocking();
            funtranslationsapiAssertions(client, FLYGON);
        }
    }

    /**
     * Testing translations endpoint with proper input for yoda endpoint. caching is tested by
     * repeating the test twice.
     */
    @Test
    @Order(5)
    void fetchTranslatedDescriptionForMewtwoUsingYoda() {
        try (HttpClient httpClient =
                embeddedServer.getApplicationContext().createBean(HttpClient.class, embeddedServer.getURL())) {
            BlockingHttpClient client = httpClient.toBlocking();
            funtranslationsapiAssertions(client, FLYGON);
        }
    }

    /**
     * Testing translations endpoint in the case that an error is in the case. We expect the standard
     * description here.
     */
    @Test
    @Order(6)
    void fetchTranslatedDescriptionForCharizardButGetStandardDescription() {
        try (HttpClient httpClient =
                embeddedServer.getApplicationContext().createBean(HttpClient.class, embeddedServer.getURL())) {
            BlockingHttpClient client = httpClient.toBlocking();
            funtranslationsapiAssertions(client, CHARIZARD);
        }
    }

    /**
     * Testing that translations endpoint does not get called when pokeapi result is missingno. We
     * expect missingno here.
     */
    @Test
    @Order(7)
    void fetchTranslatedDescriptionForNotFoundPokemonAndSkipFuntranslationsapiCall() {
        try (HttpClient httpClient =
                embeddedServer.getApplicationContext().createBean(HttpClient.class, embeddedServer.getURL())) {
            BlockingHttpClient client = httpClient.toBlocking();
            funtranslationsapiAssertions(client, DOES_NOT_EXIST);
        }
    }

    /**
     * Testing that translations endpoint does not get called when pokeapi result is Bad EGG. We
     * expect Bad EGG here.
     */
    @Test
    @Order(8)
    void fetchTranslatedDescriptionWhenPokeapiSentUnexpectedErrorAndSkipFuntranslationsapiCall() {
        try (HttpClient httpClient =
                embeddedServer.getApplicationContext().createBean(HttpClient.class, embeddedServer.getURL())) {
            BlockingHttpClient client = httpClient.toBlocking();
            funtranslationsapiAssertions(client, BAD_EGG);
        }
    }

    /**
     * Testing that a pokèmon that does not exist due to its name not possibly being a pokèmon's name does not trigger get calls to pokeapi.
     * mockserver does not even need mock call
     * expect missingno here.
     */
    @Test
    @Order(9)
    void fetchPokemonWithInvalidNameThatFailsMatchingWithRegularExpression() {
        try (HttpClient httpClient =
                embeddedServer.getApplicationContext().createBean(HttpClient.class, embeddedServer.getURL())) {
            BlockingHttpClient client = httpClient.toBlocking();
            pokeapiAssertions(client, POKEMON_THAT_CANNOT_EXIST);
        }
    }

    /**
     * Testing that a pokèmon that has null as habitat works anyway.
     * expect porygon-z's data here
     */
    @Test
    @Order(10)
    void fetchPokemonWithoutHabitatButGetAValidResponseAnyway() {
        try (HttpClient httpClient =
                embeddedServer.getApplicationContext().createBean(HttpClient.class, embeddedServer.getURL())) {
            BlockingHttpClient client = httpClient.toBlocking();
            pokeapiAssertions(client, PORYGONZ);
        }
    }

    /**
     * Helper function that groups assertions for the pokeapi endpoint.
     *
     * @param client funpokedex client
     * @param pokemonName pokèmon's name
     */
    private static void pokeapiAssertions(BlockingHttpClient client, String pokemonName) {
        HttpRequest<Object> request = HttpRequest.GET("/pokemon/" + pokemonName);

        switch (pokemonName) {
            case MEWTWO -> {
                HttpResponse<Pokemon> response = client.exchange(request, Pokemon.class);
                assertEquals(HttpStatus.OK, response.getStatus());
                assertEquals(response.body(), mewtwo());
            }
            case PORYGONZ -> {
                HttpResponse<Pokemon> response = client.exchange(request, Pokemon.class);
                assertEquals(HttpStatus.OK, response.getStatus());
                assertEquals(response.body(), porygonz());
            }
            case DOES_NOT_EXIST, POKEMON_THAT_CANNOT_EXIST -> {
                HttpResponse<Pokemon> response = client.exchange(request, Pokemon.class);
                assertEquals(HttpStatus.OK, response.getStatus());
                assertEquals(response.body(), missingno());
            }
            default -> {
                HttpResponse<Pokemon> response = client.exchange(request, Pokemon.class);
                assertEquals(HttpStatus.OK, response.getStatus());
                assertEquals(response.body(), badEgg());
            }
        }
    }

    /**
     * Helper function that groups assertions for the pokeapi endpoint.
     *
     * @param client funpokedex client
     * @param pokemonName pokèmon's name
     */
    private static void funtranslationsapiAssertions(BlockingHttpClient client, String pokemonName) {
        HttpRequest<Object> request = HttpRequest.GET("/pokemon/translated/" + pokemonName);

        switch (pokemonName) {
            case MEWTWO -> {
                HttpResponse<Pokemon> response = client.exchange(request, Pokemon.class);
                assertEquals(HttpStatus.OK, response.getStatus());
                assertEquals(response.body(), mewtwoTranslated());
            }
            case FLYGON -> {
                HttpResponse<Pokemon> response = client.exchange(request, Pokemon.class);
                assertEquals(HttpStatus.OK, response.getStatus());
                assertEquals(response.body(), flygonTranslated());
            }
            case CHARIZARD -> {
                HttpResponse<Pokemon> response = client.exchange(request, Pokemon.class);
                assertEquals(HttpStatus.OK, response.getStatus());
                assertEquals(response.body(), charizard());
            }
            case DOES_NOT_EXIST -> {
                HttpResponse<Pokemon> response = client.exchange(request, Pokemon.class);
                assertEquals(HttpStatus.OK, response.getStatus());
                assertEquals(response.body(), missingno());
            }
            default -> {
                HttpResponse<Pokemon> response = client.exchange(request, Pokemon.class);
                assertEquals(HttpStatus.OK, response.getStatus());
                assertEquals(response.body(), badEgg());
            }
        }
    }

    /** Mock server of the pokeapi client */
    @Requires(property = "spec.name", value = "PokeapiClientTest")
    @Controller
    static class PokeApiMockClient {

        private final ResourceLoader resourceLoader;

        PokeApiMockClient(ResourceLoader resourceLoader) {
            this.resourceLoader = resourceLoader;
        }

        @Get("/pokemon-species/{name}")
        Optional<?> getPokemon(String name) {
            return switch (name) {
                case MEWTWO -> returnJsonInputStream("responses/mewtwo.json");
                case FLYGON -> returnJsonInputStream("responses/flygon.json");
                case CHARIZARD -> returnJsonInputStream("responses/charizard.json");
                case PORYGONZ -> returnJsonInputStream("responses/porygon-z.json");
                case DOES_NOT_EXIST -> throw new NotFoundException();
                default -> throw new InternalServerException(INTERNAL_SERVER_ERROR);
            };
        }

        private Optional<String> returnJsonInputStream(String path) {
            return resourceLoader.getResourceAsStream(path).flatMap(inputStream -> {
                try {
                    return Optional.of(new String(inputStream.readAllBytes(), UTF_8));
                } catch (IOException e) {
                    return Optional.empty();
                }
            });
        }
    }

    /** Mock server of the funtranslationsapi client */
    @Requires(property = "spec.name", value = "FuntranslationsapiClientTest")
    @Controller
    static class FuntranslationsapiMockClient {

        private final ResourceLoader resourceLoader;

        FuntranslationsapiMockClient(ResourceLoader resourceLoader) {
            this.resourceLoader = resourceLoader;
        }

        @Post("/yoda")
        Optional<?> fetchYodaTranslations(@Body TranslationRequest request) {
            if (request.text().equalsIgnoreCase(MEWTWO_DESCRIPTION))
                return returnJsonInputStream("responses/mewtwoTranslatedDescription.json");
            return returnJsonInputStream("responses/failedTranslation.json");
        }

        @Post("/shakespeare")
        Optional<?> fetchShakespeareTranslations(@Body TranslationRequest request) {
            if (request.text().contains(FLYGON.toUpperCase()))
                return returnJsonInputStream("responses/flygonTranslatedDescription.json");
            return returnJsonInputStream("responses/failedTranslation.json");
        }

        private Optional<String> returnJsonInputStream(String path) {
            return resourceLoader.getResourceAsStream(path).flatMap(inputStream -> {
                try {
                    return Optional.of(new String(inputStream.readAllBytes(), UTF_8));
                } catch (IOException e) {
                    return Optional.empty();
                }
            });
        }
    }
}
