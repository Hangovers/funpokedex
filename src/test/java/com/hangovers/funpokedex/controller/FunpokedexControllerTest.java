package com.hangovers.funpokedex.controller;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.junit.jupiter.api.Assertions.assertEquals;

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
import io.micronaut.runtime.server.EmbeddedServer;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import jakarta.inject.Inject;
import java.io.IOException;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import org.junit.jupiter.api.*;

/**
 * The purpose of this test class is to test overall application behaviour as if it was a real world
 * run. Embedded servers are created to allow tests to response with specific jsons and avoid
 * loading real servers and incur in issues such as rate limiting. Caching is also tested.
 */
@MicronautTest(startApplication = false)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class FunpokedexControllerTest {

  @Inject static EmbeddedServer pokeapi;

  @Inject static EmbeddedServer embeddedServer;

  @BeforeAll
  public static void startServer() {
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

  @Test
  @Order(1)
  @Timeout(3)
  void verifyMewtwoCanBeFetchedWithHttpClientNoCacheHit() {
    try (HttpClient httpClient =
        embeddedServer
            .getApplicationContext()
            .createBean(HttpClient.class, embeddedServer.getURL())) {
      BlockingHttpClient client = httpClient.toBlocking();
      assertMewtwo(client);
    }
  }

  @Test
  @Order(2)
  @Timeout(1)
  void verifyMewtwoCanBeFetchedWithHttpClientHitsCache() {
    try (HttpClient httpClient =
        embeddedServer
            .getApplicationContext()
            .createBean(HttpClient.class, embeddedServer.getURL())) {
      BlockingHttpClient client = httpClient.toBlocking();
      assertMewtwo(client);
    }
  }

  private static void assertMewtwo(BlockingHttpClient client) {
    HttpRequest<Object> request = HttpRequest.GET("/pokemon/mewtwo");

    HttpResponse<Pokemon> response = client.exchange(request, Pokemon.class);

    assertEquals(HttpStatus.OK, response.getStatus());
    assertEquals(response.body(), getMewtwo());
  }

  private static Pokemon getMewtwo() {
    return new Pokemon(
        "mewtwo",
        "It was created by a scientist after years of horrific gene splicing and DNA engineering experiments.",
        "rare",
        true);
  }

  @Requires(property = "spec.name", value = "PokeapiClientTest")
  @Controller
  static class PokeApiMockClient {

    private final ResourceLoader resourceLoader;

    PokeApiMockClient(ResourceLoader resourceLoader) {
      this.resourceLoader = resourceLoader;
    }

    @Get("/pokemon-species/mewtwo")
    Optional<String> getPokemon() {
      return resourceLoader
          .getResourceAsStream("mewtwo.json")
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
