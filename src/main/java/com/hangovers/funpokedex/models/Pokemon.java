package com.hangovers.funpokedex.models;

import io.micronaut.serde.annotation.Serdeable;

@Serdeable
public record Pokemon(String name, String description, String habitat, boolean isLegendary) {}
