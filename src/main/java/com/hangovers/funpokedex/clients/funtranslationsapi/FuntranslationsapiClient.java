package com.hangovers.funpokedex.clients.funtranslationsapi;

import static io.micronaut.http.HttpHeaders.ACCEPT;

import com.hangovers.funpokedex.clients.funtranslationsapi.models.FuntranslationsapiResponse;
import com.hangovers.funpokedex.clients.funtranslationsapi.models.TranslationRequest;
import io.micronaut.cache.annotation.CacheConfig;
import io.micronaut.cache.annotation.Cacheable;
import io.micronaut.core.async.annotation.SingleResult;
import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Header;
import io.micronaut.http.annotation.Post;
import io.micronaut.http.client.annotation.Client;
import reactor.core.publisher.Mono;

@CacheConfig("translations")
@Client("${funtranslationsapi.url}")
@Header(name = ACCEPT, value = "application/json")
public interface FuntranslationsapiClient {

  @Post("/yoda")
  @SingleResult
  @Cacheable
  Mono<FuntranslationsapiResponse> fetchYodaTranslations(@Body TranslationRequest text);

  @Post("/shakespeare")
  @SingleResult
  @Cacheable
  Mono<FuntranslationsapiResponse> fetchShakespeareTranslations(@Body TranslationRequest text);
}
