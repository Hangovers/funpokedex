package com.hangovers.funpokedex.controller;

import static com.hangovers.funpokedex.TestConstants.*;
import static java.nio.charset.StandardCharsets.UTF_8;
import static org.junit.jupiter.api.Assertions.assertEquals;

import ch.qos.logback.classic.LoggerContext;
import com.hangovers.funpokedex.TestUtils;
import com.hangovers.funpokedex.model.Pokemon;
import io.micronaut.context.ApplicationContext;
import io.micronaut.context.annotation.Requires;
import io.micronaut.core.io.ResourceLoader;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.client.BlockingHttpClient;
import io.micronaut.http.client.HttpClient;
import io.micronaut.http.server.exceptions.InternalServerException;
import io.micronaut.http.server.exceptions.NotFoundException;
import io.micronaut.runtime.server.EmbeddedServer;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import jakarta.inject.Inject;
import java.io.IOException;
import java.util.Collections;
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

  @Inject static EmbeddedServer pokeapi;

  @Inject static EmbeddedServer embeddedServer;

  @BeforeAll
  public static void startServer() {
    System.setProperty("logback.configurationFile", "logback-test.xml");
    LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();
    loggerContext.reset();

    pokeapi =
        ApplicationContext.run(EmbeddedServer.class, Map.of("spec.name", "PokeapiClientTest"));

    embeddedServer =
        ApplicationContext.run(
            EmbeddedServer.class,
            Collections.singletonMap("pokeapi.url", "http://localhost:" + pokeapi.getPort()));
  }

  @AfterAll
  public static void stopServer() {
    embeddedServer.close();
    pokeapi.close();
  }

  /** This test is to see a response in normal circumstances. */
  @Test
  @Order(1)
  @Timeout(5)
  void verifyMewtwoCanBeFetchedWithHttpClientNoCacheHit() {
    try (HttpClient httpClient =
        embeddedServer
            .getApplicationContext()
            .createBean(HttpClient.class, embeddedServer.getURL())) {
      BlockingHttpClient client = httpClient.toBlocking();
      assertions(client, MEWTWO);
    }
  }

  /** Test of cache behaviour. Cache behaviour can be observed from logs. */
  @Test
  @Order(2)
  @Timeout(1)
  void verifyMewtwoCanBeFetchedWithHttpClientHitsCache() {
    try (HttpClient httpClient =
        embeddedServer
            .getApplicationContext()
            .createBean(HttpClient.class, embeddedServer.getURL())) {
      BlockingHttpClient client = httpClient.toBlocking();
      assertions(client, MEWTWO);
    }
  }

  /** Testing behaviour of application when pokeapi responds with 404 */
  @Test
  @Order(3)
  void verifyPokeApiPokemonNotFound() {
    try (HttpClient httpClient =
        embeddedServer
            .getApplicationContext()
            .createBean(HttpClient.class, embeddedServer.getURL())) {
      BlockingHttpClient client = httpClient.toBlocking();
      assertions(client, DOES_NOT_EXIST);
    }
  }

  /** Testing behaviour of application when something unexpected happens */
  @Test
  @Order(4)
  void verifyPokeApiInternalServerError() {
    try (HttpClient httpClient =
        embeddedServer
            .getApplicationContext()
            .createBean(HttpClient.class, embeddedServer.getURL())) {
      BlockingHttpClient client = httpClient.toBlocking();
      assertions(client, BAD_EGG);
    }
  }

  /**
   * Testing behaviour of application if for some reason a json with a different structure is
   * returned from pokeapi
   */
  @Test
  @Order(5)
  void verifyMalformedJsonInResponseBehaviour() {
    try (HttpClient httpClient =
        embeddedServer
            .getApplicationContext()
            .createBean(HttpClient.class, embeddedServer.getURL())) {
      BlockingHttpClient client = httpClient.toBlocking();
      assertions(client, MALFORMED);
    }
  }

  private static void assertions(BlockingHttpClient client, String getParameter) {
    HttpRequest<Object> request = HttpRequest.GET("/pokemon/" + getParameter);

    switch (getParameter) {
      case MEWTWO -> {
        HttpResponse<Pokemon> response = client.exchange(request, Pokemon.class);
        assertEquals(HttpStatus.OK, response.getStatus());
        assertEquals(response.body(), TestUtils.mewtwo());
      }
      case DOES_NOT_EXIST -> {
        HttpResponse<Pokemon> response = client.exchange(request, Pokemon.class);
        assertEquals(HttpStatus.OK, response.getStatus());
        assertEquals(response.body(), TestUtils.missingno());
      }
      default -> {
        HttpResponse<Pokemon> response = client.exchange(request, Pokemon.class);
        assertEquals(HttpStatus.OK, response.getStatus());
        assertEquals(response.body(), TestUtils.badEgg());
      }
    }
  }

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
        case MEWTWO -> returnJsonInputStream("mewtwo.json");
        case DOES_NOT_EXIST -> throw new NotFoundException();
        case MALFORMED -> returnJsonInputStream("malformed.json");
        default -> throw new InternalServerException(INTERNAL_SERVER_ERROR);
      };
    }

    private Optional<String> returnJsonInputStream(String path) {
      return resourceLoader
          .getResourceAsStream(path)
          .flatMap(
              inputStream -> {
                try {
                  return Optional.of(new String(inputStream.readAllBytes(), UTF_8));
                } catch (IOException e) {
                  return Optional.empty();
                }
              });
    }
  }
}
