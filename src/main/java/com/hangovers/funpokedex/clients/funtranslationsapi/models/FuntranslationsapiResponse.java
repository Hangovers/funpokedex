package com.hangovers.funpokedex.clients.funtranslationsapi.models;

import io.micronaut.serde.annotation.Serdeable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Serdeable
public record FuntranslationsapiSuccessResponse(Success success, Contents contents, Error error) {

  private static final Logger log = LoggerFactory.getLogger(FuntranslationsapiSuccessResponse.class);

  /** Mapper method to convert funtranslations api fetched data into the proper response */
  public String asTranslation() {
    log.info("Mapping {} into proper response", this.contents.translated());
    return this.contents.translated();
  }
}
