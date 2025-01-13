package com.hangovers.funpokedex.clients.pokeapi.models;

import io.micronaut.serde.annotation.Serdeable;

@Serdeable
public record FlavorTextEntry(String flavor_text, Language language) {}
