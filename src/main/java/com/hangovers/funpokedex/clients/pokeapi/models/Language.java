package com.hangovers.funpokedex.clients.pokeapi.models;

import io.micronaut.serde.annotation.Serdeable;

@Serdeable
public record Language(String name, String url) {}
