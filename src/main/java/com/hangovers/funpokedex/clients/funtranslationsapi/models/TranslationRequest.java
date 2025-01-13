package com.hangovers.funpokedex.clients.funtranslationsapi.models;

import io.micronaut.serde.annotation.Serdeable;

@Serdeable
public record TranslationRequest(String text) {}
