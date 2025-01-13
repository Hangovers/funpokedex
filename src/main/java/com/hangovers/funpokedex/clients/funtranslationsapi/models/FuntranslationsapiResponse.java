package com.hangovers.funpokedex.clients.funtranslationsapi.models;

import io.micronaut.serde.annotation.Serdeable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Serdeable
public record FuntranslationsapiResponse(Success success, Contents contents) {

  private static final Logger log = LoggerFactory.getLogger(FuntranslationsapiResponse.class);

  /** Mapper method to convert funtranslations api fetched data into the proper response */
  public String asTranslation() {
    if (this.success.total() < 1) return this.contents.text();
    log.info("Mapping {} into proper response", this.contents.translated());
    return this.contents.translated();
  }
}
